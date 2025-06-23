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
import models.ForType.*
import models.Session
import models.submissions.aboutfranchisesorlettings.OtherLetting
import models.submissions.common.Address
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.{JsoupHelpers, TestBaseSpec}

import scala.concurrent.Future.successful

class OtherLettingControllerSpec extends TestBaseSpec with JsoupHelpers:

  trait ControllerFixture(havingNoLettings: Boolean = false) extends MockAddressLookup:
    val repository = mock[SessionRepo]
    when(repository.saveOrUpdate(any[Session])(using any)).thenReturn(successful(()))
    val controller = new OtherLettingController(
      stubMessagesControllerComponents(),
      mock[Audit],
      aboutFranchisesOrLettingsNavigator,
      otherLettingView,
      preEnrichedActionRefiner(
        forType = FOR6020,
        aboutFranchisesOrLettings = Some(
          prefilledAboutFranchiseOrLettingsWith6020LettingsAll.copy(
            lettings =
              if havingNoLettings
              then None
              else prefilledAboutFranchiseOrLettingsWith6020LettingsAll.lettings
          )
        )
      ),
      addressLookupConnector,
      repository
    )

  "the OtherLetting controller" when {
    "handling GET requests"            should {
      "reply 200 with a fresh HTML form" in new ControllerFixture {
        val result = controller.show(index = Some(5))(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading            shouldBe "label.otherLetting.heading"
        page.input("lettingType") should beEmpty
        page.input("tenantName")  should beEmpty
      }
      "reply 200 with a pre-filled HTML form if given a known index" in new ControllerFixture {
        val result = controller.show(index = Some(3))(fakeRequest)
        val page   = contentAsJsoup(result)
        page.input("lettingType") should haveValue("Charging point")
        page.input("tenantName")  should haveValue("Tesla")
      }
      "render back link to CYA if come from CYA" in new ControllerFixture {
        val result = controller.show(Some(3))(fakeRequestFromCYA)
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
        page.error("lettingType") shouldBe "error.lettingType.required"
        page.error("tenantName")  shouldBe "error.tenantName.required"
      }
      "save new record and reply 303 and redirect to address lookup page" in new ControllerFixture(havingNoLettings =
        true
      ) {
        val lettingType = "New garage"
        val tenantName  = "Henry VIII"
        val result      = controller.submit(index = Some(0))(
          fakePostRequest.withFormUrlEncodedBody(
            "lettingType" -> lettingType,
            "tenantName"  -> tenantName
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/on-ramp"
        val session = captor[Session]
        verify(repository, once).saveOrUpdate(session.capture())(using any)
        inside(session.getValue.aboutFranchisesOrLettings.value.lettings.value.apply(0)) { case record: OtherLetting =>
          record.lettingType.value shouldBe lettingType
          record.tenantName.value  shouldBe tenantName
        }
      }
      "update existing record and reply 303 and redirect to address lookup page" in new ControllerFixture {
        val lettingType = "Turned into New garage"
        val tenantName  = "Charles I"
        val result      = controller.submit(index = Some(3))(
          fakePostRequest.withFormUrlEncodedBody(
            "lettingType" -> lettingType,
            "tenantName"  -> tenantName
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/on-ramp"
        val session = captor[Session]
        verify(repository, once).saveOrUpdate(session.capture())(using any)
        inside(session.getValue.aboutFranchisesOrLettings.value.lettings.value.apply(3)) { case record: OtherLetting =>
          record.lettingType.value shouldBe lettingType
          record.tenantName.value  shouldBe tenantName
        }
      }
    }
    "retrieving the confirmed address" should {
      "save record and reply 303 redirect to the next page" in new ControllerFixture {
        val result = controller.addressLookupCallback(idx = 3, "confirmedAddress")(fakeRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.RentDetailsController.show(idx = 3).url

        val id = captor[String]
        verify(addressLookupConnector, once).getConfirmedAddress(id)(using any[HeaderCarrier])
        id.getValue shouldBe "confirmedAddress"

        val session = captor[Session]
        verify(repository, once).saveOrUpdate(session)(using any)
        inside(session.getValue.aboutFranchisesOrLettings.value.lettings.value.apply(3)) { case record: OtherLetting =>
          record.correspondenceAddress.value shouldBe Address(
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
