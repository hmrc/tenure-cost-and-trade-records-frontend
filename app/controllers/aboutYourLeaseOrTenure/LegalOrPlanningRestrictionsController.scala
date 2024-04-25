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
import form.aboutYourLeaseOrTenure.LegalOrPlanningRestrictionsForm.legalPlanningRestrictionsForm
import models.{ForTypes, Session}
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.LegalOrPlanningRestrictions
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.LegalOrPlanningRestrictionId
import play.api.i18n.I18nSupport
import play.api.i18n.Lang.logger
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.legalOrPlanningRestrictions

import javax.inject.{Inject, Named, Singleton}

@Singleton
class LegalOrPlanningRestrictionsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  legalOrPlanningRestrictionsView: legalOrPlanningRestrictions,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      legalOrPlanningRestrictionsView(
        request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.legalOrPlanningRestrictions) match {
          case Some(data) => legalPlanningRestrictionsForm.fill(data)
          case _          => legalPlanningRestrictionsForm
        },
        getBackLink(request.sessionData),
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
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(legalOrPlanningRestrictions = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(LegalOrPlanningRestrictionId, updatedData).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session)(implicit request: Request[AnyContent]): String =
    answers.forType match {
      case ForTypes.for6020 =>
        answers.aboutLeaseOrAgreementPartTwo.flatMap(_.payACapitalSumDetails).map(_.capitalSumOrPremium) match {
          case Some(AnswerYes) => controllers.aboutYourLeaseOrTenure.routes.CapitalSumDescriptionController.show().url
          case Some(AnswerNo)  => controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
          case _               =>
            logger.warn(s"Back link for pay capital sum page reached with unknown benefits given value")
            controllers.routes.TaskListController.show().url
        }
      case _                => controllers.aboutYourLeaseOrTenure.routes.PaymentWhenLeaseIsGrantedController.show().url
    }

}
