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

package controllers.connectiontoproperty

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import connectors.addressLookup.*
import controllers.{AddressLookupSupport, FORDataCaptureController}
import form.connectiontoproperty.TenantDetailsForm.theForm
import models.Session
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.{CorrespondenceAddress, LettingPartOfPropertyDetails, StillConnectedDetails, TenantDetails}
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.LettingPartOfPropertyDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.tenantDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class LettingPartOfPropertyDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  tenantDetailsView: tenantDetails,
  withSessionRefiner: WithSessionRefiner,
  addressLookupConnector: AddressLookupConnector,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with AddressLookupSupport(addressLookupConnector)
    with I18nSupport:

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("LettingPartOfPropertyDetails")
    val freshForm  = theForm
    val filledForm =
      for
        requestedIndex              <- index
        stillConnectedDetails       <- request.sessionData.stillConnectedDetails
        lettingPartOfPropertyDetail <- stillConnectedDetails.lettingPartOfPropertyDetails.lift(requestedIndex)
      yield theForm.fill(lettingPartOfPropertyDetail.tenantDetails)

    Ok(
      tenantDetailsView(
        filledForm.getOrElse(freshForm),
        index,
        getBackLink(index),
        request.sessionData.toSummary
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[TenantDetails](
      theForm,
      formWithErrors =>
        BadRequest(
          tenantDetailsView(
            formWithErrors,
            index,
            getBackLink(index),
            request.sessionData.toSummary
          )
        ),
      data => {
        val ifLettingPartOfPropertyDetailsEmpty = StillConnectedDetails(lettingPartOfPropertyDetails =
          IndexedSeq(LettingPartOfPropertyDetails(tenantDetails = data))
        )
        var updatedSectionIndex                 = 0
        val updatedStillConnectedDetails        =
          request.sessionData.stillConnectedDetails.fold(ifLettingPartOfPropertyDetailsEmpty) { stillConnectedDetails =>
            val existingSections         = stillConnectedDetails.lettingPartOfPropertyDetails
            val requestedSection         = index.flatMap(existingSections.lift)
            val (idx, updatedSectionObj) =
              requestedSection.fold {
                val defaultSection   = LettingPartOfPropertyDetails(data)
                val appendedSections = existingSections.appended(defaultSection)
                appendedSections.indexOf(defaultSection) -> appendedSections
              } { sectionToUpdate =>
                val indexToUpdate = existingSections.indexOf(sectionToUpdate)
                indexToUpdate -> existingSections
                  .updated(indexToUpdate, sectionToUpdate.copy(tenantDetails = data))
              }
            updatedSectionIndex = idx
            stillConnectedDetails
              .copy(
                lettingPartOfPropertyDetailsIndex = updatedSectionIndex,
                lettingPartOfPropertyDetails = updatedSectionObj
              )
          }

        val updatedData = updateStillConnectedDetails(_ => updatedStillConnectedDetails)
        for
          _              <- repository.saveOrUpdate(updatedData)
          redirectResult <- redirectToAddressLookupFrontend(
                              config = AddressLookupConfig(
                                lookupPageHeadingKey = "tenantDetails.address.lookupPageHeading",
                                selectPageHeadingKey = "tenantDetails.address.selectPageHeading",
                                confirmPageLabelKey = "tenantDetails.address.confirmPageHeading",
                                offRampCall = routes.LettingPartOfPropertyDetailsController.addressLookupCallback(
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
      convertedAddress  = confirmedAddress.asCorrespondenceAddress
      newSession       <- successful(sessionWithAddress(idx, convertedAddress))
      _                <- repository.saveOrUpdate(newSession)
    yield {
      val redirectToCYA = navigator.cyaPageVacant.filter(_ => navigator.from(request) == "CYA")
      val nextPage      =
        redirectToCYA.getOrElse(navigator.nextPage(LettingPartOfPropertyDetailsPageId, newSession).apply(newSession))
      Redirect(nextPage)
    }
  }

  private def getBackLink(mayBeIndex: Option[Int])(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      case _     =>
        mayBeIndex match {
          case Some(index) if index > 0 =>
            controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(index - 1).url
          case _                        => controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
        }
    }

  private def sessionWithAddress(idx: Int, address: CorrespondenceAddress)(using session: Session) =
    assert(session.stillConnectedDetails.isDefined)
    assert(session.stillConnectedDetails.get.lettingPartOfPropertyDetails.lift(idx).isDefined)
    // assert(session.stillConnectedDetails.get.lettingPartOfPropertyDetails(idx).tenantDetails.correspondenceAddress)
    session.copy(
      stillConnectedDetails = session.stillConnectedDetails.map { d =>
        d.copy(
          lettingPartOfPropertyDetails = d.lettingPartOfPropertyDetails.patch(
            idx,
            Seq(
              d.lettingPartOfPropertyDetails(idx)
                .copy(tenantDetails =
                  d.lettingPartOfPropertyDetails(idx).tenantDetails.copy(correspondenceAddress = Some(address))
                )
            ),
            1
          )
        )
      }
    )

  extension (confirmed: AddressLookupConfirmedAddress)
    def asCorrespondenceAddress = CorrespondenceAddress(
      confirmed.buildingNameNumber,
      confirmed.street1,
      confirmed.town,
      confirmed.county,
      confirmed.postcode
    )
