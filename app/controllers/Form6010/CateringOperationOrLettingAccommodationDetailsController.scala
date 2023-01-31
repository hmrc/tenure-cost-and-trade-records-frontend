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
import form.Form6010.CateringOperationOrLettingAccommodationForm.cateringOperationOrLettingAccommodationForm
import form.Form6010.CateringOperationOrLettingAccommodationRentForm.cateringOperationOrLettingAccommodationRentForm
import models.submissions.Form6010.CateringOperationOrLettingAccommodationDetails
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{cateringOperationOrLettingAccommodationDetails, cateringOperationOrLettingAccommodationRentDetails}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, CateringOperationOrLettingAccommodationSection}
import controllers.Form6010


import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CateringOperationOrLettingAccommodationDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  cateringOperationOrLettingAccommodationDetailsView: cateringOperationOrLettingAccommodationDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport{

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[CateringOperationOrLettingAccommodationDetails] = for {
      requestedIndex <- index
      existingAccommodationSections <- request.sessionData.aboutFranchisesOrLettings.map(_.cateringOperationOrLettingAccommodationSections)
      // lift turns exception-throwing access by index into an option-returning safe operation
      requestedAccommodationSection <- existingAccommodationSections.lift(requestedIndex)
    } yield requestedAccommodationSection.cateringOperationOrLettingAccommodationDetails

    Ok(cateringOperationOrLettingAccommodationDetailsView(existingDetails.fold(cateringOperationOrLettingAccommodationForm)(cateringOperationOrLettingAccommodationForm.fill), index))
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    cateringOperationOrLettingAccommodationForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(cateringOperationOrLettingAccommodationDetailsView(formWithErrors, index))),
        data => {
          val ifFranchisesOrLettingsEmpty = AboutFranchisesOrLettings(cateringOperationOrLettingAccommodationSections = IndexedSeq(CateringOperationOrLettingAccommodationSection(cateringOperationOrLettingAccommodationDetails = data)))
          val updatedAboutFranchisesOrLettings: (Int, AboutFranchisesOrLettings) = request.sessionData.aboutFranchisesOrLettings.fold(0 -> ifFranchisesOrLettingsEmpty){franchiseOrLettings =>
          val existingSections = franchiseOrLettings.cateringOperationOrLettingAccommodationSections
            val requestedSection = index.flatMap(existingSections.lift)
            val updatedSections: (Int, IndexedSeq[CateringOperationOrLettingAccommodationSection]) = requestedSection.fold{
              val defaultSection = CateringOperationOrLettingAccommodationSection(data)
              val appendedSections = existingSections.appended(defaultSection)
              appendedSections.indexOf(defaultSection) -> appendedSections
            }{ sectionToUpdate =>
              val indexToUpdate = existingSections.indexOf(sectionToUpdate)
              indexToUpdate -> existingSections.updated(indexToUpdate, sectionToUpdate.copy(cateringOperationOrLettingAccommodationDetails = data))
            }
            updatedSections._1 -> franchiseOrLettings.copy(cateringOperationOrLettingAccommodationSections = updatedSections._2)
          }
          updatedAboutFranchisesOrLettings match {
            case (currentIndex, aboutFranchisesOrLettings) => session.saveOrUpdate(updateAboutFranchisesOrLettings(_ => aboutFranchisesOrLettings)).map(_ =>
              Redirect(Form6010.routes.CateringOperationOrLettingAccommodationDetailsRentController.show(currentIndex))
            )
          }

        }
      )
  }

}
