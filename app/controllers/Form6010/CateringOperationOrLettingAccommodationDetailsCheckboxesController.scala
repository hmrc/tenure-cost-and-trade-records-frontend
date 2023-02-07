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
import form.Form6010.AddAnotherCateringOperationOrLettingAccommodationForm.addAnotherCateringOperationOrLettingAccommodationForm
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{addAnotherCateringOperationOrLettingAccommodation, cateringOperationOrLettingAccommodationCheckboxesDetails}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CateringOperationOrLettingAccommodationDetailsCheckboxesController @Inject() (
  mcc: MessagesControllerComponents,
  cateringOperationOrLettingAccommodationDetailsCheckboxesView: cateringOperationOrLettingAccommodationCheckboxesDetails,
  addAnotherCateringOperationOrLettingAccommodationView: addAnotherCateringOperationOrLettingAccommodation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        cateringOperationOrLettingAccommodationDetailsCheckboxesView(
          index,
          "cateringOperationOrLettingAccommodationCheckboxesDetails",
          controllers.Form6010.routes.CateringOperationOrLettingAccommodationDetailsRentController.show(index).url
        )
      )
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        addAnotherCateringOperationOrLettingAccommodationView(
          addAnotherCateringOperationOrLettingAccommodationForm,
          index,
          "addAnotherCateringOperationOrLettingAccommodation",
          controllers.Form6010.routes.CateringOperationOrLettingAccommodationDetailsCheckboxesController.show(index).url
        )
      )
    )
  }

}
