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

package controllers.aboutYourLeaseOrTenure

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.PayACapitalSumDetailsForm.payACapitalSumDetailsForm
import models.Session
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.PayACapitalSumInformationDetails
import models.submissions.common.AnswersYesNo.*
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.PayCapitalSumDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.payACapitalSumDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PayACapitalSumDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  payACapitalSumDetailsView: payACapitalSumDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("PayACapitalSumDetails")

    Future.successful(
      Ok(
        payACapitalSumDetailsView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.payACapitalSumInformationDetails) match {
            case Some(data) => payACapitalSumDetailsForm.fill(data)
            case _          => payACapitalSumDetailsForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PayACapitalSumInformationDetails](
      payACapitalSumDetailsForm,
      formWithErrors =>
        BadRequest(
          payACapitalSumDetailsView(formWithErrors, getBackLink(request.sessionData), request.sessionData.toSummary)
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(payACapitalSumInformationDetails = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(PayCapitalSumDetailsId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(answers: Session)(implicit request: Request[AnyContent]): String =
    navigator.from match {
      case "TL" => controllers.routes.TaskListController.show().url + "#pay-a-capital-sum-details"
      case _    =>
        answers.aboutLeaseOrAgreementPartTwo.flatMap(
          _.payACapitalSumDetails.map(_.capitalSumOrPremium)
        ) match {
          case Some(AnswerYes) =>
            controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
          case _               => controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
        }
    }
}
