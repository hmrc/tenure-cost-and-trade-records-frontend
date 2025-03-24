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

package controllers.requestReferenceNumber

import actions.SessionRequest
import connectors.addressLookup.*
import models.Session
import models.submissions.requestReferenceNumber.{RequestReferenceNumberAddress, RequestReferenceNumberDetails, RequestReferenceNumberPropertyDetails}
import navigation.RequestReferenceNumberNavigator
import org.scalatest.RecoverMethods.recoverToExceptionIf
import play.api.http.Status
import play.api.libs.json.Writes
import play.api.mvc.AnyContent
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.{JsoupHelpers, TestBaseSpec}
import views.html.requestReferenceNumber.requestReferenceNumberPropertyDetails as RequestReferenceNumberPropertyDetailsView

import scala.concurrent.Future.successful

class RequestReferenceNumberPropertyDetailsControllerSpec extends TestBaseSpec with JsoupHelpers:

  "the RequestReferenceNumber controller" when {
    "starting with a session"          should {
      "reply 303 redirect to the show page" in new ControllerFixture {
        val result = controller.startWithSession(fakeRequest)
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(
          result
        ).value        shouldBe routes.RequestReferenceNumberPropertyDetailsController.show().url
      }
    }
    "showing the form"                 should {
      "reply 200 with an empty form" in new ControllerFixture {
        val result = controller.show(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading                    shouldBe "requestReferenceNumber.heading"
        page.backLink                   shouldBe controllers.routes.LoginController.show.url
        page.input("businessTradingName") should beEmpty
      }
      "reply 200 with a pre-filled form" in new ControllerFixture(
        requestReferenceNumberDetails = Some(
          RequestReferenceNumberDetails(
            propertyDetails = Some(
              RequestReferenceNumberPropertyDetails(
                businessTradingName = "Wombles Inc",
                address = None
              )
            ),
            contactDetails = None,
            checkYourAnswers = None
          )
        )
      ) {
        val result = controller.show(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading                    shouldBe "requestReferenceNumber.heading"
        page.backLink                   shouldBe controllers.routes.LoginController.show.url
        page.input("businessTradingName") should haveValue("Wombles Inc")
      }
    }
    "submitting the form"              should {
      "reply 404 if the submitted data is invalid" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "businessTradingName" -> "" // missing
          )
        )
        status(result) shouldBe Status.BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("businessTradingName") shouldBe "error.requestReferenceNumber.businessTradingName.required"
      }
      "throw exception if the address lookup service did not provide the /on-ramp location" in new ControllerFixture {
        when(addressLookupConnector.initJourney(any[AddressLookupConfig])(any[SessionRequest[AnyContent]]))
          .thenReturn(successful(None))
        recoverToExceptionIf[Exception] {
          controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "businessTradingName" -> "Wombles Inc"
            )
          )
        }.map { exception =>
          exception should have message "The AddressLookupConnector did not receive the on-ramp location from the ADDRESS_LOOKUP_FRONTEND service"
        }
      }
      "reply 303 redirect to the address lookup page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "businessTradingName" -> "Wombles Inc"
          )
        )
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result).value shouldBe "/on-ramp"
        val session = captor[Session]
        verify(repository, once).saveOrUpdate(session.capture())(any[Writes[Session]], any[HeaderCarrier])
        session.getValue.requestReferenceNumberDetails.value.propertyDetails.value.businessTradingName shouldBe "Wombles Inc"
      }
    }
    "retrieving the confirmed address" should {
      "reply 303 redirect to the next page" in new ControllerFixture(
        requestReferenceNumberDetails = Some(
          RequestReferenceNumberDetails(
            propertyDetails = Some(
              RequestReferenceNumberPropertyDetails(
                businessTradingName = "Wombles Inc",
                address = None
              )
            ),
            contactDetails = None,
            checkYourAnswers = None
          )
        )
      ) {
        val result = controller.addressLookupCallback("123")(fakeRequest)
        status(result)                 shouldBe Status.SEE_OTHER
        redirectLocation(result).value shouldBe routes.RequestReferenceNumberContactDetailsController.show().url

        val id = captor[String]
        verify(addressLookupConnector, once).getConfirmedAddress(id)(any[HeaderCarrier])
        id.getValue shouldBe "123"

        val session = captor[Session]
        verify(repository, once).saveOrUpdate(session)(any[Writes[Session]], any[HeaderCarrier])
        session.getValue.requestReferenceNumberDetails.value.propertyDetails.value.address.value shouldBe RequestReferenceNumberAddress(
          buildingNameNumber = confirmedAddress.address.lines.get.head,
          street1 = Some(confirmedAddress.address.lines.get.apply(1)),
          town = confirmedAddress.address.lines.get.last,
          county = None,
          postcode = confirmedAddress.address.postcode.get
        )
      }
    }
  }

  trait ControllerFixture(
    requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = None
  ):
    val repository = mock[SessionRepo]
    when(repository.saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier]))
      .thenReturn(successful(()))

    val addressLookupConnector = mock[AddressLookupConnector]
    when(addressLookupConnector.initJourney(any[AddressLookupConfig])(any[SessionRequest[AnyContent]]))
      .thenReturn(successful(Some("/on-ramp")))

    val confirmedAddress = AddressLookupConfirmedAddress(
      address = AddressLookupAddress(
        lines = Some(Seq("line1", "line2", "line3")),
        postcode = Some("postcode"),
        country = None
      ),
      auditRef = "auditRef",
      id = Some("id")
    )
    when(addressLookupConnector.getConfirmedAddress(anyString)(any[HeaderCarrier]))
      .thenReturn(successful(confirmedAddress))

    val controller = new RequestReferenceNumberPropertyDetailsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[RequestReferenceNumberNavigator],
      theView = inject[RequestReferenceNumberPropertyDetailsView],
      preEnrichedActionRefiner(
        requestReferenceNumberDetails = requestReferenceNumberDetails
      ),
      addressLookupConnector,
      repository
    )
