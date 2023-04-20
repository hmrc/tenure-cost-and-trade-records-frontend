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

import actions.{RefNumRequest, SessionRequest, WithSessionRefiner}
import connectors.{Audit, SubmissionConnector}
import models.submissions.NotConnectedSubmission
import controllers.FORDataCaptureController
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
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
  //  repository: FormDocumentRepository,
  submissionConnector: SubmissionConnector,
  checkYourAnswersNotConnectedView: checkYourAnswersNotConnected,
  confirmationNotConnectedView: confirmationNotConnected,
  audit: Audit,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  lazy val confirmationUrl = controllers.notconnected.routes.CheckYourAnswersNotConnectedController.confirmation().url

  val log = Logger(classOf[CheckYourAnswersNotConnectedController])

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(checkYourAnswersNotConnectedView()))
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    submit(request.sessionData.referenceNumber)
  }

  private def submit[T](refNum: String)(implicit request: SessionRequest[T]): Future[Result] = {
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    for {
      _ <- submitNotConnectedTInformation(refNum)(hc, request)
    } yield Found(confirmationUrl)
  }

  def submitNotConnectedTInformation(refNum: String)(implicit hc: HeaderCarrier, request: SessionRequest[_]): Future[Unit] = {
//      val auditType      = "NotConnectedSubmission"
//      // Dummy data from session to able creation of audit dashboards
//      val submissionJson = Json.toJson(request.sessionData).as[JsObject]
      submitToBackend()
//      audit.sendExplicitAudit(auditType, submissionJson ++ Audit.languageJson)
    Future.unit
  }

  def confirmation: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(Ok(confirmationNotConnectedView()))
  }

  private def submitToBackend()(implicit hc: HeaderCarrier, request: SessionRequest[_]): Future[Unit] = {
    val session                 = request.sessionData
    val sessionRemoveConnection = session.removeConnectionDetails

    val submission = NotConnectedSubmission (
      session.referenceNumber,
      session.address,
      sessionRemoveConnection.flatMap(_.removeConnectionDetails.map(_.removeConnectionFullName)).toString,
      sessionRemoveConnection.flatMap(_.removeConnectionDetails.map(_.removeConnectionDetails.email)),
      sessionRemoveConnection.flatMap(_.removeConnectionDetails.map(_.removeConnectionDetails.phone)),
      sessionRemoveConnection
        .flatMap(_.removeConnectionDetails.map(_.removeConnectionAdditionalInfo))
        .getOrElse(Some("")),
      Instant.now(),
      sessionRemoveConnection.flatMap(_.pastConnectionType.map(_.name)) match {
        case Some(_) => true
        case None    => false
      },
      Some(request.messages.lang.language)
    )

    submission match {
      case NotConnectedSubmission(
      submission.id,
      submission.address,
      submission.fullName,
      submission.emailAddress,
      submission.phoneNumber,
      submission.additionalInformation,
      submission.createdAt,
      submission.previouslyConnected,
      submission.lang) =>
        val auditType = "NotConnectedSubmission"
        // Dummy data from session to able creation of audit dashboards
        val submissionJson = Json.toJson(request.sessionData).as[JsObject]
        audit.sendExplicitAudit(auditType, submissionJson ++ Audit.languageJson)
      case _ =>
        val submissionJson = Json.toJson(request.sessionData).as[JsObject]
        log.error(s"Could not send data to TCTR Backend - ${request.sessionData.referenceNumber} - ${hc.sessionId}")
        audit.sendExplicitAudit("NotConnectedSubmissionFailed", submissionJson)
    }

    submissionConnector.submitNotConnected(session.referenceNumber, submission)
  }

}
