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
import form.aboutfranchisesorlettings.FeeReceivedForm.feeReceivedForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, CateringOperationBusinessDetails, CateringOperationBusinessSection}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.FeeReceivedPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.feeReceived

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class FeeReceivedController @Inject()(
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  feeReceivedView: feeReceived,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[CateringOperationBusinessDetails] = for {
      existingAccommodationSections <-
        request.sessionData.aboutFranchisesOrLettings.map(_.cateringOperationBusinessSections)
      // lift turns exception-throwing access by index into an option-returning safe operation
      requestedAccommodationSection <- existingAccommodationSections.lift(idx)
    } yield requestedAccommodationSection.cateringOperationBusinessDetails

    Ok(
      feeReceivedView(
        existingDetails.fold(feeReceivedForm)(feeReceivedForm.fill),
        idx,
        "concessionDetails",
        "cateringOperationOrLettingAccommodationDetails",
        backLink(idx),
        request.sessionData.toSummary,
        request.sessionData.forType
      )
    )
  }

  def submit(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CateringOperationBusinessDetails](
      feeReceivedForm,
      formWithErrors =>
        BadRequest(
          feeReceivedView(
            formWithErrors,
            idx,
            "concessionDetails",
            "cateringOperationOrLettingAccommodationDetails",
            backLink(idx),
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      data => {
        val ifFranchisesOrLettingsEmpty      = AboutFranchisesOrLettings(cateringOperationBusinessSections =
          IndexedSeq(CateringOperationBusinessSection(cateringOperationBusinessDetails = data))
        )
        val updatedAboutFranchisesOrLettings =
          request.sessionData.aboutFranchisesOrLettings.fold(ifFranchisesOrLettingsEmpty) { franchiseOrLettings =>
            val existingSections                                                     = franchiseOrLettings.cateringOperationBusinessSections
            val requestedSection                                                     = existingSections.lift(idx)
            val updatedSections: (Int, IndexedSeq[CateringOperationBusinessSection]) =
              requestedSection.fold {
                val defaultSection   = CateringOperationBusinessSection(data)
                val appendedSections = existingSections.appended(defaultSection)
                appendedSections.indexOf(defaultSection) -> appendedSections
              } { sectionToUpdate =>
                val indexToUpdate = existingSections.indexOf(sectionToUpdate)
                indexToUpdate -> existingSections.updated(
                  indexToUpdate,
                  sectionToUpdate.copy(cateringOperationBusinessDetails = data)
                )
              }
            franchiseOrLettings
              .copy(
                cateringOperationCurrentIndex = updatedSections._1,
                cateringOperationBusinessSections = updatedSections._2
              )
          }

        val updatedData = updateAboutFranchisesOrLettings(_ => updatedAboutFranchisesOrLettings)
        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(FeeReceivedPageId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def backLink(idx: Int): String =
    controllers.aboutfranchisesorlettings.routes.CateringOperationBusinessDetailsController.show(Some(idx)).url

}
