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
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.IncomeRecordIncludedForm.incomeRecordIncludedForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.LettingAccommodationRentIncludesPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodationRentIncludes

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LettingOtherPartOfPropertyRentIncludesController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationOrLettingAccommodationRentIncludesView: cateringOperationOrLettingAccommodationRentIncludes,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutFranchisesOrLettings
      .flatMap(_.lettingSections.lift(index))
      .fold(
        startRedirect
      ) { currentSection =>
        val rentIncludesForm = incomeRecordIncludedForm.fill(currentSection.itemsInRent)
        audit.sendChangeLink("LettingOtherPartOfPropertyRentIncludes")

        Ok(
          cateringOperationOrLettingAccommodationRentIncludesView(
            rentIncludesForm,
            index,
            "lettingOtherPartOfPropertyCheckboxesDetails",
            currentSection.lettingOtherPartOfPropertyInformationDetails.operatorName,
            controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsRentController
              .show(index)
              .url,
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        )
      }
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    (for {
      existingSections <- request.sessionData.aboutFranchisesOrLettings.map(_.lettingSections)
      currentSection   <- existingSections.lift(index)
    } yield continueOrSaveAsDraft[List[String]](
      incomeRecordIncludedForm,
      formWithErrors =>
        BadRequest(
          cateringOperationOrLettingAccommodationRentIncludesView(
            formWithErrors,
            index,
            "lettingOtherPartOfPropertyCheckboxesDetails",
            currentSection.lettingOtherPartOfPropertyInformationDetails.operatorName,
            controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsRentController
              .show(index)
              .url,
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      data => {
        val updatedSections = existingSections.updated(
          index,
          currentSection.copy(itemsInRent = data)
        )
        val updatedSession  = updateAboutFranchisesOrLettings(_.copy(lettingSections = updatedSections))
        session.saveOrUpdate(updatedSession).map { _ =>
          Redirect(navigator.nextPage(LettingAccommodationRentIncludesPageId, updatedSession).apply(updatedSession))
        }
      }
    )).getOrElse(startRedirect)
  }

  private def startRedirect: Result = Redirect(routes.CateringOperationDetailsController.show(None))

}
