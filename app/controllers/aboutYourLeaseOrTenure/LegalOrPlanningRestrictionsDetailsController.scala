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
import form.aboutYourLeaseOrTenure.LegalOrPlanningRestrictionsDetailsForm.legalOrPlanningRestrictionsDetailsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.LegalOrPlanningRestrictionsDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.LegalOrPlanningRestrictionDetailsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.legalOrPlanningRestrictionsDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LegalOrPlanningRestrictionsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  legalOrPlanningRestrictionsDetailsView: legalOrPlanningRestrictionsDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      legalOrPlanningRestrictionsDetailsView(
        request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.legalOrPlanningRestrictionsDetails) match {
          case Some(data) => legalOrPlanningRestrictionsDetailsForm.fill(data)
          case _          => legalOrPlanningRestrictionsDetailsForm
        },
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LegalOrPlanningRestrictionsDetails](
      legalOrPlanningRestrictionsDetailsForm,
      formWithErrors =>
        BadRequest(legalOrPlanningRestrictionsDetailsView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(legalOrPlanningRestrictionsDetails = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(LegalOrPlanningRestrictionDetailsId, updatedData).apply(updatedData)))

      }
    )
  }

}
