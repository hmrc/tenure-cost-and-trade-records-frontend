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

import controllers.LoginController.loginForm
import form.Form6010.TenantsAdditionsDisregardedDetailsForm.tenantsAdditionsDisregardedDetailsForm
import form.Form6010.TenantsAdditionsDisregardedForm.tenantsAdditionsDisregardedForm
import form.Form6010.LegalOrPlanningRestrictionsForm.legalPlanningRestrictionsForm
import models.submissions.Form6010.{TenantsAdditionsDisregardedNo, TenantsAdditionsDisregardedYes}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{legalOrPlanningRestrictions, tenantsAdditionsDisregarded, tenantsAdditionsDisregardedDetails}
import views.html.login

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class TenantsAdditionsDisregardedController @Inject() (
  mcc: MessagesControllerComponents,
  tenantsAdditionsDisregardedDetailsView: tenantsAdditionsDisregardedDetails,
  tenantsAdditionsDisregardedView: tenantsAdditionsDisregarded,
  legalOrPlanningRestrictionsView: legalOrPlanningRestrictions,
  login: login
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action { implicit request =>
    Ok(tenantsAdditionsDisregardedView(tenantsAdditionsDisregardedForm))
  }

  def submit = Action.async { implicit request =>
    tenantsAdditionsDisregardedForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(tenantsAdditionsDisregardedView(formWithErrors))),
        data =>
          data.tenantAdditionalDisregarded match {
            case TenantsAdditionsDisregardedYes =>
              Future.successful(
                Ok(tenantsAdditionsDisregardedDetailsView(tenantsAdditionsDisregardedDetailsForm))
              )
            case TenantsAdditionsDisregardedNo  =>
              Future.successful(Ok(legalOrPlanningRestrictionsView(legalPlanningRestrictionsForm)))
            case _                              => Future.successful(Ok(login(loginForm)))

          }
      )
  }
}
