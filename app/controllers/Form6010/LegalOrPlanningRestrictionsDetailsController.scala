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

import form.Form6010.FurtherInformationOrRemarksForm.furtherInformationOrRemarksForm
import form.Form6010.LegalOrPlanningRestrictionsDetailsForm.legalOrPlanningRestrictionsDetailsForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{furtherInformationOrRemarks, legalOrPlanningRestrictionsDetails}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class LegalOrPlanningRestrictionsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  legalOrPlanningRestrictionsDetailsView: legalOrPlanningRestrictionsDetails,
  furtherInformationOrRemarksView: furtherInformationOrRemarks
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action { implicit request =>
    Ok(legalOrPlanningRestrictionsDetailsView(legalOrPlanningRestrictionsDetailsForm))
  }

  def submit = Action.async { implicit request =>
    legalOrPlanningRestrictionsDetailsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(legalOrPlanningRestrictionsDetailsView(formWithErrors))),
        data => Future.successful(Ok(furtherInformationOrRemarksView(furtherInformationOrRemarksForm)))
      )
  }
}
