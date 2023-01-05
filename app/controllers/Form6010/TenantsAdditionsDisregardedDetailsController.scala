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

import form.Form6010.LegalOrPlanningRestrictionsForm.legalPlanningRestrictionsForm
import form.Form6010.TenantsAdditionsDisregardedDetailsForm.tenantsAdditionsDisregardedDetailsForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{legalOrPlanningRestrictions, tenantsAdditionsDisregardedDetails}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class TenantsAdditionsDisregardedDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  legalOrPlanningRestrictionsView: legalOrPlanningRestrictions,
  tenantsAdditionsDisregardedDetailsView: tenantsAdditionsDisregardedDetails
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action { implicit request =>
    Ok(tenantsAdditionsDisregardedDetailsView(tenantsAdditionsDisregardedDetailsForm))
  }

  def submit = Action.async { implicit request =>
    tenantsAdditionsDisregardedDetailsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(tenantsAdditionsDisregardedDetailsView(formWithErrors))),
        data => Future.successful(Ok(legalOrPlanningRestrictionsView(legalPlanningRestrictionsForm)))
      )
  }
}
