/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.aboutYourLeaseOrTenure

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.PayACapitalSumAmountDetailsForm.payACapitalSumAmountDetailsForm
import models.Session
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.{PayACapitalSumAmountDetails, PayACapitalSumInformationDetails}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.PayCapitalSumAmountDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.payACapitalSumAmountDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PayACapitalSumAmountDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  payACapitalSumAmountDetailsView: payACapitalSumAmountDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        payACapitalSumAmountDetailsView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.payACapitalSumAmountDetails) match {
            case Some(data) => payACapitalSumAmountDetailsForm.fill(data)
            case _          => payACapitalSumAmountDetailsForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PayACapitalSumAmountDetails](
      payACapitalSumAmountDetailsForm,
      formWithErrors =>
        BadRequest(
          payACapitalSumAmountDetailsView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(payACapitalSumAmountDetails = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(PayCapitalSumAmountDetailsId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(answers: Session)(implicit request: Request[AnyContent]): String =
    navigator.from match {
      case "TL" => controllers.routes.TaskListController.show().url + "#pay-a-capital-sum-amount-details"
      case _    =>
        answers.aboutLeaseOrAgreementPartTwo.flatMap(
          _.payACapitalSumDetails.map(_.capitalSumOrPremium.name)
        ) match {
          case Some("yes") =>
            controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
          case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
          case _           =>
            logger.warn(s"Back link for pay capital sum page reached with unknown tenants additions disregarded value")
            controllers.routes.TaskListController.show().url
        }
    }
}
