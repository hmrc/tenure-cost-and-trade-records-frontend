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

import actions.{RefNumAction, RefNumRequest, SessionRequest, WithSessionRefiner}
import connectors.Audit
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
  refNumberAction: RefNumAction,
  confirmationView: confirmation,
  audit: Audit,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  lazy val confirmationUrl = controllers.routes.FormSubmissionController.confirmation().url


//  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
//    submit(request.sessionData.referenceNumber)
//  }

  def submit: Action[AnyContent] = refNumberAction.async { implicit request: RefNumRequest[AnyContent] =>
    request.body.asFormUrlEncoded.flatMap { body =>
      body.get("declaration").map { agree =>
        if (agree.headOption.exists(_.toBoolean)) submit(request.refNum) else rejectSubmission
      }
    } getOrElse rejectSubmission
  }

  private def submit[T](refNum: String)(implicit request: SessionRequest[T]): Future[Result] = {
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    for {
      _ <- submitNotConnectedInformation(refNum)(hc, request)
    } yield Found(confirmationUrl)
  }

  def submitNotConnectedInformation(
    refNum: String
  )(implicit hc: HeaderCarrier, request: SessionRequest[_]): Future[Unit] = {
    val auditType      = "FormSubmission"
    // Dummy data from session to able creation of audit dashboards
    val submissionJson = Json.toJson(request.sessionData).as[JsObject]

    audit.sendExplicitAudit(auditType, submissionJson ++ Audit.languageJson)
    Future.unit
  }

  private def rejectSubmission = Future.successful {
    Found(routes.LoginController.show.url)
  }

//  def confirmation: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
//    Ok(confirmationView(request.sessionData.referenceNumber,request.sessionData.forType, request.sessionData.toSummary, request.sessionData))
//  }
}
