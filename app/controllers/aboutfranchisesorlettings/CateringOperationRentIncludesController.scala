/*
 * Copyright 2024 HM Revenue & Customs
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
import form.aboutfranchisesorlettings.IncomeRecordIncludedForm.incomeRecordIncludedForm as theForm
import models.ForType
import models.ForType.*
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
  audit: Audit,
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
        val rentIncludesForm = theForm.fill(currentSection.itemsInRent)
        audit.sendChangeLink("CateringOperationRentIncludes")
        Ok(
          cateringOperationOrLettingAccommodationDetailsCheckboxesView(
            rentIncludesForm,
            index,
            "cateringOperationOrLettingAccommodationCheckboxesDetails",
            currentSection.cateringOperationDetails.operatorName,
            backlink(request.sessionData.forType, index)
          )
        )
      }
  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    (for {
      existingSections <- request.sessionData.aboutFranchisesOrLettings.map(_.cateringOperationSections)
      currentSection   <- existingSections.lift(index)
    } yield continueOrSaveAsDraft[List[String]](
      theForm,
      formWithErrors =>
        BadRequest(
          cateringOperationOrLettingAccommodationDetailsCheckboxesView(
            formWithErrors,
            index,
            "cateringOperationOrLettingAccommodationCheckboxesDetails",
            currentSection.cateringOperationDetails.operatorName,
            backlink(request.sessionData.forType, index)
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

  def backlink(forType: ForType, index: Int): String = {
    val isForType6016Or6015 = forType == FOR6016 || forType == FOR6015

    if isForType6016Or6015 then
      controllers.aboutfranchisesorlettings.routes.CalculatingTheRentForController.show(index).url
    else controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsRentController.show(index).url
  }

  private def startRedirect: Result = Redirect(routes.CateringOperationDetailsController.show(None))

}
