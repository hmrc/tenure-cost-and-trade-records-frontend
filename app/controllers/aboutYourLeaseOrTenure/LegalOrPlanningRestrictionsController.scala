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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.{FORDataCaptureController, aboutYourLeaseOrTenure}
import form.aboutYourLeaseOrTenure.LegalOrPlanningRestrictionsForm.legalPlanningRestrictionsForm
import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.LegalOrPlanningRestrictions
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.LegalOrPlanningRestrictionId
import play.api.i18n.I18nSupport
import play.api.i18n.Lang.logger
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.legalOrPlanningRestrictions

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LegalOrPlanningRestrictionsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  legalOrPlanningRestrictionsView: legalOrPlanningRestrictions,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("LegalOrPlanningRestrictions")

    Ok(
      legalOrPlanningRestrictionsView(
        request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.legalOrPlanningRestrictions) match {
          case Some(data) => legalPlanningRestrictionsForm.fill(data)
          case _          => legalPlanningRestrictionsForm
        },
        getBackLink,
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LegalOrPlanningRestrictions](
      legalPlanningRestrictionsForm,
      formWithErrors =>
        BadRequest(
          legalOrPlanningRestrictionsView(
            formWithErrors,
            getBackLink,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(legalOrPlanningRestrictions = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(LegalOrPlanningRestrictionId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    request.sessionData.forType match {
      case FOR6020           =>
        request.sessionData.aboutLeaseOrAgreementPartTwo
          .flatMap(_.payACapitalSumDetails)
          .map(_.capitalSumOrPremium) match {
          case Some(AnswerYes) => aboutYourLeaseOrTenure.routes.CapitalSumDescriptionController.show().url
          case Some(AnswerNo)  => aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
          case _               =>
            logger.warn(s"Back link for pay capital sum page reached with unknown benefits given value")
            controllers.routes.TaskListController.show().url
        }
      case FOR6045 | FOR6046 =>
        request.sessionData.aboutLeaseOrAgreementPartTwo
          .flatMap(_.payACapitalSumDetails)
          .map(_.capitalSumOrPremium) match {
          case Some(AnswerYes) => aboutYourLeaseOrTenure.routes.CapitalSumDescriptionController.show().url
          case _               => aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
        }
      case FOR6048           =>
        request.sessionData.aboutLeaseOrAgreementPartTwo
          .flatMap(_.payACapitalSumDetails)
          .map(_.capitalSumOrPremium) match {
          case Some(AnswerYes) => aboutYourLeaseOrTenure.routes.PayACapitalSumAmountDetailsController.show().url
          case _               => aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
        }
      case _                 => aboutYourLeaseOrTenure.routes.PaymentWhenLeaseIsGrantedController.show().url
    }

}
