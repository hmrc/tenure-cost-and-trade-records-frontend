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
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, LettingAddress, TelecomMastLetting}
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, *}
import repositories.SessionRepo
import utils.{JsoupHelpers, TestBaseSpec}

import scala.concurrent.Future.successful

class TelecomMastLettingControllerSpec
    extends TestBaseSpec
    with TelecomMastLettingControllerBehaviours
    with JsoupHelpers:

  trait ControllerFixture extends MockAddressLookup:
    val repository = mock[SessionRepo]
    when(repository.saveOrUpdate(any[Session])(any, any)).thenReturn(successful(()))
    val controller = new TelecomMastLettingController(
      stubMessagesControllerComponents(),
      mock[Audit],
      aboutFranchisesOrLettingsNavigator,
      telecomMastLettingView,
      preEnrichedActionRefiner(
        forType = FOR6020,
        aboutFranchisesOrLettings = Some(
          prefilledAboutFranchiseOrLettingsWith6020LettingsAll
        )
      ),
      addressLookupConnector,
      repository
    )

  "the TelecomMastLetting controller" when {
    "handling GET requests"            should {
      "reply 200 with a fresh HTML form" in new ControllerFixture {
        val result = controller.show(index = Some(5))(fakeRequest)
        val html   = contentAsJsoup(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading                     shouldBe "label.telecomMastLetting.heading"
        page.input("operatingCompanyName") should beEmpty
        page.input("siteOfMast")           should beEmpty
      }
      "reply 200 with a pre-filled HTML form if given a known index" in new ControllerFixture {
        val result = controller.show(index = Some(1))(fakeRequest)
        val page   = contentAsJsoup(result)
        page.input("operatingCompanyName") should haveValue("Vodafone")
        page.input("siteOfMast")           should haveValue("roof")
      }
      "render back link to CYA if come from CYA" in new ControllerFixture {
        val result = controller.show(Some(0))(fakeRequestFromCYA)
        val page   = contentAsJsoup(result)
        page.backLink shouldBe routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      }
    }
    "handling POST requests"           should {
      "throw a BAD_REQUEST if an empty form is submitted" in new ControllerFixture {
        val result = controller.submit(index = Some(0))(
          FakeRequest().withFormUrlEncodedBody(Seq.empty*)
        )
        status(result) shouldBe BAD_REQUEST
        val page = contentAsJsoup(result)
        page.error("operatingCompanyName") shouldBe "error.operatingCompanyName.required"
        page.error("siteOfMast")           shouldBe "error.siteOfMast.required"
      }
      behave like savingLettingRecordAndRedirectingToAddressLookupService(index = 1)
    }
    "retrieving the confirmed address" should {
      behave like retrievingConfirmedAddressFromAddressLookupService(index = 1)
    }
  }

trait TelecomMastLettingControllerBehaviours:
  this: TelecomMastLettingControllerSpec =>

  def savingLettingRecordAndRedirectingToAddressLookupService(index: Int) =
    s"save record at index=$index and reply 303 and redirect to address lookup page" in new ControllerFixture {
      val operatingCompanyName = "Bread and Butter Ltd"
      val siteOfMast           = "Terrace"
      val result               = controller.submit(Some(index))(
        fakePostRequest.withFormUrlEncodedBody(
          "operatingCompanyName" -> operatingCompanyName,
          "siteOfMast"           -> siteOfMast
        )
      )
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe "/on-ramp"
      val session = captor[Session]
      verify(repository, once).saveOrUpdate(session.capture())(any, any)
      inside(session.getValue.aboutFranchisesOrLettings.value.lettings.value.apply(index)) {
        case record: TelecomMastLetting =>
          record.operatingCompanyName.value shouldBe operatingCompanyName
          record.siteOfMast.value           shouldBe siteOfMast
      }
    }

  def retrievingConfirmedAddressFromAddressLookupService(index: Int) =
    s"save record at index=$index and reply 303 redirect to the next page" in new ControllerFixture {
      val result = controller.addressLookupCallback(index, "confirmedAddress")(fakeRequest)
      status(result)                 shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.RentDetailsController.show(index).url

      val id = captor[String]
      verify(addressLookupConnector, once).getConfirmedAddress(id)(any)
      id.getValue shouldBe "confirmedAddress"

      val session = captor[Session]
      verify(repository, once).saveOrUpdate(session)(any, any)
      inside(session.getValue.aboutFranchisesOrLettings.value.lettings.value.apply(index)) {
        case record: TelecomMastLetting =>
          record.correspondenceAddress.value shouldBe LettingAddress(
            buildingNameNumber = addressLookupConfirmedAddress.address.lines.get.head,
            street1 = Some(addressLookupConfirmedAddress.address.lines.get.apply(1)),
            town = addressLookupConfirmedAddress.address.lines.get.last,
            county = None,
            postcode = addressLookupConfirmedAddress.address.postcode.get
          )
      }
    }
