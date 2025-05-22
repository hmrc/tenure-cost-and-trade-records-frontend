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

package controllers.requestReferenceNumber

import actions.{SessionRequest, WithSessionRefiner}
import config.ErrorHandler
import connectors.{Audit, SubmissionConnector}
import controllers.FORDataCaptureController
import form.requestReferenceNumber.RequestReferenceNumberCheckYourAnswersForm.theForm
import models.Session
import models.submissions.RequestReferenceNumberSubmission
import play.api.Logging
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.requestReferenceNumber.requestReferenceNumberCheckYourAnswers as RequestReferenceNumberCheckYourAnswersView
import views.html.requestReferenceNumber.requestReferenceNumberConfirmation as RequestReferenceNumberConfirmationView

import java.time.Instant
import java.util.UUID
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RequestReferenceNumberCheckYourAnswersController @Inject() (
  mcc: MessagesControllerComponents,
  submissionConnector: SubmissionConnector,
  checkYourAnswersView: RequestReferenceNumberCheckYourAnswersView,
  confirmationView: RequestReferenceNumberConfirmationView,
  errorHandler: ErrorHandler,
  audit: Audit,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  import controllers.FeedbackFormMapper.feedbackForm

  lazy val confirmationUrl: String =
    controllers.requestReferenceNumber.routes.RequestReferenceNumberCheckYourAnswersController.confirmation().url

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersView(
          request.sessionData.requestReferenceNumberDetails.flatMap(_.checkYourAnswers) match {
            case Some(cya) => theForm.fill(cya)
            case _         => theForm
          },
          request.sessionData
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    submitRequestReferenceNumber()(using hc, request)
  }

  def submitRequestReferenceNumber()(implicit hc: HeaderCarrier, request: SessionRequest[?]): Future[Result] = {
    val auditType      = "NoReferenceSubmission"
    val session        = request.sessionData
    val submissionJson = Json.toJson(request.sessionData).as[JsObject]

    submitRequestRefNumToBackend(session).map { _ =>
      val outcome = Json.obj("isSuccessful" -> true)
      audit.sendExplicitAudit(
        auditType,
        submissionJson ++ Audit.languageJson ++ Json.obj("outcome" -> outcome)
      )
      Redirect(confirmationUrl)
    } recoverWith { case e: Exception =>
      val failureReason = s"Could not send request reference number data to HOD - ${hc.sessionId.getOrElse("")}"
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

  private def submitRequestRefNumToBackend(
    session: Session
  )(implicit hc: HeaderCarrier, messages: Messages): Future[Unit] = {
    val sessionRequestRefNum        = session.requestReferenceNumberDetails
    val sessionRequestRefNumAddress = sessionRequestRefNum.flatMap(_.propertyDetails)
    val sessionRequestRefNumDetails = sessionRequestRefNum.flatMap(_.contactDetails)

    val submission = RequestReferenceNumberSubmission(
      UUID.randomUUID.toString,
      sessionRequestRefNumAddress.map(_.businessTradingName).getOrElse(""),
      sessionRequestRefNumAddress.map(_.address).get.get,
      sessionRequestRefNumDetails.map(_.noReferenceNumberFullName).getOrElse(""),
      sessionRequestRefNumDetails.map(_.noReferenceNumberContactDetails).get,
      sessionRequestRefNumDetails.map(_.noReferenceNumberAdditionalInfo).get,
      Instant.now(),
      Some(messages.lang.language)
    )

    submissionConnector.submitRequestReferenceNumber(submission).map(_ => ())
  }

}
