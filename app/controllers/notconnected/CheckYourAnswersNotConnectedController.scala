/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.notconnected

import actions.WithSessionRefiner
import config.ErrorHandler
import connectors.{Audit, SubmissionConnector}
import controllers.{FORDataCaptureController, FeedbackFormMapper}
import models.Session
import models.submissions.NotConnectedSubmission
import models.submissions.common.AnswersYesNo.*
import play.api.Logging
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import views.html.confirmation
import views.html.notconnected.checkYourAnswersNotConnected

import java.time.Instant
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersNotConnectedController @Inject() (
  mcc: MessagesControllerComponents,
  submissionConnector: SubmissionConnector,
  checkYourAnswersNotConnectedView: checkYourAnswersNotConnected,
  confirmationView: confirmation,
  errorHandler: ErrorHandler,
  audit: Audit,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  import FeedbackFormMapper.feedbackForm

  lazy val confirmationUrl: String =
    controllers.notconnected.routes.CheckYourAnswersNotConnectedController.confirmation().url

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(Ok(checkYourAnswersNotConnectedView(request.sessionData)))
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val auditType      = "NotConnectedSubmission"
    val submissionJson = Json.toJson(request.sessionData).as[JsObject]
    val session        = request.sessionData
    val sessionId      = summon[HeaderCarrier].sessionId.getOrElse("")

    submitToBackend(session).map { _ =>
      val outcome = Json.obj("isSuccessful" -> true)
      audit.sendExplicitAudit(auditType, submissionJson ++ Audit.languageJson ++ Json.obj("outcome" -> outcome))
      Found(confirmationUrl)
    } recoverWith { case e: Exception =>
      val failureReason = s"Could not send data to HOD - ${session.referenceNumber} - $sessionId"
      logger.error(failureReason, e)
      val outcome       = Json.obj(
        "isSuccessful"    -> false,
        "failureCategory" -> INTERNAL_SERVER_ERROR,
        "failureReason"   -> failureReason
      )
      audit.sendExplicitAudit(auditType, submissionJson ++ Json.obj("outcome" -> outcome))
      errorHandler.internalServerErrorTemplate(using request).map(InternalServerError(_))
    }
  }

  def confirmation: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future(Ok(confirmationView(feedbackForm)))
  }

  private def submitToBackend(
    session: Session
  )(implicit hc: HeaderCarrier, messages: Messages): Future[Unit] = {
    val sessionRemoveConnection = session.removeConnectionDetails.flatMap(_.removeConnectionDetails)

    val submission = NotConnectedSubmission(
      session.referenceNumber,
      session.forType,
      session.address,
      sessionRemoveConnection.map(_.removeConnectionFullName).getOrElse(""),
      sessionRemoveConnection.map(_.removeConnectionDetails.email),
      sessionRemoveConnection.map(_.removeConnectionDetails.phone),
      sessionRemoveConnection.map(_.removeConnectionAdditionalInfo).getOrElse(Some("")),
      Instant.now(),
      session.removeConnectionDetails.flatMap(_.pastConnectionType).contains(AnswerYes),
      Some(messages.lang.language)
    )

    submissionConnector.submitNotConnected(session.referenceNumber, submission).map(_ => ())
  }

}
