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

package controllers

import actions.{SessionRequest, WithSessionRefiner}
import config.ErrorHandler
import connectors.{Audit, SubmissionConnector}
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
import views.html.confirmation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FormSubmissionController @Inject() (
  mcc: MessagesControllerComponents,
  submissionConnector: SubmissionConnector,
  errorHandler: ErrorHandler,
  confirmationView: confirmation,
  audit: Audit,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with Logging
    with I18nSupport {

  import FeedbackFormMapper.feedbackForm

  lazy val confirmationUrl = controllers.routes.FormSubmissionController.confirmation().url

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    submit()
  }

  private def submit[T]()(implicit request: SessionRequest[T]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    val auditType = "FormSubmission"
    val submissionJson = Json.toJson(request.sessionData).as[JsObject]
    val session = request.sessionData

    submitToBackend(session).flatMap { _ =>
      audit.sendExplicitAudit(auditType, submissionJson ++ Audit.languageJson)
      Future.successful(
        Redirect(controllers.routes.FormSubmissionController.confirmation())
      )
    } recover { case e: Exception =>
      logger.error(s"Could not send data to HOD - ${session.referenceNumber} - ${hc.sessionId}")
      audit.sendExplicitAudit("FormSubmissionFailed", submissionJson)
      InternalServerError(errorHandler.internalServerErrorTemplate(request))
    }
  }

  def confirmation: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(confirmationView(feedbackForm))
  }

  private def submitToBackend(
                               session: Session
                             )(implicit hc: HeaderCarrier, request: SessionRequest[_]): Future[Unit] = {
    println("*" * 100)
    println("*" * 100)
    println("submitting")
    val submission = ConnectedSubmission(session)

    println(s"Submission is -> ${submission.toString}")
    println("*" * 100)
    println("*" * 100)
    submissionConnector.submitConnected(session.referenceNumber, submission)
    println(s"submitted")
    println("*" * 100)
    println("*" * 100)
  }
}
