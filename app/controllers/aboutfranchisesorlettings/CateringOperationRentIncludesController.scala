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
import form.aboutfranchisesorlettings.CateringOperationOrLettingAccommodationRentIncludesForm.cateringOperationOrLettingAccommodationRentIncludesForm
import models.ForTypes
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.CateringOperationRentIncludesPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodationRentIncludes

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CateringOperationRentIncludesController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationOrLettingAccommodationDetailsCheckboxesView: cateringOperationOrLettingAccommodationRentIncludes,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutFranchisesOrLettings
      .flatMap(_.cateringOperationSections.lift(index))
      .fold(
        startRedirect
      ) { currentSection =>
        val rentIncludesForm = cateringOperationOrLettingAccommodationRentIncludesForm.fill(currentSection.itemsInRent)

        Ok(
          cateringOperationOrLettingAccommodationDetailsCheckboxesView(
            rentIncludesForm,
            index,
            "cateringOperationOrLettingAccommodationCheckboxesDetails",
            currentSection.cateringOperationDetails.operatorName,
            backlink(request.sessionData.forType, index),
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        )
      }
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    (for {
      existingSections <- request.sessionData.aboutFranchisesOrLettings.map(_.cateringOperationSections)
      currentSection   <- existingSections.lift(index)
    } yield continueOrSaveAsDraft[List[String]](
      cateringOperationOrLettingAccommodationRentIncludesForm,
      formWithErrors =>
        BadRequest(
          cateringOperationOrLettingAccommodationDetailsCheckboxesView(
            formWithErrors,
            index,
            "cateringOperationOrLettingAccommodationCheckboxesDetails",
            currentSection.cateringOperationDetails.operatorName,
            backlink(request.sessionData.forType, index),
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      data => {
        val updatedSections = existingSections.updated(
          index,
          currentSection.copy(itemsInRent = data)
        )
        val updatedSession  = updateAboutFranchisesOrLettings(_.copy(cateringOperationSections = updatedSections))
        session.saveOrUpdate(updatedSession).map { _ =>
          Redirect(navigator.nextPage(CateringOperationRentIncludesPageId, updatedSession).apply(updatedSession))
        }
      }
    )).getOrElse(startRedirect)
  }

  def backlink(forType: String, index: Int): String = {
    val isForType6016Or6015 = forType == ForTypes.for6016 || forType == ForTypes.for6015

    isForType6016Or6015 match {
      case true  =>
        controllers.aboutfranchisesorlettings.routes.CalculatingTheRentForController.show(index).url
      case false =>
        controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsRentController.show(index).url
    }
  }

  private def startRedirect: Result = Redirect(routes.CateringOperationDetailsController.show(None))

}
