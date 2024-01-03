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

package controllers.aboutfranchisesorlettings

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.CalculatingTheRentForm.calculatingTheRentForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.CalculatingTheRent
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.CalculatingTheRentForPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.calculatingTheRentFor

import javax.inject.{Inject, Named}
import scala.concurrent.ExecutionContext

class CalculatingTheRentForController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  calculatingTheRentForView: calculatingTheRentFor,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.flatMap(
      _.cateringOperationSections.lift(index)
    )
    existingSection.fold(
      Redirect(routes.CateringOperationDetailsController.show(None))
    ) { cateringOperationOrLettingAccommodationSection =>
      val rentDetailsForm =
        cateringOperationOrLettingAccommodationSection.calculatingTheRent.fold(
          calculatingTheRentForm
        )(calculatingTheRentForm.fill)
      Ok(
        calculatingTheRentForView(
          rentDetailsForm,
          index,
          existingSection.get.cateringOperationDetails.operatorName,
          request.sessionData.toSummary
        )
      )
    }
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.map(_.cateringOperationSections).get(index)

    continueOrSaveAsDraft[CalculatingTheRent](
      calculatingTheRentForm,
      formWithErrors =>
        BadRequest(
          calculatingTheRentForView(
            formWithErrors,
            index,
            existingSection.cateringOperationDetails.operatorName,
            request.sessionData.toSummary
          )
        ),
      data =>
        request.sessionData.aboutFranchisesOrLettings.fold(
          Redirect(routes.CateringOperationDetailsController.show(None))
        ) { aboutFranchisesOrLettings =>
          val existingSections = aboutFranchisesOrLettings.cateringOperationSections
          val updatedSections  = existingSections.updated(
            index,
            existingSections(index).copy(calculatingTheRent = Some(data))
          )
          val updatedData      = updateAboutFranchisesOrLettings(_.copy(cateringOperationSections = updatedSections))
          session.saveOrUpdate(updatedData)
          Redirect(navigator.nextPage(CalculatingTheRentForPageId, updatedData).apply(updatedData))
        }
    )
  }
}
