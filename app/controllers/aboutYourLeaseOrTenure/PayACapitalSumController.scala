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

package controllers.aboutYourLeaseOrTenure

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.PayACapitalSumForm.payACapitalSumForm
import models.ForType.*
import models.Session
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.PayACapitalSumDetails
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.PayCapitalSumId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.payACapitalSum

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PayACapitalSumController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  payACapitalSumView: payACapitalSum,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("PayACapitalSum")

    Future.successful(
      Ok(
        payACapitalSumView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.payACapitalSumDetails) match {
            case Some(data) => payACapitalSumForm.fill(data)
            case _          => payACapitalSumForm
          },
          request.sessionData.forType,
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PayACapitalSumDetails](
      payACapitalSumForm,
      formWithErrors =>
        BadRequest(
          payACapitalSumView(
            formWithErrors,
            request.sessionData.forType,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(payACapitalSumDetails = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(PayCapitalSumId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(answers: Session)(implicit request: Request[AnyContent]): String =
    navigator.from match {
      case "TL" => controllers.routes.TaskListController.show().url + "#pay-a-capital-sum"
      case _    =>
        answers.forType match {
          case FOR6020           =>
            answers.aboutLeaseOrAgreementPartThree.flatMap(_.benefitsGiven).map(_.benefitsGiven) match {
              case Some(AnswerYes) =>
                controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenDetailsController.show().url
              case Some(AnswerNo)  => controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenController.show().url
              case _               =>
                logger.warn(s"Back link for pay capital sum page reached with unknown benefits given value")
                controllers.routes.TaskListController.show().url
            }
          case FOR6045 | FOR6046 =>
            if answers.aboutLeaseOrAgreementPartFour.flatMap(_.isGivenRentFreePeriod).contains(AnswerYes) then
              controllers.aboutYourLeaseOrTenure.routes.RentFreePeriodDetailsController.show().url
            else controllers.aboutYourLeaseOrTenure.routes.IsGivenRentFreePeriodController.show().url
          case _                 =>
            answers.aboutLeaseOrAgreementPartTwo.flatMap(
              _.tenantAdditionsDisregardedDetails.map(_.tenantAdditionalDisregarded.name)
            ) match {
              case Some("yes") =>
                controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController.show().url
              case Some("no")  =>
                controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
              case _           =>
                logger.warn(
                  s"Back link for pay capital sum page reached with unknown tenants additions disregarded value"
                )
                controllers.routes.TaskListController.show().url
            }
        }
    }
}
