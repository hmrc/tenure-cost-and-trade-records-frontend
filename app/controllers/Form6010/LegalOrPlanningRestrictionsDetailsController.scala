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

package controllers.Form6010

import actions.WithSessionRefiner
import form.additionalinformation.FurtherInformationOrRemarksForm.furtherInformationOrRemarksForm
import form.Form6010.LegalOrPlanningRestrictionsDetailsForm.legalOrPlanningRestrictionsDetailsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.legalOrPlanningRestrictionsDetails
import views.html.additionalinformation.furtherInformationOrRemarks

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class LegalOrPlanningRestrictionsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  legalOrPlanningRestrictionsDetailsView: legalOrPlanningRestrictionsDetails,
  furtherInformationOrRemarksView: furtherInformationOrRemarks,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      legalOrPlanningRestrictionsDetailsView(
        request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.legalOrPlanningRestrictionsDetails) match {
          case Some(data) => legalOrPlanningRestrictionsDetailsForm.fillAndValidate(data)
          case _          => legalOrPlanningRestrictionsDetailsForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    legalOrPlanningRestrictionsDetailsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(legalOrPlanningRestrictionsDetailsView(formWithErrors))),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(legalOrPlanningRestrictionsDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(
            Ok(
              furtherInformationOrRemarksView(
                furtherInformationOrRemarksForm,
                controllers.Form6010.routes.TenantsAdditionsDisregardedController.show().url
              )
            )
          )
        }
      )
  }
}
