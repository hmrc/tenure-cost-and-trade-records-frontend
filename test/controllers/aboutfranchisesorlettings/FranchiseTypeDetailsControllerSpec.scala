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
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, BusinessAddress, Concession6015IncomeRecord, FranchiseIncomeRecord}
import models.submissions.common.AnswerYes
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepo
import utils.{JsoupHelpers, TestBaseSpec}

import scala.concurrent.Future.successful

class FranchiseTypeDetailsControllerSpec
    extends TestBaseSpec
    with FranchiseTypeDetailsControllerBehaviours
    with JsoupHelpers:

  "the FranchiseTypeDetails controller" when {
    "handling GET requests"            should {
      "reply 200 with a fresh HTML form" in new ControllerFixture {
        val result = controller.show(0)(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading shouldBe "cateringOperationOrLettingAccommodationOperator.heading"
      }
      "reply 200 with a fresh HTML form if given unknown index" in new ControllerFixture {
        val result = controller.show(2)(fakeRequest)
        val page   = contentAsJsoup(result)
        page.input("operatorName")   should beEmpty
        page.input("typeOfBusiness") should beEmpty
      }
      "reply 200 with a pre-filled HTML form if given a known index" in new ControllerFixture {
        val result = controller.show(0)(fakeRequest)
        val page   = contentAsJsoup(result)
        page.input("operatorName")   should haveValue(concession6015IncomeRecord.businessDetails.get.operatorName)
        page.input("typeOfBusiness") should haveValue(concession6015IncomeRecord.businessDetails.get.typeOfBusiness)
      }
      "render back link to CYA if request comes from CYA" in new ControllerFixture {
        val result = controller.show(0)(fakeRequestFromCYA)
        val page   = contentAsJsoup(result)
        page.backLink shouldBe routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      }
      "render a correct back link to type of income page if no query parameters in the url " in new ControllerFixture {
        val result = controller.show(0)(fakeRequest)
        val page   = contentAsJsoup(result)
        page.backLink shouldBe routes.TypeOfIncomeController.show(idx = Some(0)).url
      }
    }
    "handling POST requests"           should {
      "throw a BAD_REQUEST if an empty form is submitted" in new ControllerFixture {
        val result = controller.submit(0)(
          FakeRequest().withFormUrlEncodedBody(Seq.empty*)
        )
        status(result) shouldBe BAD_REQUEST
        val page = contentAsJsoup(result)
        page.error("operatorName")   shouldBe "error.operatorName.required"
        page.error("typeOfBusiness") shouldBe "error.typeOfBusiness.required"
      }
      behave like savingIncomeRecordAndRedirectingToAddressLookupService(0)
      behave like savingIncomeRecordAndRedirectingToAddressLookupService(1)
    }
    "retrieving the confirmed address" should {
      behave like retrievingConfirmedAddressFromAddressLookupService(index = 0)
      behave like retrievingConfirmedAddressFromAddressLookupService(index = 1)
    }
  }

  trait ControllerFixture extends MockAddressLookup:
    val repository = mock[SessionRepo]
    when(repository.saveOrUpdate(any[Session])(using any)).thenReturn(successful(()))
    val controller = new FranchiseTypeDetailsController(
      stubMessagesControllerComponents(),
      mock[Audit],
      aboutFranchisesOrLettingsNavigator,
      franchiseTypeDetailsView,
      preEnrichedActionRefiner(
        aboutFranchisesOrLettings = Some(
          AboutFranchisesOrLettings(
            Some(AnswerYes),
            rentalIncome = Some(
              IndexedSeq(concession6015IncomeRecord, franchiseIncomeRecord)
            )
          )
        )
      ),
      addressLookupConnector,
      repository
    )

trait FranchiseTypeDetailsControllerBehaviours:
  this: FranchiseTypeDetailsControllerSpec =>

  def savingIncomeRecordAndRedirectingToAddressLookupService(index: Int): Unit =
    s"save record at index=$index and reply 303 and redirect to address lookup page" in new ControllerFixture {
      val operatorName   = "Godzilla"
      val typeOfBusiness = "Atomic Bomb Factory"
      val result         = controller.submit(index)(
        fakePostRequest.withFormUrlEncodedBody(
          "operatorName"   -> operatorName,
          "typeOfBusiness" -> typeOfBusiness
        )
      )
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe "/on-ramp"
      val session = captor[Session]
      verify(repository, once).saveOrUpdate(session.capture())(using any)
      inside(session.getValue.aboutFranchisesOrLettings.value.rentalIncome.value.apply(index)) {
        case record: Concession6015IncomeRecord =>
          record.businessDetails.value.operatorName   shouldBe operatorName
          record.businessDetails.value.typeOfBusiness shouldBe typeOfBusiness
        case record: FranchiseIncomeRecord      =>
          record.businessDetails.value.operatorName   shouldBe operatorName
          record.businessDetails.value.typeOfBusiness shouldBe typeOfBusiness
      }
    }

  def retrievingConfirmedAddressFromAddressLookupService(index: Int): Unit =
    s"save record at index=$index and reply 303 redirect to the next page" in new ControllerFixture {
      val result = controller.addressLookupCallback(index, "confirmedAddress")(fakeRequest)
      status(result)                 shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.RentalIncomeRentController.show(0).url

      val id = captor[String]
      verify(addressLookupConnector, once).getConfirmedAddress(id)(using any)
      id.getValue shouldBe "confirmedAddress"

      val session = captor[Session]
      verify(repository, once).saveOrUpdate(session)(using any)
      inside(session.getValue.aboutFranchisesOrLettings.value.rentalIncome.value.apply(index)) {
        case record: Concession6015IncomeRecord =>
          record.businessDetails.value.cateringAddress.value shouldBe BusinessAddress(
            buildingNameNumber = addressLookupConfirmedAddress.address.lines.get.head,
            street1 = Some(addressLookupConfirmedAddress.address.lines.get.apply(1)),
            town = addressLookupConfirmedAddress.address.lines.get.last,
            county = None,
            postcode = addressLookupConfirmedAddress.address.postcode.get
          )
        case record: FranchiseIncomeRecord      =>
          record.businessDetails.value.cateringAddress.value shouldBe BusinessAddress(
            buildingNameNumber = addressLookupConfirmedAddress.address.lines.get.head,
            street1 = Some(addressLookupConfirmedAddress.address.lines.get.apply(1)),
            town = addressLookupConfirmedAddress.address.lines.get.last,
            county = None,
            postcode = addressLookupConfirmedAddress.address.postcode.get
          )
      }
    }
