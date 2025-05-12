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

import connectors.{Audit, MockAddressLookup}
import models.ForType.*
import models.Session
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, AdvertisingRightLetting, LettingAddress}
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, *}
import repositories.SessionRepo
import utils.{JsoupHelpers, TestBaseSpec}

import scala.concurrent.Future.successful

class AdvertisingRightLettingControllerSpec 
  extends TestBaseSpec
  with JsoupHelpers:

  trait ControllerFixture(havingNoLettings: Boolean = false) extends MockAddressLookup:
    val repository = mock[SessionRepo]
    when(repository.saveOrUpdate(any[Session])(any, any)).thenReturn(successful(()))
    val controller = new AdvertisingRightLettingController(
      stubMessagesControllerComponents(),
      mock[Audit],
      aboutFranchisesOrLettingsNavigator,
      advertisingRightView,
      preEnrichedActionRefiner(
        forType = FOR6020,
        aboutFranchisesOrLettings = Some(
          prefilledAboutFranchiseOrLettingsWith6020LettingsAll.copy(
            lettings =
              if havingNoLettings
              then None
              else prefilledAboutFranchiseOrLettingsWith6020LettingsAll.lettings
        )),
      ),
      addressLookupConnector,
      repository
    )

  "the AdvertisingRightLetting controller" when {
    "handling GET requests"            should {
      "reply 200 with a fresh HTML form" in new ControllerFixture {
        val result = controller.show(index = Some(4))(fakeRequest)
        val html = contentAsJsoup(result)
        status(result) shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading shouldBe "label.advertisingRightLetting.heading"
        page.input("descriptionOfSpace") should beEmpty
        page.input("advertisingCompanyName") should beEmpty
      }
      "reply 200 with a pre-filled HTML form if given a known index" in new ControllerFixture {
        val result = controller.show(index = Some(2))(fakeRequest)
        val page = contentAsJsoup(result)
        page.input("descriptionOfSpace") should haveValue("Billboard")
        page.input("advertisingCompanyName") should haveValue("JCDx")
      }
      "render back link to CYA if come from CYA" in new ControllerFixture {
        val result = controller.show(Some(0))(fakeRequestFromCYA)
        val page = contentAsJsoup(result)
        page.backLink shouldBe routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      }
    }
    "handling POST requests" should {
      "throw a BAD_REQUEST if an empty form is submitted" in new ControllerFixture {
        val result = controller.submit(index = Some(0))(
          FakeRequest().withFormUrlEncodedBody(Seq.empty *)
        )
        status(result) shouldBe BAD_REQUEST
        val page = contentAsJsoup(result)
        page.error("descriptionOfSpace") shouldBe "error.descriptionOfSpace.required"
        page.error("advertisingCompanyName") shouldBe "error.advertisingCompanyName.required"
      }
      "save new record and reply 303 and redirect to address lookup page" in new ControllerFixture(havingNoLettings = true) {
        val descriptionOfSpace = "New Ballet Hall"
        val advertisingCompanyName = "Dancing Company"
        val result = controller.submit(index = Some(0))(
          fakePostRequest.withFormUrlEncodedBody(
            "descriptionOfSpace" -> descriptionOfSpace,
            "advertisingCompanyName" -> advertisingCompanyName
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/on-ramp"
        val session = captor[Session]
        verify(repository, once).saveOrUpdate(session.capture())(any, any)
        inside(session.getValue.aboutFranchisesOrLettings.value.lettings.value.apply(0)) {
          case record: AdvertisingRightLetting =>
            record.descriptionOfSpace.value shouldBe descriptionOfSpace
            record.advertisingCompanyName.value shouldBe advertisingCompanyName
        }
      }
      "update existing record and reply 303 and redirect to address lookup page" in new ControllerFixture {
        val descriptionOfSpace = "Turned into Ballet Hall"
        val advertisingCompanyName = "Dancing Company"
        val result = controller.submit(index = Some(2))(
          fakePostRequest.withFormUrlEncodedBody(
            "descriptionOfSpace" -> descriptionOfSpace,
            "advertisingCompanyName" -> advertisingCompanyName
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/on-ramp"
        val session = captor[Session]
        verify(repository, once).saveOrUpdate(session.capture())(any, any)
        inside(session.getValue.aboutFranchisesOrLettings.value.lettings.value.apply(2)) {
          case record: AdvertisingRightLetting =>
            record.descriptionOfSpace.value shouldBe descriptionOfSpace
            record.advertisingCompanyName.value shouldBe advertisingCompanyName
        }
      }
      "save record and reply 303 redirect to the next page" in new ControllerFixture {
        val result = controller.addressLookupCallback(idx = 2, "confirmedAddress")(fakeRequest)
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.RentDetailsController.show(2).url

        val id = captor[String]
        verify(addressLookupConnector, once).getConfirmedAddress(id)(any)
        id.getValue shouldBe "confirmedAddress"

        val session = captor[Session]
        verify(repository, once).saveOrUpdate(session)(any, any)
        inside(session.getValue.aboutFranchisesOrLettings.value.lettings.value.apply(2)) {
          case record: AdvertisingRightLetting =>
            record.correspondenceAddress.value shouldBe LettingAddress(
              buildingNameNumber = addressLookupConfirmedAddress.address.lines.get.head,
              street1 = Some(addressLookupConfirmedAddress.address.lines.get.apply(1)),
              town = addressLookupConfirmedAddress.address.lines.get.last,
              county = None,
              postcode = addressLookupConfirmedAddress.address.postcode.get
            )
        }
      }
    }
  }