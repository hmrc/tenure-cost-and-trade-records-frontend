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

import connectors.Audit
import models.ForType.*
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import utils.TestBaseSpec
import play.api.test.Helpers._
import play.api.test.FakeRequest

class AtmLettingControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]
  def atmLettingController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(
      prefilledAboutFranchiseOrLettingsWith6020LettingsAll
    )
  ) = new AtmLettingController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutFranchisesOrLettingsNavigator,
    atmLettingView,
    preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = FOR6020),
    mockSessionRepo
  )

  "GET /"    should {
    "return 200" in {
      val result = atmLettingController().show(Some(0))(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = atmLettingController().show(Some(0))(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render back link to CYA if come from CYA Baseload" in {
      val result  = atmLettingController().show(Some(0))(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-franchise-or-lettings")
      content should not include "/financial-year-end"
    }
  }
  "SUBMIT /" should {
    "throw a BAD_REQUEST on empty form submission" in {
      val res = atmLettingController().submit(Some(0))(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "throw a BAD_REQUEST on empty form submission from CYA" in {

      val res = atmLettingController().submit(Some(0))(
        FakeRequest().withFormUrlEncodedBody("from" -> "CYA")
      )
      status(res) shouldBe BAD_REQUEST
    }

  }

  "handle valid form submissions by updating session data and redirecting correctly" in {
    val validFormData = Map(
      "bankOrCompany"                            -> "Bank A",
      "correspondenceAddress.buildingNameNumber" -> "123",
      "correspondenceAddress.street1"            -> "Main St",
      "correspondenceAddress.town"               -> "Townsville",
      "correspondenceAddress.county"             -> "Countyshire",
      "correspondenceAddress.postcode"           -> "DD11 DD"
    )
    val request       = FakeRequest(POST, "/atm-letting-submit").withFormUrlEncodedBody(validFormData.toSeq*)
    val controller    = atmLettingController()

    val result = controller.submit(Some(0))(request)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/rent-details?idx=0")
  }

  "handle valid form submissions by adding new ATM to session data and redirecting correctly" in {
    val validFormData = Map(
      "bankOrCompany"                            -> "Bank A",
      "correspondenceAddress.buildingNameNumber" -> "123",
      "correspondenceAddress.street1"            -> "Main St",
      "correspondenceAddress.town"               -> "Townsville",
      "correspondenceAddress.county"             -> "Countyshire",
      "correspondenceAddress.postcode"           -> "DD11 DD"
    )
    val request       = FakeRequest(POST, "/atm-letting-submit").withFormUrlEncodedBody(validFormData.toSeq*)
    val controller    = atmLettingController()

    val result = controller.submit(Some(4))(request)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/rent-details?idx=4")
  }
}
