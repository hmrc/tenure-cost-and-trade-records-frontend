/*
 * Copyright 2022 HM Revenue & Customs
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

import controllers.LoginController.loginForm
import form.Form6010.LegalOrPlanningRestrictionsForm.legalPlanningRestrictionsForm
import form.Form6010.LegalOrPlanningRestrictionsDetailsForm.legalOrPlanningRestrictionsDetailsForm
import form.Form6010.FurtherInformationOrRemarksForm.furtherInformationOrRemarksForm
import models.submissions.Form6010.{LegalPlanningRestrictionsNo, LegalPlanningRestrictionsYes}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.Form6010.{furtherInformationOrRemarks, legalOrPlanningRestrictions, legalOrPlanningRestrictionsDetails}
import views.html.login

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class LegalOrPlanningRestrictionsController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  legalOrPlanningRestrictionsView: legalOrPlanningRestrictions,
  legalOrPlanningRestrictionsDetailsView: legalOrPlanningRestrictionsDetails,
  furtherInformationOrRemarksView: furtherInformationOrRemarks
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action { implicit request =>
    Ok(legalOrPlanningRestrictionsView(legalPlanningRestrictionsForm))
  }

  def submit = Action.async { implicit request =>
    legalPlanningRestrictionsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(legalOrPlanningRestrictionsView(formWithErrors))),
        data =>
          data.legalPlanningRestrictions match {
            case LegalPlanningRestrictionsYes =>
              Future.successful(
                Ok(legalOrPlanningRestrictionsDetailsView(legalOrPlanningRestrictionsDetailsForm))
              )
            case LegalPlanningRestrictionsNo  =>
              Future.successful(
                Ok(furtherInformationOrRemarksView(furtherInformationOrRemarksForm))
              )
          }
      )
  }
}
