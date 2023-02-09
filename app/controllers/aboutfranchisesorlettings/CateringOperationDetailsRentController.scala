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
import form.Form6010.CateringOperationOrLettingAccommodationRentForm.cateringOperationOrLettingAccommodationRentForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.cateringOperationOrLettingAccommodationRentDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CateringOperationDetailsRentController @Inject() (
  mcc: MessagesControllerComponents,
  cateringOperationOrLettingAccommodationRentDetailsView: cateringOperationOrLettingAccommodationRentDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.flatMap(
      _.cateringOperationOrLettingAccommodationSections.lift(index)
    )
    existingSection.fold(
      Redirect(routes.CateringOperationDetailsController.show(None))
    ) { cateringOperationOrLettingAccommodationSection =>
      val rentDetailsForm =
        cateringOperationOrLettingAccommodationSection.cateringOperationOrLettingAccommodationRentDetails.fold(
          cateringOperationOrLettingAccommodationRentForm
        )(cateringOperationOrLettingAccommodationRentForm.fill)
      Ok(
        cateringOperationOrLettingAccommodationRentDetailsView(
          rentDetailsForm,
          index,
          "cateringOperationOrLettingAccommodationRentDetails",
          controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show().url
        )
      )
    }
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    cateringOperationOrLettingAccommodationRentForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              cateringOperationOrLettingAccommodationRentDetailsView(
                formWithErrors,
                index,
                "cateringOperationOrLettingAccommodationRentDetails",
                controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show().url
              )
            )
          ),
        data =>
          request.sessionData.aboutFranchisesOrLettings.fold(
            Future
              .successful(Redirect(routes.CateringOperationDetailsController.show(None)))
          ) { aboutFranchisesOrLettings =>
            val existingSections = aboutFranchisesOrLettings.cateringOperationOrLettingAccommodationSections
            val updatedSections  = existingSections.updated(
              index,
              existingSections(index).copy(cateringOperationOrLettingAccommodationRentDetails = Some(data))
            )
            val dataForSession   =
              updateAboutFranchisesOrLettings(_.copy(cateringOperationOrLettingAccommodationSections = updatedSections))
            session
              .saveOrUpdate(dataForSession)
              .map(_ => Redirect(routes.CateringOperationRentIncludesController.show(index)))
          }
      )
  }

}
