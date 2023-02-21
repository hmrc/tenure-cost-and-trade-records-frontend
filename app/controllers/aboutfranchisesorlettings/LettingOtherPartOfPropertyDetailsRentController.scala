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
import form.Form6010.LettingOtherPartOfPropertyRentForm.lettingOtherPartOfPropertyRentForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.LettingAccommodationRentDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodationRentDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LettingOtherPartOfPropertyDetailsRentController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationOrLettingAccommodationRentDetailsView: cateringOperationOrLettingAccommodationRentDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettingSections.lift(index))
    existingSection.fold(Redirect(routes.LettingOtherPartOfPropertyDetailsController.show(None))) { lettingSection =>
      val lettingDetailsForm = lettingSection.lettingOtherPartOfPropertyRentDetails.fold(
        lettingOtherPartOfPropertyRentForm
      )(lettingOtherPartOfPropertyRentForm.fill)
      Ok(
        cateringOperationOrLettingAccommodationRentDetailsView(
          lettingDetailsForm,
          index,
          "lettingOtherPartOfPropertyRentDetails",
          controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show().url
        )
      )
    }
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    lettingOtherPartOfPropertyRentForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              cateringOperationOrLettingAccommodationRentDetailsView(
                formWithErrors,
                index,
                "lettingOtherPartOfPropertyRentDetails",
                controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show().url
              )
            )
          ),
        data =>
          request.sessionData.aboutFranchisesOrLettings.fold(
            Future.successful(Redirect(routes.LettingOtherPartOfPropertyDetailsController.show(None)))
          ) { aboutFranchiseOrLettings =>
            val existingSections = aboutFranchiseOrLettings.lettingSections
            val updatedSections  = existingSections
              .updated(index, existingSections(index).copy(lettingOtherPartOfPropertyRentDetails = Some(data)))
            val updatedData      = updateAboutFranchisesOrLettings(_.copy(lettingSections = updatedSections))
            session.saveOrUpdate(updatedData)
            Future.successful(Redirect(navigator.nextPage(LettingAccommodationRentDetailsPageId).apply(updatedData)))
          }
      )
  }

}
