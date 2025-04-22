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

package controllers.connectiontoproperty

import connectors.{Audit, MockAddressLookup}
import models.Session
import models.submissions.connectiontoproperty.*
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import repositories.SessionRepo
import utils.{JsoupHelpers, TestBaseSpec}

import scala.concurrent.Future.successful

class LettingPartOfPropertyDetailsControllerSpec extends TestBaseSpec with JsoupHelpers:

  "the LettingPartOfPropertyDetails controller" when {
    "handling GET / requests"          should {
      "reply 200 with an empty form" in new ControllerFixture {
        val result = controller.show(index = None)(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading                     shouldBe "tenantDetails.heading"
        page.backLink                    shouldBe routes.IsRentReceivedFromLettingController.show().url
        page.input("tenantName")           should beEmpty
        page.input("descriptionOfLetting") should beEmpty
      }
      "reply 200 with a pre-filled form" in new ControllerFixture(
        lettingPartOfPropertyDetails = IndexedSeq(
          LettingPartOfPropertyDetails(
            tenantDetails = TenantDetails(
              name = "John Doe",
              descriptionOfLetting = "Short term rental",
              correspondenceAddress = None
            )
          )
        )
      ) {
        val result = controller.show(index = Some(0))(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading                     shouldBe "tenantDetails.heading"
        page.backLink                    shouldBe routes.IsRentReceivedFromLettingController.show().url
        page.input("tenantName")           should haveValue("John Doe")
        page.input("descriptionOfLetting") should haveValue("Short term rental")
      }
      "generate back Link" should {
        "to CYA page if query param present" in new ControllerFixture {
          val result = controller.show(index = Some(0))(fakeRequestFromCYA)
          val page   = contentAsJsoup(result)
          page.backLink shouldBe routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
        }
        "to previous letting page" in new ControllerFixture {
          val result = controller.show(index = Some(1))(fakeRequest)
          val page   = contentAsJsoup(result)
          page.backLink shouldBe routes.AddAnotherLettingPartOfPropertyController.show(0).url
        }
        "to Is Rent Received From page if there is only 1 letting" in new ControllerFixture {
          val result = controller.show(index = Some(0))(fakeRequest)
          val page   = contentAsJsoup(result)
          page.backLink shouldBe routes.IsRentReceivedFromLettingController.show().url
        }
      }
    }
    "handling POST /"                  should {
      "reply 400 and error message if the submitted data is invalid" in new ControllerFixture {
        val result = controller.submit(index = None)(
          fakePostRequest.withFormUrlEncodedBody(
            "tenantName"           -> "", // missing,
            "descriptionOfLetting" -> "1234567890" * 6 // too long
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("tenantName")           shouldBe "error.tenantName.required"
        page.error("descriptionOfLetting") shouldBe "error.descriptionOfLetting.maxLength"
      }
      "reply 303 redirect to the address lookup page when given new details" in new ControllerFixture {
        val result = controller.submit(index = None)(
          fakePostRequest.withFormUrlEncodedBody(
            "tenantName"           -> "New tenant",
            "descriptionOfLetting" -> "This has never been given before"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/on-ramp"

        val session  = captor[Session]
        verify(repository, once).saveOrUpdate(session.capture())(any, any)
        val captured = session.getValue

        captured.stillConnectedDetails.value.lettingPartOfPropertyDetailsIndex shouldBe 0
        val details = captured.stillConnectedDetails.value.lettingPartOfPropertyDetails
        details                                          should have size 1
        details(0).tenantDetails.name                  shouldBe "New tenant"
        details(0).tenantDetails.descriptionOfLetting  shouldBe "This has never been given before"
        details(0).tenantDetails.correspondenceAddress shouldBe None
      }
      "reply 303 redirect to the address lookup page when updating existing details" in new ControllerFixture(
        lettingPartOfPropertyDetails = oneLettingPartOfPropertyDetails
      ) {
        val result = controller.submit(index = Some(0))(
          fakePostRequest.withFormUrlEncodedBody(
            "tenantName"           -> "Mario Rossi",
            "descriptionOfLetting" -> "This is updating the existing one"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/on-ramp"

        val session  = captor[Session]
        verify(repository, once).saveOrUpdate(session.capture())(any, any)
        val captured = session.getValue

        captured.stillConnectedDetails.value.lettingPartOfPropertyDetailsIndex shouldBe 0
        val details = captured.stillConnectedDetails.value.lettingPartOfPropertyDetails
        details                                          should have size 1
        details(0).tenantDetails.name                  shouldBe "Mario Rossi"
        details(0).tenantDetails.descriptionOfLetting  shouldBe "This is updating the existing one"
        details(0).tenantDetails.correspondenceAddress shouldBe None
      }
    }
    "retrieving the confirmed address" should {
      "reply 303 redirect to the next page" in new ControllerFixture(
        lettingPartOfPropertyDetails = oneLettingPartOfPropertyDetails
      ) {
        val result = controller.addressLookupCallback(0, "confirmedAddress")(fakeRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.LettingPartOfPropertyDetailsRentController.show(0).url

        val id = captor[String]
        verify(addressLookupConnector, once).getConfirmedAddress(id)(any)
        id.getValue shouldBe "confirmedAddress"

        val session  = captor[Session]
        verify(repository, once).saveOrUpdate(session)(any, any)
        val captured = session.getValue

        captured.stillConnectedDetails.value.lettingPartOfPropertyDetailsIndex shouldBe 0
        val details = captured.stillConnectedDetails.value.lettingPartOfPropertyDetails
        details                                                should have size 1
        details(0).tenantDetails.name                        shouldBe "John Doe"
        details(0).tenantDetails.descriptionOfLetting        shouldBe "Short term rental"
        details(0).tenantDetails.correspondenceAddress.value shouldBe CorrespondenceAddress(
          buildingNameNumber = addressLookupConfirmedAddress.address.lines.get.head,
          street1 = Some(addressLookupConfirmedAddress.address.lines.get.apply(1)),
          town = addressLookupConfirmedAddress.address.lines.get.last,
          county = None,
          postcode = addressLookupConfirmedAddress.address.postcode.get
        )
      }
    }
  }

  val oneLettingPartOfPropertyDetails = IndexedSeq(
    LettingPartOfPropertyDetails(
      tenantDetails = TenantDetails(
        name = "John Doe",
        descriptionOfLetting = "Short term rental",
        correspondenceAddress = None
      )
    )
  )

  trait ControllerFixture(
    lettingPartOfPropertyDetails: IndexedSeq[LettingPartOfPropertyDetails] = IndexedSeq.empty
  ) extends MockAddressLookup:
    val audit      = mock[Audit]
    val repository = mock[SessionRepo]
    when(repository.saveOrUpdate(any[Session])(any, any)).thenReturn(successful(()))

    val controller = new LettingPartOfPropertyDetailsController(
      stubMessagesControllerComponents(),
      audit,
      connectedToPropertyNavigator,
      tenantDetailsView,
      preEnrichedActionRefiner(stillConnectedDetails =
        Some(
          prefilledStillConnectedDetailsYesToAll.copy(
            lettingPartOfPropertyDetails = lettingPartOfPropertyDetails
          )
        )
      ),
      addressLookupConnector,
      repository
    )
