/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.lettingHistory

import actions.{SessionRequest, WithSessionRefiner}
import connectors.addressLookup.{AddressLookupConfig, AddressLookupConfirmedAddress, AddressLookupConnector}
import controllers.{AddressLookupSupport, FORDataCaptureController}
import form.lettingHistory.OccupierDetailForm.theForm
import models.Session
import models.submissions.lettingHistory.LettingHistory.{MaxNumberOfCompletedLettings, byAddingOrUpdatingOccupier, byUpdatingOccupierAddress}
import models.submissions.lettingHistory.OccupierDetail
import models.submissions.lettingHistory.OccupierAddress
import navigation.LettingHistoryNavigator
import navigation.identifiers.OccupierDetailPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.lettingHistory.occupierDetail as OccupierDetailView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class OccupierDetailController @Inject (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: OccupierDetailView,
  sessionRefiner: WithSessionRefiner,
  addressLookupConnector: AddressLookupConnector,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with AddressLookupSupport(addressLookupConnector)
    with RentalPeriodSupport
    with I18nSupport:

  def show(maybeIndex: Option[Int] = None): Action[AnyContent] = (Action andThen sessionRefiner).apply {
    implicit request =>
      val completedLettings = (
        for lettingHistory <- request.sessionData.lettingHistory.toList
        yield lettingHistory.completedLettings
      ).flatten

      if maybeIndex.isEmpty && completedLettings.size == MaxNumberOfCompletedLettings
      then Redirect(routes.OccupierListController.show)
      else
        val freshForm  = theForm
        val filledForm =
          for
            index          <- maybeIndex
            occupierDetail <- completedLettings.lift(index)
          yield freshForm.fill(occupierDetail)

        Ok(
          theView(
            filledForm.getOrElse(freshForm),
            effectiveRentalPeriod,
            backLinkUrl,
            maybeIndex
          )
        )
  }

  def submit(maybeIndex: Option[Int] = None): Action[AnyContent] =
    (Action andThen sessionRefiner).async { implicit request =>
      continueOrSaveAsDraft[OccupierDetail](
        theForm,
        theFormWithErrors =>
          successful(
            BadRequest(
              theView(
                theFormWithErrors,
                effectiveRentalPeriod,
                backLinkUrl,
                maybeIndex
              )
            )
          ),
        formData =>
          given Session                      = request.sessionData
          val (updatedIndex, updatedSession) = byAddingOrUpdatingOccupier(formData, maybeIndex)
          for
            _              <- repository.saveOrUpdateSession(updatedSession)
            redirectResult <- redirectToAddressLookupFrontend(
                                config = AddressLookupConfig(
                                  lookupPageHeadingKey = "lettingHistory.occupierDetail.address.lookupPageHeading",
                                  selectPageHeadingKey = "lettingHistory.occupierDetail.address.selectPageHeading",
                                  confirmPageLabelKey = "lettingHistory.occupierDetail.address.confirmPageHeading",
                                  offRampCall = routes.OccupierDetailController.addressLookupCallback(updatedIndex)
                                )
                              )
          yield redirectResult
      )
    }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = OccupierDetailPageId)

  def addressLookupCallback(idx: Int, id: String) = (Action andThen sessionRefiner).async { implicit request =>
    given Session = request.sessionData
    for
      confirmedAddress <- getConfirmedAddress(id)
      occupierAddress  <- confirmedAddress.asOccupierAddress
      newSession       <- successful(byUpdatingOccupierAddress(idx, occupierAddress))
      navigationData    = Map("index" -> idx)
      savedSession     <- repository.saveOrUpdateSession(newSession)
    yield navigator.redirect(currentPage = OccupierDetailPageId, savedSession, navigationData)
  }

  extension (confirmed: AddressLookupConfirmedAddress)
    def asOccupierAddress = OccupierAddress(
      confirmed.buildingNameNumber,
      confirmed.street1,
      confirmed.town,
      confirmed.county,
      confirmed.postcode
    )
