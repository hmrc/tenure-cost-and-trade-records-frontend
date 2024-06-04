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

package controllers.requestReferenceNumber

import actions.{SessionRequest, WithSessionRefiner}
import config.ErrorHandler
import connectors.{Audit, SubmissionConnector}
import controllers.FORDataCaptureController
import form.requestReferenceNumber.CheckYourAnswersRequestReferenceNumberForm.checkYourAnswersRequestReferenceNumberForm
import models.Session
import models.submissions.RequestReferenceNumberSubmission
import play.api.Logging
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.requestReferenceNumber.{checkYourAnswersRequestReferenceNumber, confirmationRequestReferenceNumber}

import java.time.Instant
import java.util.UUID
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersRequestReferenceNumberController @Inject() (
  mcc: MessagesControllerComponents,
  submissionConnector: SubmissionConnector,
  checkYourAnswersRequestReferenceNumberView: checkYourAnswersRequestReferenceNumber,
  confirmationRequestReferenceNumberView: confirmationRequestReferenceNumber,
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
    controllers.requestReferenceNumber.routes.CheckYourAnswersRequestReferenceNumberController.confirmation().url

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersRequestReferenceNumberView(
          request.sessionData.requestReferenceNumberDetails.flatMap(_.checkYourAnswersRequestReferenceNumber) match {
            case Some(checkYourAnswersRequestReferenceNumber) =>
              checkYourAnswersRequestReferenceNumberForm.fill(checkYourAnswersRequestReferenceNumber)
            case _                                            => checkYourAnswersRequestReferenceNumberForm
          },
          request.sessionData
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    submitRequestReferenceNumber()(hc, request)
  }

  def submitRequestReferenceNumber()(implicit hc: HeaderCarrier, request: SessionRequest[_]): Future[Result] = {
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
    } recover { case e: Exception =>
      val failureReason = s"Could not send request reference number data to HOD - ${hc.sessionId.getOrElse("")}"
      logger.error(failureReason)
      val outcome       = Json.obj(
        "isSuccessful"    -> false,
        "failureCategory" -> INTERNAL_SERVER_ERROR,
        "failureReason"   -> failureReason
      )
      audit.sendExplicitAudit(auditType, submissionJson ++ Json.obj("outcome" -> outcome))
      InternalServerError(errorHandler.internalServerErrorTemplate(request))
    }
  }

  def confirmation: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future(Ok(confirmationRequestReferenceNumberView(feedbackForm)))
  }

  private def submitRequestRefNumToBackend(
    session: Session
  )(implicit hc: HeaderCarrier, messages: Messages): Future[Unit] = {
    val sessionRequestRefNum        = session.requestReferenceNumberDetails
    val sessionRequestRefNumAddress = sessionRequestRefNum.flatMap(_.requestReferenceNumberAddress)
    val sessionRequestRefNumDetails = sessionRequestRefNum.flatMap(_.requestReferenceContactDetails)

    val submission = RequestReferenceNumberSubmission(
      UUID.randomUUID.toString,
      sessionRequestRefNumAddress.map(_.requestReferenceNumberBusinessTradingName).getOrElse(""),
      sessionRequestRefNumAddress.map(_.requestReferenceNumberAddress).get,
      sessionRequestRefNumDetails.map(_.requestReferenceNumberContactDetailsFullName).getOrElse(""),
      sessionRequestRefNumDetails.map(_.requestReferenceNumberContactDetails).get,
      sessionRequestRefNumDetails.map(_.requestReferenceNumberContactDetailsAdditionalInformation).get,
      Instant.now(),
      Some(messages.lang.language)
    )

    submissionConnector.submitRequestReferenceNumber(submission)
  }
}
