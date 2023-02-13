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
import form.Form6010.LettingOtherPartOfPropertyForm.lettingOtherPartOfPropertyForm
import models.submissions.Form6010.LettingOtherPartOfPropertyInformationDetails
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, LettingSection}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutfranchisesorlettings.{cateringOperationOrLettingAccommodationDetails, cateringOperationOrLettingAccommodationRentDetails}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LettingOtherPartOfPropertyDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  cateringOperationOrLettingAccommodationDetailsView: cateringOperationOrLettingAccommodationDetails,
  cateringOperationOrLettingAccommodationRentDetailsView: cateringOperationOrLettingAccommodationRentDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[LettingOtherPartOfPropertyInformationDetails] = for {
      requestedIndex          <- index
      existingLettingSections <- request.sessionData.aboutFranchisesOrLettings.map(_.lettingSections)
      // lift turns exception-throwing access by index into an option-returning safe operation
      requestedLettingSection <- existingLettingSections.lift(requestedIndex)
    } yield requestedLettingSection.lettingOtherPartOfPropertyInformationDetails

    Ok(
      cateringOperationOrLettingAccommodationDetailsView(
        existingDetails.fold(lettingOtherPartOfPropertyForm)(lettingOtherPartOfPropertyForm.fill),
        index,
        "lettingOtherPartOfPropertyDetails",
        controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show().url
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    lettingOtherPartOfPropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              cateringOperationOrLettingAccommodationDetailsView(
                formWithErrors,
                index,
                "lettingOtherPartOfPropertyDetails",
                controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show().url
              )
            )
          ),
        data => {
          val ifFranchisesOrLettingsEmpty                                        = AboutFranchisesOrLettings(lettingSections =
            IndexedSeq(LettingSection(lettingOtherPartOfPropertyInformationDetails = data))
          )
          val updatedAboutFranchisesOrLettings: (Int, AboutFranchisesOrLettings) =
            request.sessionData.aboutFranchisesOrLettings.fold(0 -> ifFranchisesOrLettingsEmpty) {
              franchiseOrLettings =>
                val existingSections                                   = franchiseOrLettings.lettingSections
                val requestedSection                                   = index.flatMap(existingSections.lift)
                val updatedSections: (Int, IndexedSeq[LettingSection]) = requestedSection.fold {
                  val defaultSection   = LettingSection(data)
                  val appendedSections = existingSections.appended(defaultSection)
                  appendedSections.indexOf(defaultSection) -> appendedSections
                } { sectionToUpdate =>
                  val indexToUpdate = existingSections.indexOf(sectionToUpdate)
                  indexToUpdate -> existingSections
                    .updated(indexToUpdate, sectionToUpdate.copy(lettingOtherPartOfPropertyInformationDetails = data))
                }
                updatedSections._1 -> franchiseOrLettings.copy(lettingSections = updatedSections._2)
            }
          updatedAboutFranchisesOrLettings match {
            case (currentIndex, aboutFranchisesOrLettings) =>
              session
                .saveOrUpdate(updateAboutFranchisesOrLettings(_ => aboutFranchisesOrLettings))
                .map(_ => Redirect(routes.LettingOtherPartOfPropertyDetailsRentController.show(currentIndex)))
          }
        }
      )
  }

}
