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
import form.aboutYourLeaseOrTenure.CurrentAnnualRentForm.currentAnnualRentForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.common.{AnswerNo, AnswerYes}
import models.{AnnualRent, Session}
import models.ForType.*
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.CurrentAnnualRentPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.NumberUtil.zeroBigDecimal
import views.html.aboutYourLeaseOrTenure.currentAnnualRent

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CurrentAnnualRentController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  currentAnnualRentView: currentAnnualRent,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("CurrentAnnualRent")
    Ok(
      currentAnnualRentView(
        request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.annualRent) match {
          case Some(annualRent) => currentAnnualRentForm().fill(annualRent)
          case _                => currentAnnualRentForm()
        },
        getBackLink(request.sessionData),
        request.sessionData.toSummary
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val includedPartsSum = request.sessionData.aboutLeaseOrAgreementPartOne
      .fold(zeroBigDecimal)(leaseOrAgreement1 =>
        Seq(
          leaseOrAgreement1.rentIncludeTradeServicesInformation.flatMap(_.sumIncludedInRent),
          leaseOrAgreement1.rentIncludeFixtureAndFittingsDetails.flatMap(_.sumIncludedInRent)
        ).flatten.sum
      )

    continueOrSaveAsDraft[AnnualRent](
      currentAnnualRentForm(includedPartsSum),
      formWithErrors =>
        BadRequest(
          currentAnnualRentView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(annualRent = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(CurrentAnnualRentPageId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.forType match {
      case FOR6011 =>
        answers.aboutLeaseOrAgreementPartOne.flatMap(_.connectedToLandlord) match {
          case Some(AnswerYes) =>
            controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show().url
          case Some(AnswerNo)  => controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show().url
          case _               =>
            logger.warn(s"Back link for current annual rent page reached with unknown value")
            controllers.routes.TaskListController.show().url
        }
      case _       => controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show().url
    }
}
