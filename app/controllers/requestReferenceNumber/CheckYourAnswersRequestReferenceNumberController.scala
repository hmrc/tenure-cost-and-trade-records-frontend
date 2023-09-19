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
import connectors.Audit
import controllers.FORDataCaptureController
import form.requestReferenceNumber.CheckYourAnswersRequestReferenceNumberForm.checkYourAnswersRequestReferenceNumberForm

import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.requestReferenceNumber.{checkYourAnswersRequestReferenceNumber, confirmationRequestReferenceNumber}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersRequestReferenceNumberController @Inject() (
  mcc: MessagesControllerComponents,
  checkYourAnswersRequestReferenceNumberView: checkYourAnswersRequestReferenceNumber,
  confirmationRequestReferenceNumberView: confirmationRequestReferenceNumber,
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
              checkYourAnswersRequestReferenceNumberForm.fillAndValidate(checkYourAnswersRequestReferenceNumber)
            case _                                            => checkYourAnswersRequestReferenceNumberForm
          },
          request.sessionData
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    submit(request.sessionData.referenceNumber)
  }

  private def submit[T](refNum: String)(implicit request: SessionRequest[T]): Future[Result] = {
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    for {
      _ <- submitRequestReferenceNumber(refNum)(hc, request)
    } yield Found(confirmationUrl)
  }

  def submitRequestReferenceNumber(
    refNum: String
  )(implicit hc: HeaderCarrier, request: SessionRequest[_]): Future[Unit] = {
    val auditType      = "noReference"
    // Dummy data from session to able creation of audit dashboards
    val submissionJson = Json.toJson(request.sessionData).as[JsObject]

    audit.sendExplicitAudit(auditType, submissionJson ++ Audit.languageJson)
    Future.unit
  }

  def confirmation: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future(Ok(confirmationRequestReferenceNumberView(feedbackForm)))
  }

}
