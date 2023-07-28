/*
 * Copyright 2023 HM Revenue & Customs
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

import actions.{SessionRequest, WithSessionRefiner}
import config.ErrorHandler
import connectors.{Audit, SubmissionConnector}
import models.submissions.NotConnectedSubmission
import controllers.FORDataCaptureController
import models.Session
import play.api.{Logger, Logging}
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.notconnected.checkYourAnswersNotConnected
import views.html.confirmationNotConnected

import java.time.Instant
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersNotConnectedController @Inject() (
  mcc: MessagesControllerComponents,
  submissionConnector: SubmissionConnector,
  checkYourAnswersNotConnectedView: checkYourAnswersNotConnected,
  confirmationNotConnectedView: confirmationNotConnected,
  errorHandler: ErrorHandler,
  audit: Audit,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  lazy val confirmationUrl: String =
    controllers.notconnected.routes.CheckYourAnswersNotConnectedController.confirmation().url

  val log: Logger = Logger(classOf[CheckYourAnswersNotConnectedController])

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(Ok(checkYourAnswersNotConnectedView(request.sessionData)))
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    val auditType                  = "NotConnectedSubmission"
    val submissionJson             = Json.toJson(request.sessionData).as[JsObject]
    val session                    = request.sessionData

    submitToBackend(session).map { _ =>
      audit.sendExplicitAudit(auditType, submissionJson ++ Audit.languageJson)
      Redirect(controllers.notconnected.routes.CheckYourAnswersNotConnectedController.confirmation())
    } recover { case e: Exception =>
      logger.error(s"Could not send data to HOD - ${session.referenceNumber} - ${hc.sessionId}")
      audit.sendExplicitAudit("NotConnectedSubmissionFailed", submissionJson)
      InternalServerError(errorHandler.internalServerErrorTemplate(request))
    }
  }

  def confirmation: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(Ok(confirmationNotConnectedView(request.sessionData)))
  }

  private def submitToBackend(
    session: Session
  )(implicit hc: HeaderCarrier, request: SessionRequest[_]): Future[Unit] = {
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
      session.removeConnectionDetails.map(_.pastConnectionType.map(_.name)) match {
        case Some(_) => true
        case None    => false
      },
      Some(Audit.language)
    )

    submissionConnector.submitNotConnected(session.referenceNumber, submission)
  }

}
