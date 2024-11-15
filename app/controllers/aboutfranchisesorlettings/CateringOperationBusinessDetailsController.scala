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

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.CateringOperationBusinessDetails6030Form.cateringOperationBusinessDetails6030Form
import form.aboutfranchisesorlettings.CateringOperationBusinessDetailsForm.cateringOperationBusinessDetailsForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, CateringOperationBusinessDetails, CateringOperationBusinessSection}
import models.ForType
import models.ForType.*
import models.Session
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.CateringOperationBusinessPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodationDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CateringOperationBusinessDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationDetailsView: cateringOperationOrLettingAccommodationDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  private def forType(implicit request: SessionRequest[AnyContent]): ForType =
    request.sessionData.forType

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[CateringOperationBusinessDetails] = for {
      requestedIndex                <- index
      existingAccommodationSections <-
        request.sessionData.aboutFranchisesOrLettings.map(
          _.cateringOperationBusinessSections.getOrElse(IndexedSeq.empty)
        )
      // lift turns exception-throwing access by index into an option-returning safe operation
      requestedAccommodationSection <- existingAccommodationSections.lift(requestedIndex)
    } yield requestedAccommodationSection.cateringOperationBusinessDetails

    Ok(
      cateringOperationDetailsView(
        if (forType == FOR6030) {
          existingDetails.fold(cateringOperationBusinessDetails6030Form)(
            cateringOperationBusinessDetails6030Form.fill
          )
        } else {
          existingDetails.fold(cateringOperationBusinessDetailsForm)(
            cateringOperationBusinessDetailsForm.fill
          )
        },
        index,
        "concessionDetails",
        "cateringOperationOrLettingAccommodationDetails",
        getBackLink(request.sessionData, index),
        request.sessionData.toSummary,
        forType
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CateringOperationBusinessDetails](
      if (forType == FOR6030) {
        cateringOperationBusinessDetails6030Form
      } else {
        cateringOperationBusinessDetailsForm
      },
      formWithErrors =>
        BadRequest(
          cateringOperationDetailsView(
            formWithErrors,
            index,
            "concessionDetails",
            "cateringOperationOrLettingAccommodationDetails",
            getBackLink(request.sessionData, index),
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      data => {
        val ifFranchisesOrLettingsEmpty      = AboutFranchisesOrLettings(cateringOperationBusinessSections =
          Some(IndexedSeq(CateringOperationBusinessSection(cateringOperationBusinessDetails = data)))
        )
        val updatedAboutFranchisesOrLettings =
          request.sessionData.aboutFranchisesOrLettings.fold(ifFranchisesOrLettingsEmpty) { franchiseOrLettings =>
            val existingSections = franchiseOrLettings.cateringOperationBusinessSections.getOrElse(IndexedSeq.empty)
            val requestedSection = index.flatMap(existingSections.lift)

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
                cateringOperationBusinessSections = Some(updatedSections._2)
              )
          }

        val updatedData = updateAboutFranchisesOrLettings(_ => updatedAboutFranchisesOrLettings)
        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(CateringOperationBusinessPageId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def getBackLink(answers: Session, maybeIndex: Option[Int]): String =
    answers.forType match {
      case FOR6015 | FOR6016 =>
        controllers.aboutfranchisesorlettings.routes.ConcessionOrFranchiseController.show().url
      case FOR6030           =>
        controllers.aboutfranchisesorlettings.routes.ConcessionOrFranchiseFeeController.show().url
      case _                 =>
        maybeIndex match {
          case Some(index) if index > 0 =>
            controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(index - 1).url
          case _                        =>
            controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
        }
    }

}
