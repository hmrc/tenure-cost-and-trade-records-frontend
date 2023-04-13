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
import connectors.Audit
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.notconnected.checkYourAnswersNotConnected
import views.html.confirmationNotConnected

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersNotConnectedController @Inject() (
  mcc: MessagesControllerComponents,
  checkYourAnswersNotConnectedView: checkYourAnswersNotConnected,
  confirmationNotConnectedView: confirmationNotConnected,
//  audit: Audit,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  lazy val confirmationUrl = controllers.routes.FormSubmissionController.confirmation().url

  val log                      = Logger(classOf[CheckYourAnswersNotConnectedController])
  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(checkYourAnswersNotConnectedView()))
  }
//
//  def submit = Action.async { implicit request =>
//    Future.successful(Ok(confirmationNotConnectedView()))
//  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    submit(request.sessionData.referenceNumber)
  }

  private def submit[T](refNum: String)(implicit request: SessionRequest[T]): Future[Result] = {
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    for {
      _ <- submitNotConnectedTInformation(refNum)(hc, request)
    } yield Found(confirmationUrl)
  }

  def submitNotConnectedTInformation(
    refNum: String
  )(implicit hc: HeaderCarrier, request: SessionRequest[_]): Future[Unit] = {
//    val auditType = "FormSubmission"
    // Dummy data from session to able creation of audit dashboards
//    val submissionJson = Json.toJson(request.sessionData).as[JsObject]
    log.warn("*****---*****")
//    audit.sendExplicitAudit(auditType, submissionJson ++ Audit.languageJson)
    Future.unit
  }

  def confirmation: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(Ok(confirmationNotConnectedView()))
  }

}
