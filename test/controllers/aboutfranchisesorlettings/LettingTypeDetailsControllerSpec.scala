/*
 * Copyright 2026 HM Revenue & Customs
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

import connectors.Audit
import models.Session
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, LettingIncomeRecord}
import models.submissions.common.Address
import models.submissions.common.AnswersYesNo.*
import org.jsoup.nodes.Document
import org.mockito.ArgumentCaptor
import org.scalatest.Inside
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepo
import test.{JsoupHelpers, MockAddressLookup}
import test.ControllerSpec

import scala.concurrent.Future

class LettingTypeDetailsControllerSpec extends ControllerSpec with LettingTypeDetailsControllerBehaviours with JsoupHelpers with Inside:

  "the LettingTypeDetails controller" when {
    "handling GET requests"  should {
      "reply 200 with a fresh HTML form" in new ControllerFixture {
        val result: Future[Result] = controller.show(0)(getRequest)
        status(result)          shouldBe OK
        contentType(result).get shouldBe HTML
        charset(result).get     shouldBe UTF8

        val page: Document = contentAsJsoup(result)
        page.heading shouldBe "lettingOtherPartOfPropertyDetailsTenants.heading"
      }

      "reply 200 with a fresh HTML form if given unknown index" in new ControllerFixture {
        val result: Future[Result] = controller.show(2)(getRequest)
        val page: Document         = contentAsJsoup(result)
        page.input("lettingOperatorName")   should beEmpty
        page.input("lettingTypeOfBusiness") should beEmpty
      }

      "render back link to CYA if request comes from CYA" in new ControllerFixture {
        val result: Future[Result] = controller.show(0)(getRequestFromCYA)
        val page: Document         = contentAsJsoup(result)
        page.backLink shouldBe routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      }

      "render a correct back link to type of income page if no query parameters in the url " in new ControllerFixture {
        val result: Future[Result] = controller.show(0)(getRequest)
        val page: Document         = contentAsJsoup(result)
        page.backLink shouldBe routes.TypeOfIncomeController.show(idx = Some(0)).url
      }
    }
    "handling POST requests" should {
      "throw a BAD_REQUEST if an empty form is submitted" in new ControllerFixture {
        val res: Future[Result] = controller.submit(0)(
          FakeRequest().withFormUrlEncodedBody(Seq.empty*)
        )
        status(res) shouldBe BAD_REQUEST
      }
      behave like savingIncomeRecordAndRedirectingToAddressLookupService(1)
    }

    "retrieving the confirmed address" should {
      behave like retrievingConfirmedAddressFromAddressLookupService(index = 1)
    }
  }

  trait ControllerFixture extends MockAddressLookup:
    val repository: SessionRepo = mock[SessionRepo]
    when(repository.saveOrUpdate(any[Session])(using any)).thenReturn(Future.unit)

    val controller: LettingTypeDetailsController = LettingTypeDetailsController(
      stubMessagesControllerComponents(),
      mock[Audit],
      aboutFranchisesOrLettingsNavigator,
      lettingTypeDetailsView,
      preEnrichedActionRefiner(
        aboutFranchisesOrLettings = Some(
          AboutFranchisesOrLettings(
            Some(AnswerYes),
            rentalIncome = Some(
              IndexedSeq(concessionIncomeRecord, lettingIncomeRecord)
            )
          )
        )
      ),
      addressLookupConnector,
      repository
    )

trait LettingTypeDetailsControllerBehaviours:
  this: LettingTypeDetailsControllerSpec =>

  def savingIncomeRecordAndRedirectingToAddressLookupService(index: Int): Unit =
    s"save record at index=$index and reply 303 and redirect to address lookup page" in new ControllerFixture {
      val operatorName           = "Godzilla"
      val typeOfBusiness         = "Atomic Bomb Factory"
      val result: Future[Result] = controller.submit(index)(
        postRequest.withFormUrlEncodedBody(
          "lettingOperatorName"   -> operatorName,
          "lettingTypeOfBusiness" -> typeOfBusiness
        )
      )
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get shouldBe "/on-ramp"

      val session: ArgumentCaptor[Session] = captor[Session]
      verify(repository, once).saveOrUpdate(session.capture())(using any)
      inside(session.getValue.aboutFranchisesOrLettings.get.rentalIncome.get.apply(index)) {
        case record: LettingIncomeRecord =>
          record.operatorDetails.get.operatorName   shouldBe operatorName
          record.operatorDetails.get.typeOfBusiness shouldBe typeOfBusiness
      }
    }

  def retrievingConfirmedAddressFromAddressLookupService(index: Int): Unit =
    s"save record at index=$index and reply 303 redirect to the next page" in new ControllerFixture {
      val result: Future[Result] = controller.addressLookupCallback(index, "confirmedAddress")(getRequest)
      status(result)               shouldBe SEE_OTHER
      redirectLocation(result).get shouldBe routes.RentalIncomeRentController.show(0).url

      val id: ArgumentCaptor[String] = captor[String]
      verify(addressLookupConnector, once).getConfirmedAddress(id)(using any)
      id.getValue shouldBe "confirmedAddress"

      val session: ArgumentCaptor[Session] = captor[Session]
      verify(repository, once).saveOrUpdate(session)(using any)
      inside(session.getValue.aboutFranchisesOrLettings.get.rentalIncome.get.apply(index)) {
        case record: LettingIncomeRecord =>
          record.operatorDetails.get.lettingAddress.get shouldBe Address(
            buildingNameNumber = addressLookupConfirmedAddress.address.lines.get.head,
            street1 = Some(addressLookupConfirmedAddress.address.lines.get.apply(1)),
            town = addressLookupConfirmedAddress.address.lines.get.last,
            county = None,
            postcode = addressLookupConfirmedAddress.address.postcode.get
          )
      }
    }
