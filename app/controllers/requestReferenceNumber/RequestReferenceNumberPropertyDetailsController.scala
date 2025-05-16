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

package controllers.requestReferenceNumber

import actions.WithSessionRefiner
import connectors.addressLookup.{AddressLookupConfig, AddressLookupConfirmedAddress, AddressLookupConnector}
import controllers.AddressLookupSupport
import form.requestReferenceNumber.RequestReferenceNumberPropertyDetailsForm.theForm
import models.ForType.*
import models.Session
import models.submissions.common.Address
import models.submissions.requestReferenceNumber.*
import navigation.RequestReferenceNumberNavigator
import navigation.identifiers.NoReferenceNumberPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.requestReferenceNumber.requestReferenceNumberPropertyDetails as RequestReferenceNumberPropertyDetailsView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future.successful
import scala.concurrent.ExecutionContext

@Singleton
class RequestReferenceNumberPropertyDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: RequestReferenceNumberNavigator,
  theView: RequestReferenceNumberPropertyDetailsView,
  withSessionRefiner: WithSessionRefiner,
  addressLookupConnector: AddressLookupConnector,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FrontendController(mcc)
    with AddressLookupSupport(addressLookupConnector)
    with I18nSupport:

  def startWithSession: Action[AnyContent] = Action.async { implicit request =>
    repository.start(Session("", FOR6010, Address("", None, "", None, ""), "", isWelsh = false))
    successful(Redirect(routes.RequestReferenceNumberPropertyDetailsController.show()))
  }

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val freshForm  = theForm
    val filledForm =
      for
        details <- request.sessionData.requestReferenceNumberDetails
        address <- details.propertyDetails
      yield freshForm.fill(address.businessTradingName)

    successful(
      Ok(
        theView(filledForm.getOrElse(freshForm))
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    theForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          successful(
            BadRequest(
              theView(formWithErrors)
            )
          ),
        formData => {
          given Session = request.sessionData
          for
            newSession     <- successful(sessionWithBusinessTradingName(formData))
            _              <- repository.saveOrUpdate(newSession)
            redirectResult <- redirectToAddressLookupFrontend(
                                config = AddressLookupConfig(
                                  lookupPageHeadingKey = "requestReferenceNumber.address.lookupPageHeading",
                                  selectPageHeadingKey = "requestReferenceNumber.address.selectPageHeading",
                                  confirmPageLabelKey = "requestReferenceNumber.address.confirmPageHeading",
                                  offRampCall =
                                    routes.RequestReferenceNumberPropertyDetailsController.addressLookupCallback()
                                )
                              )
          yield redirectResult
        }
      )
  }

  private def sessionWithBusinessTradingName(businessTradingName: String)(using session: Session) =
    session.copy(
      requestReferenceNumberDetails = Some(
        session.requestReferenceNumberDetails.fold(
          RequestReferenceNumberDetails(
            propertyDetails = Some(
              RequestReferenceNumberPropertyDetails(businessTradingName, address = None)
            )
          )
        ) { details =>
          details.copy(
            propertyDetails = Some(
              details.propertyDetails.fold(
                RequestReferenceNumberPropertyDetails(businessTradingName, address = None)
              ) { address =>
                address.copy(businessTradingName = businessTradingName)
              }
            )
          )
        }
      )
    )

  def addressLookupCallback(id: String) = (Action andThen withSessionRefiner).async { implicit request =>
    given Session = request.sessionData
    for
      confirmedAddress <- getConfirmedAddress(id)
      convertedAddress  = confirmedAddress.asRequestReferenceNumberAddress
      newSession       <- successful(sessionWithConfirmedAddress(convertedAddress))
      _                <- repository.saveOrUpdate(newSession)
    yield Redirect(navigator.nextPage(NoReferenceNumberPageId, newSession).apply(newSession))
  }

  extension (confirmed: AddressLookupConfirmedAddress)
    private def asRequestReferenceNumberAddress = RequestReferenceNumberAddress(
      confirmed.buildingNameNumber,
      confirmed.street1,
      confirmed.town,
      confirmed.county,
      confirmed.postcode
    )

  private def sessionWithConfirmedAddress(addr: RequestReferenceNumberAddress)(using session: Session) =
    assert(session.requestReferenceNumberDetails.isDefined)
    assert(session.requestReferenceNumberDetails.get.propertyDetails.isDefined)
    session.copy(
      requestReferenceNumberDetails = session.requestReferenceNumberDetails.map { detail =>
        detail.copy(
          propertyDetails = detail.propertyDetails.map { property =>
            property.copy(
              address = Some(addr)
            )
          }
        )
      }
    )
