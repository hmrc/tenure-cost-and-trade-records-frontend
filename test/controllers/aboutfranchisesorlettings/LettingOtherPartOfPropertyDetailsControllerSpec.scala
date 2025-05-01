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

package controllers.aboutfranchisesorlettings

import connectors.{Audit, MockAddressLookup}
import models.Session
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, LettingAddress}
import play.api.test.Helpers.*
import repositories.SessionRepo
import utils.JsoupHelpers.*
import utils.{JsoupHelpers, TestBaseSpec}

import scala.concurrent.Future.successful

class LettingOtherPartOfPropertyDetailsControllerSpec extends TestBaseSpec with JsoupHelpers:

  "the LettingOtherPartOfPropertyDetails controller" when {
    "handling GET / requests"          should {
      "reply 200 with a fresh HTML form if not given any index" in new ControllerFixture {
        val result = controller.show(index = None)(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading                      shouldBe "lettingOtherPartOfPropertyDetails.heading"
        page.input("lettingOperatorName")   should beEmpty
        page.input("lettingTypeOfBusiness") should beEmpty
        page.backLink                       should endWith(routes.TypeOfIncomeController.show().url)
      }
      "reply 200 with a fresh HTML form if given an unknown index" in new ControllerFixture {
        val result = controller.show(index = Some(2))(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading                      shouldBe "lettingOtherPartOfPropertyDetails.heading"
        page.input("lettingOperatorName")   should beEmpty
        page.input("lettingTypeOfBusiness") should beEmpty
        page.backLink                       should endWith(routes.AddAnotherLettingOtherPartOfPropertyController.show(1).url)
      }
      "reply 200 with a pre-filled HTML form if given a known index" in new ControllerFixture(
        Some(prefilledAboutFranchiseOrLettings)
      ) {
        val result = controller.show(index = Some(0))(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.input("lettingOperatorName")   should haveValue("Operator Name")
        page.input("lettingTypeOfBusiness") should haveValue("Type of Business")
      }
    }
    "handling POST / requests"         should {
      "reply 400 and error messages if the form is submitted with invalid data" in new ControllerFixture {
        val result = controller.submit(index = None)(
          fakePostRequest.withFormUrlEncodedBody(
            "lettingOperatorName"   -> "", // missing
            "lettingTypeOfBusiness" -> "0123456789" * 6 // too long
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("lettingOperatorName")   shouldBe "error.operatorName.required"
        page.error("lettingTypeOfBusiness") shouldBe "error.lettingTypeOfBusiness.maxLength"
      }
      "reply 303 to the address lookup page when the form is submitted good, with index=1 but no pre-existing data" in new ControllerFixture(
        Some(prefilledAboutFranchiseOrLettings)
      ) {
        val result = controller.submit(index = Some(1))(
          fakePostRequest.withFormUrlEncodedBody(
            "lettingOperatorName"   -> "Some operator",
            "lettingTypeOfBusiness" -> "Nice type of business"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/on-ramp"

        val session  = captor[Session]
        verify(repository, once).saveOrUpdate(session.capture())(any, any)
        val captured = session.getValue

        captured.aboutFranchisesOrLettings.value.lettingSections.size shouldBe 2
        val section = captured.aboutFranchisesOrLettings.value.lettingSections.last
        section.lettingOtherPartOfPropertyInformationDetails.operatorName   shouldBe "Some operator"
        section.lettingOtherPartOfPropertyInformationDetails.typeOfBusiness shouldBe "Nice type of business"
        section.lettingOtherPartOfPropertyInformationDetails.lettingAddress shouldBe None
      }
      "reply 303 when the form is submitted good, with index=0 and pre-existing data" in new ControllerFixture(
        Some(prefilledAboutFranchiseOrLettings)
      ) {
        val result = controller.submit(index = Some(0))(
          fakePostRequest.withFormUrlEncodedBody(
            "lettingOperatorName"   -> "Yet another operator",
            "lettingTypeOfBusiness" -> "Residential accommodation"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/on-ramp"

        val session  = captor[Session]
        verify(repository, once).saveOrUpdate(session.capture())(any, any)
        val captured = session.getValue

        captured.aboutFranchisesOrLettings.value.lettingSections.size shouldBe 1
        val section = captured.aboutFranchisesOrLettings.value.lettingSections.last
        section.lettingOtherPartOfPropertyInformationDetails.operatorName   shouldBe "Yet another operator"
        section.lettingOtherPartOfPropertyInformationDetails.typeOfBusiness shouldBe "Residential accommodation"
        section.lettingOtherPartOfPropertyInformationDetails.lettingAddress shouldBe None
      }
    }
    "retrieving the confirmed address" should {
      "reply 303 redirect to the next page" in new ControllerFixture(
        Some(prefilledAboutFranchiseOrLettings)
      ) {
        val result = controller.addressLookupCallback(0, "confirmedAddress")(fakeRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.LettingOtherPartOfPropertyDetailsRentController.show(0).url

        val session  = captor[Session]
        verify(repository, once).saveOrUpdate(session)(any, any)
        val captured = session.getValue

        captured.aboutFranchisesOrLettings.value.lettingSections.size shouldBe 1
        val section = captured.aboutFranchisesOrLettings.value.lettingSections.head
        section.lettingOtherPartOfPropertyInformationDetails.operatorName         shouldBe "Operator Name"
        section.lettingOtherPartOfPropertyInformationDetails.typeOfBusiness       shouldBe "Type of Business"
        section.lettingOtherPartOfPropertyInformationDetails.lettingAddress.value shouldBe LettingAddress(
          buildingNameNumber = addressLookupConfirmedAddress.address.lines.get.head,
          street1 = Some(addressLookupConfirmedAddress.address.lines.get.apply(1)),
          town = addressLookupConfirmedAddress.address.lines.get.last,
          county = None,
          postcode = addressLookupConfirmedAddress.address.postcode.get
        )
      }
    }
  }

  trait ControllerFixture(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = None
  ) extends MockAddressLookup:
    val audit      = mock[Audit]
    val repository = mock[SessionRepo]
    when(repository.saveOrUpdate(any[Session])(any, any)).thenReturn(successful(()))

    val controller =
      new LettingOtherPartOfPropertyDetailsController(
        stubMessagesControllerComponents(),
        audit,
        aboutFranchisesOrLettingsNavigator,
        lettingOtherPartOfPropertyDetailsView,
        preEnrichedActionRefiner(
          aboutFranchisesOrLettings = aboutFranchisesOrLettings
        ),
        addressLookupConnector,
        repository
      )
