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
import form.aboutfranchisesorlettings.FranchiseTypeDetailsForm.theForm
import models.ForType.*
import models.Session
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, BusinessDetails, CateringOperationSection}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.CateringOperationDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodationDetails as CateringOperationOrLettingAccommodationDetailsView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CateringOperationDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  theView: CateringOperationOrLettingAccommodationDetailsView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[BusinessDetails] = for {
      requestedIndex                <- index
      existingAccommodationSections <-
        request.sessionData.aboutFranchisesOrLettings.map(_.cateringOperationSections)
      // lift turns exception-throwing access by index into an option-returning safe operation
      requestedAccommodationSection <- existingAccommodationSections.lift(requestedIndex)
    } yield requestedAccommodationSection.cateringOperationDetails
    audit.sendChangeLink("BusinessDetails")
    Ok(
      theView(
        existingDetails.fold(theForm)(
          theForm.fill
        ),
        index,
        "concessionDetails",
        "cateringOperationOrLettingAccommodationDetails",
        backLink(request.sessionData, index),
        request.sessionData.toSummary,
        request.sessionData.forType
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[BusinessDetails](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(
            formWithErrors,
            index,
            "concessionDetails",
            "cateringOperationOrLettingAccommodationDetails",
            backLink(request.sessionData, index),
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      formData => {
        val ifFranchisesOrLettingsEmpty      = AboutFranchisesOrLettings(cateringOperationSections =
          IndexedSeq(CateringOperationSection(cateringOperationDetails = formData))
        )
        val updatedAboutFranchisesOrLettings =
          request.sessionData.aboutFranchisesOrLettings.fold(
            ifFranchisesOrLettingsEmpty
          ) { franchiseOrLettings =>
            val existingSections                                             = franchiseOrLettings.cateringOperationSections
            val requestedSection                                             = index.flatMap(existingSections.lift)
            val updatedSections: (Int, IndexedSeq[CateringOperationSection]) =
              requestedSection.fold {
                val defaultSection   = CateringOperationSection(formData)
                val appendedSections = existingSections.appended(defaultSection)
                appendedSections.indexOf(defaultSection) -> appendedSections
              } { sectionToUpdate =>
                val indexToUpdate = existingSections.indexOf(sectionToUpdate)
                indexToUpdate -> existingSections.updated(
                  indexToUpdate,
                  sectionToUpdate.copy(cateringOperationDetails = formData)
                )
              }
            franchiseOrLettings
              .copy(cateringOperationCurrentIndex = updatedSections._1, cateringOperationSections = updatedSections._2)
          }

        val updatedData = updateAboutFranchisesOrLettings(_ => updatedAboutFranchisesOrLettings)
        repository.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(CateringOperationDetailsPageId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def backLink(answers: Session, maybeIndex: Option[Int]): String =
    answers.forType match
      case FOR6015 | FOR6016 =>
        routes.ConcessionOrFranchiseController.show().url
      case _                 =>
        maybeIndex match
          case Some(index) if index > 0 =>
            routes.AddAnotherCateringOperationController.show(index - 1).url
          case _                        =>
            routes.CateringOperationController.show().url
