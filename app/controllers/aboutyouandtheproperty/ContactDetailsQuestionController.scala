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

package controllers.aboutyouandtheproperty

import actions.WithSessionRefiner
import connectors.Audit
import connectors.addressLookup.{AddressLookupConfig, AddressLookupConfirmedAddress, AddressLookupConnector}
import controllers.{AddressLookupSupport, FORDataCaptureController}
import form.aboutyouandtheproperty.ContactDetailsQuestionForm.theForm
import models.Session
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AlternativeAddress, AlternativeContactDetails, ContactDetailsQuestion}
import models.submissions.common.AnswerYes
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.ContactDetailsQuestionId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.contactDetailsQuestion as ContactDetailsQuestionView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ContactDetailsQuestionController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  theView: ContactDetailsQuestionView,
  withSessionRefiner: WithSessionRefiner,
  addressLookupConnector: AddressLookupConnector,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with AddressLookupSupport(addressLookupConnector)
    with I18nSupport:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("ContactDetailsQuestion")
    val freshForm  = theForm
    val filledForm =
      for
        aboutYouAndTheProperty <- request.sessionData.aboutYouAndTheProperty
        altDetailsQuestion     <- aboutYouAndTheProperty.altDetailsQuestion
      yield theForm.fill(altDetailsQuestion)

    successful(
      Ok(
        theView(filledForm.getOrElse(freshForm), request.sessionData.toSummary, navigator.from)
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ContactDetailsQuestion](
      theForm,
      formWithErrors =>
        successful(
          BadRequest(
            theView(formWithErrors, request.sessionData.toSummary)
          )
        ),
      formData =>
        given Session  = request.sessionData
        val newSession = sessionWithFormData(formData)
        repository
          .saveOrUpdate(newSession)
          .flatMap { _ =>
            if formData.contactDetailsQuestion == AnswerYes
            then
              redirectToAddressLookupFrontend(
                config = AddressLookupConfig(
                  lookupPageHeadingKey = "requestReferenceNumber.address.lookupPageHeading",
                  selectPageHeadingKey = "requestReferenceNumber.address.selectPageHeading",
                  confirmPageLabelKey = "requestReferenceNumber.address.confirmPageHeading",
                  offRampCall = routes.ContactDetailsQuestionController.addressLookupCallback()
                )
              )
            else
              // AnswerNo  =>  skip address lookup
              successful(Redirect(navigator.nextPage(ContactDetailsQuestionId, newSession).apply(newSession)))
          }
    )
  }

  def addressLookupCallback(id: String) = (Action andThen withSessionRefiner).async { implicit request =>
    given Session = request.sessionData
    for
      confirmedAddress <- getConfirmedAddress(id)
      convertedAddress  = confirmedAddress.asAlternativeContactDetails
      newSession       <- successful(sessionWithAddress(convertedAddress))
      _                <- repository.saveOrUpdate(newSession)
    yield Redirect(navigator.nextPage(ContactDetailsQuestionId, newSession).apply(newSession))
  }

  private def sessionWithFormData(formData: ContactDetailsQuestion)(using session: Session) =
    session.copy(aboutYouAndTheProperty =
      Some(
        session.aboutYouAndTheProperty.fold(
          AboutYouAndTheProperty(altDetailsQuestion =
            Some(
              formData
            )
          )
        ) { aboutYouAndTheProperty =>
          aboutYouAndTheProperty.copy(altDetailsQuestion =
            Some(
              formData
            )
          )
        }
      )
    )

  private def sessionWithAddress(address: AlternativeContactDetails)(using session: Session) =
    assert(session.aboutYouAndTheProperty.isDefined)
    session.copy(aboutYouAndTheProperty =
      session.aboutYouAndTheProperty.map(
        _.copy(
          altContactInformation = Some(address)
        )
      )
    )

  extension (confirmed: AddressLookupConfirmedAddress)
    private def asAlternativeContactDetails = AlternativeContactDetails(
      alternativeContactAddress = AlternativeAddress(
        confirmed.buildingNameNumber,
        confirmed.street1,
        confirmed.town,
        confirmed.county,
        confirmed.postcode
      )
    )
