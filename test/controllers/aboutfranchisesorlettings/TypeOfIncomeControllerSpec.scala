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

import actions.SessionRequest
import connectors.Audit
import models.ForType.*
import models.submissions.aboutfranchisesorlettings.*
import models.submissions.aboutfranchisesorlettings.TypeOfIncome.*
import play.api.http.Status.*
import play.api.libs.json.{JsError, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.TestBaseSpec
import utils.FakeObjects

class TypeOfIncomeControllerSpec extends TestBaseSpec with FakeObjects {

  val mockAudit: Audit = mock[Audit]
  def typeOfIncomeController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(
      prefilledAboutFranchiseOrLettings6045
    )
  ) =
    new TypeOfIncomeController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutFranchisesOrLettingsNavigator,
      typeOfIncomeView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = FOR6045),
      mockSessionRepo
    )

  "IncomeRecord" should {
    "serialize and deserialize correctly for FranchiseIncomeRecord with complete details" in {
      val incomeRecord = franchiseIncomeRecord
      val json         = Json.toJson(incomeRecord: FranchiseIncomeRecord)
      json.as[FranchiseIncomeRecord] shouldBe incomeRecord
    }

    "serialize and deserialize correctly for Concession6015IncomeRecord with complete details" in {
      val incomeRecord = concession6015IncomeRecord
      val json         = Json.toJson(incomeRecord: Concession6015IncomeRecord)
      json.as[Concession6015IncomeRecord] shouldBe incomeRecord
    }

    "serialize and deserialize correctly for ConcessionIncomeRecord with complete details" in {
      val incomeRecord = concessionIncomeRecord
      val json         = Json.toJson(incomeRecord: ConcessionIncomeRecord)
      json.as[ConcessionIncomeRecord] shouldBe incomeRecord
    }

    "serialize and deserialize correctly for LettingIncomeRecord with complete details" in {
      val incomeRecord = lettingIncomeRecord
      val json         = Json.toJson(incomeRecord: LettingIncomeRecord)
      json.as[LettingIncomeRecord] shouldBe incomeRecord
    }

    "serialize and deserialize correctly for ConcessionIncomeRecord with optional fields missing" in {
      val incomeRecord = ConcessionIncomeRecord(
        sourceType = TypeConcession,
        businessDetails = None,
        feeReceived = None
      )
      val json         = Json.toJson(incomeRecord: IncomeRecord)
      json.as[IncomeRecord] shouldBe incomeRecord
    }

    "serialize and deserialize correctly for Concession6015IncomeRecord with optional fields missing" in {
      val incomeRecord = Concession6015IncomeRecord(
        sourceType = TypeConcession6015,
        businessDetails = None,
        rent = None
      )
      val json         = Json.toJson(incomeRecord: IncomeRecord)
      json.as[IncomeRecord] shouldBe incomeRecord
    }

    "serialize and deserialize correctly FranchiseIncomeRecord with optional fields missing" in {
      val incomeRecord = FranchiseIncomeRecord(
        sourceType = TypeFranchise,
        businessDetails = None
      )
      val json         = Json.toJson(incomeRecord: IncomeRecord)
      json.as[IncomeRecord] shouldBe incomeRecord
    }

    "serialize and deserialize correctly for LettingIncomeRecord with optional fields missing" in {
      val incomeRecord = LettingIncomeRecord(
        sourceType = TypeLetting,
        operatorDetails = None,
        rent = None,
        itemsIncluded = None
      )
      val json         = Json.toJson(incomeRecord: IncomeRecord)
      json.as[IncomeRecord] shouldBe incomeRecord
    }

    "fail to deserialize for unknown sourceType" in {
      val json = Json.obj("sourceType" -> "unknownType")
      json.validate[IncomeRecord] shouldBe a[JsError]
    }
  }

  "GET /"    should {
    "return 200" in {
      val result = typeOfIncomeController().show(Some(0))(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = typeOfIncomeController().show(Some(0))(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render back link to CYA if come from CYA" in {
      val result  = typeOfIncomeController().show(Some(0))(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-franchise-or-lettings")
      content should not include "/financial-year-end"
    }

    "render a  back link to 'franchise-or-lettings-tied-to-property' " in {
      val result  = typeOfIncomeController().show(Some(0))(fakeRequest)
      val content = contentAsString(result)
      content should include("/franchise-or-lettings-tied-to-property")
    }
  }
  "SUBMIT /" should {
    "throw a BAD_REQUEST on empty form submission" in {

      val res = typeOfIncomeController().submit(Some(0))(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "update income and redirect to franchise details if type selected is concession6015 type" in {
    val controller     = typeOfIncomeController()
    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfIncome" -> "typeConcession6015")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6015YesSession, request)
    val result         = controller.submit(Some(0))(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/franchise-type-details?idx=0")
    verify(mockSessionRepo).saveOrUpdate(any)(using any)
  }

  "update income and redirect to franchise details if type selected is Franchise type" in {
    val controller     = typeOfIncomeController()
    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfIncome" -> "typeFranchise")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6010YesSession, request)
    val result         = controller.submit(Some(0))(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/franchise-type-details?idx=0")
    verify(mockSessionRepo, times(2)).saveOrUpdate(any)(using any)
  }

  "update income and redirect to concession details if type selected is concession type" in {
    val controller     = typeOfIncomeController()
    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfIncome" -> "typeConcession")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6045, request)
    val result         = controller.submit(Some(0))(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/concession-type-details?idx=0")
    verify(mockSessionRepo, times(3)).saveOrUpdate(any)(using any)
  }

  "update income and redirect to letting details if type selected is letting type" in {
    val controller     = typeOfIncomeController()
    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfIncome" -> "typeLetting")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6045, request)
    val result         = controller.submit(Some(0))(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/letting-type-details?idx=0"
    )
  }
  "redirect to MaxOfLettingsReachedController when rental income records exceed the limit for letting" in {
    val maxRentalRecords = IndexedSeq.fill(5)(LettingIncomeRecord(sourceType = TypeLetting))
    val controller       = typeOfIncomeController(
      Option(
        prefilledAboutFranchiseOrLettings6045.copy(
          rentalIncome = Option(maxRentalRecords)
        )
      )
    )

    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfIncome" -> "typeLetting")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6045, request)
    val result         = controller.submit(None)(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Option("/send-trade-and-cost-information/max-lettings?src=typeOfIncome")

  }

  "redirect to MaxOfLettingsReachedController when rental income records exceed the limit for concession" in {
    val maxRentalRecords = IndexedSeq.fill(5)(ConcessionIncomeRecord(sourceType = TypeConcession))
    val controller       = typeOfIncomeController(
      Option(
        prefilledAboutFranchiseOrLettings6045.copy(
          rentalIncome = Option(maxRentalRecords)
        )
      )
    )

    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfIncome" -> "typeConcession")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6045, request)
    val result         = controller.submit(None)(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Option("/send-trade-and-cost-information/max-lettings?src=typeOfIncome")
  }
}
