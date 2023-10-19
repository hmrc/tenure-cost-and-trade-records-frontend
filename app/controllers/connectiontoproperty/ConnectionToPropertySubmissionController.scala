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

package controllers.connectiontoproperty
import actions.{SessionRequest, WithSessionRefiner}
import config.ErrorHandler
import connectors.{Audit, SubmissionConnector}
import controllers.FeedbackFormMapper
import models.Session
import models.submissions.ConnectedSubmission
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.connectiontoproperty.confirmationVacantProperty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConnectionToPropertySubmissionController @Inject() (
  mcc: MessagesControllerComponents,
  submissionConnector: SubmissionConnector,
  errorHandler: ErrorHandler,
  vacantPropertyView: confirmationVacantProperty,
  audit: Audit,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  import FeedbackFormMapper.feedbackForm

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    submit()
  }

  private def submit[T]()(implicit request: SessionRequest[T]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    val auditType                  = "VacantFormSubmission"
    val submissionJson             = Json.toJson(request.sessionData).as[JsObject]
    val session                    = request.sessionData
    submitToBackend(session).flatMap { _ =>
      val outcome = Json.obj("isSuccessful" -> true)
      audit.sendExplicitAudit(auditType, submissionJson ++ Audit.languageJson ++ Json.obj("outcome" -> outcome))
      Future.successful(
        Redirect(controllers.connectiontoproperty.routes.ConnectionToPropertySubmissionController.confirmation())
      )
    } recover { case e: Exception =>
      val failureReason = s"Could not send data to HOD - ${session.referenceNumber} - ${hc.sessionId.getOrElse("")}"
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
    Future(Ok(vacantPropertyView(feedbackForm)))
  }

  private def submitToBackend(
    session: Session
  )(implicit hc: HeaderCarrier, request: SessionRequest[_]): Future[Unit] = {

    val submission = ConnectedSubmission(session)

    submissionConnector.submitConnected(session.referenceNumber, submission)
  }
}
