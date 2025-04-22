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
import connectors.addressLookup.*
import controllers.{AddressLookupSupport, FORDataCaptureController}
import form.aboutfranchisesorlettings.LettingOtherPartOfPropertyForm.theForm
import models.Session
import models.submissions.aboutfranchisesorlettings.*
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.LettingAccommodationDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodationDetails as CateringOperationOrLettingAccommodationDetailsView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class LettingOtherPartOfPropertyDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  theView: CateringOperationOrLettingAccommodationDetailsView,
  withSessionRefiner: WithSessionRefiner,
  addressLookupConnector: AddressLookupConnector,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with AddressLookupSupport(addressLookupConnector)
    with I18nSupport:

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("LettingOtherPartOfPropertyDetails")
    val freshForm  = theForm
    val filledForm =
      for
        requestedIndex            <- index
        aboutFranchisesOrLettings <- request.sessionData.aboutFranchisesOrLettings
        lettingSection            <- aboutFranchisesOrLettings.lettingSections.lift(requestedIndex)
      yield theForm.fill(lettingSection.lettingOtherPartOfPropertyInformationDetails)

    Ok(
      theView(
        filledForm.getOrElse(freshForm),
        index,
        "lettingDetails",
        "lettingOtherPartOfPropertyDetails",
        backLink(index),
        request.sessionData.toSummary,
        request.sessionData.forType
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[OperatorDetails](
      theForm,
      formWithErrors =>
        successful(
          BadRequest(
            theView(
              formWithErrors,
              index,
              "lettingDetails",
              "lettingOtherPartOfPropertyDetails",
              backLink(index),
              request.sessionData.toSummary,
              request.sessionData.forType
            )
          )
        ),
      formData => {
        val ifFranchisesOrLettingsEmpty      = AboutFranchisesOrLettings(lettingSections =
          IndexedSeq(LettingSection(lettingOtherPartOfPropertyInformationDetails = formData))
        )
        var updatedSectionIndex              = 0
        val updatedAboutFranchisesOrLettings =
          request.sessionData.aboutFranchisesOrLettings.fold(ifFranchisesOrLettingsEmpty) { franchiseOrLettings =>
            val existingSections         = franchiseOrLettings.lettingSections
            val requestedSection         = index.flatMap(existingSections.lift)
            val (idx, updatedSectionObj) =
              requestedSection.fold {
                val defaultSection   = LettingSection(formData)
                val appendedSections = existingSections.appended(defaultSection)
                appendedSections.indexOf(defaultSection) -> appendedSections
              } { sectionToUpdate =>
                val indexToUpdate = existingSections.indexOf(sectionToUpdate)
                indexToUpdate -> existingSections
                  .updated(indexToUpdate, sectionToUpdate.copy(lettingOtherPartOfPropertyInformationDetails = formData))
              }
            updatedSectionIndex = idx
            franchiseOrLettings
              .copy(
                lettingCurrentIndex = updatedSectionIndex,
                lettingSections = updatedSectionObj
              )
          }

        val updatedData = updateAboutFranchisesOrLettings(_ => updatedAboutFranchisesOrLettings)
        for
          _              <- repository.saveOrUpdate(updatedData)
          redirectResult <- redirectToAddressLookupFrontend(
                              config = AddressLookupConfig(
                                lookupPageHeadingKey = "lettingOtherPartOfPropertyDetails.address.lookupPageHeading",
                                selectPageHeadingKey = "lettingOtherPartOfPropertyDetails.address.selectPageHeading",
                                confirmPageLabelKey = "lettingOtherPartOfPropertyDetails.address.selectPageHeading",
                                offRampCall = routes.LettingOtherPartOfPropertyDetailsController.addressLookupCallback(
                                  updatedSectionIndex
                                )
                              )
                            )
        yield redirectResult
      }
    )
  }

  def addressLookupCallback(idx: Int, id: String) = (Action andThen withSessionRefiner).async { implicit request =>
    given Session = request.sessionData
    for
      confirmedAddress <- getConfirmedAddress(id)
      lettingAddress    = confirmedAddress.asLettingAddress
      newSession       <- successful(sessionWithLettingAddress(idx, lettingAddress))
      _                <- repository.saveOrUpdate(newSession)
    yield Redirect(navigator.nextPage(LettingAccommodationDetailsPageId, newSession).apply(newSession))
  }

  private def sessionWithLettingAddress(idx: Int, address: LettingAddress)(using session: Session) =
    assert(session.aboutFranchisesOrLettings.isDefined)
    assert(session.aboutFranchisesOrLettings.get.lettingSections.lift(idx).isDefined)
    def patchOne(a: AboutFranchisesOrLettings): LettingSection =
      a.lettingSections(idx)
        .copy(
          lettingOtherPartOfPropertyInformationDetails = a
            .lettingSections(idx)
            .lettingOtherPartOfPropertyInformationDetails
            .copy(
              lettingAddress = Some(address)
            )
        )

    session.copy(
      aboutFranchisesOrLettings = session.aboutFranchisesOrLettings.map { a =>
        a.copy(
          lettingSections = a.lettingSections.patch(idx, Seq(patchOne(a)), 1)
        )
      }
    )

  private def backLink(maybeIndex: Option[Int]) =
    maybeIndex match {
      case Some(index) if index > 0 => routes.AddAnotherLettingOtherPartOfPropertyController.show(index - 1).url
      case _                        => routes.LettingOtherPartOfPropertyController.show().url
    }

  extension (confirmed: AddressLookupConfirmedAddress)
    def asLettingAddress = LettingAddress(
      confirmed.buildingNameNumber,
      confirmed.street1,
      confirmed.town,
      confirmed.county,
      confirmed.postcode
    )
