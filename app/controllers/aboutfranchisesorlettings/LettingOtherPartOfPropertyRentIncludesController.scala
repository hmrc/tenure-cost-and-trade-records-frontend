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
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.LettingAccommodationRentIncludesPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodationRentIncludes

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class LettingOtherPartOfPropertyRentIncludesController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationOrLettingAccommodationRentIncludesView: cateringOperationOrLettingAccommodationRentIncludes,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettingSections.lift(index))
    Future.successful(
      Ok(
        cateringOperationOrLettingAccommodationRentIncludesView(
          index,
          "lettingOtherPartOfPropertyCheckboxesDetails",
          existingSection.get.lettingOtherPartOfPropertyInformationDetails.operatorName,
          controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsRentController.show(index).url,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft(
      Redirect(navigator.nextPage(LettingAccommodationRentIncludesPageId, request.sessionData).apply(request.sessionData))
    )
  }

}
