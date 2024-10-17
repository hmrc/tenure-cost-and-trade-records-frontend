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

import actions.SessionRequest
import models.ForType.*
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.mvc.request.RequestTarget
import utils.TestBaseSpec
import play.api.test.Helpers.*
import play.api.test.FakeRequest

class RentDetailsControllerSpec extends TestBaseSpec {

  def rentDetailsController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(
      prefilledAboutFranchiseOrLettingsWith6020LettingsAll
    )
  ) = new RentDetailsController(
    stubMessagesControllerComponents(),
    aboutFranchisesOrLettingsNavigator,
    rentDetailsView,
    preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = FOR6020),
    mockSessionRepo
  )

  "GET /" should {
    "return 200 for ATM rent details" in {
      val result = rentDetailsController().show(0)(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = rentDetailsController().show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 for telco Mast rent details" in {
      val result = rentDetailsController().show(1)(fakeRequest)
      status(result) shouldBe OK
    }

    "return 200 for advertising right rent details" in {
      val result = rentDetailsController().show(2)(fakeRequest)
      status(result) shouldBe OK
    }

    "return 200 for other rent details" in {
      val result = rentDetailsController().show(3)(fakeRequest)
      status(result) shouldBe OK
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST on empty form submission" in {

      val res = rentDetailsController().submit(0)(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
    "throw a BAD_REQUEST on empty form submission from CYA" in {

      val res = rentDetailsController().submit(0)(
        FakeRequest().withFormUrlEncodedBody("from" -> "CYA")
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "handle form submission with valid atm rent details and update the session" in {
    val validData      = Map(
      "annualRent"      -> "1000",
      "dateInput.day"   -> "15",
      "dateInput.month" -> "5",
      "dateInput.year"  -> "2020"
    )
    val request        = FakeRequest(POST, "/rent-details-submit").withFormUrlEncodedBody(validData.toSeq*)
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6020Session, request)
    val controller     = rentDetailsController()
    val result         = controller.submit(0)(sessionRequest)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/add-another-letting?idx=3"
    ) // Adjust according to your actual redirect

  }

  "handle form submission with valid telco rent details and update the session" in {
    val validData      = Map(
      "annualRent"      -> "1000",
      "dateInput.day"   -> "15",
      "dateInput.month" -> "5",
      "dateInput.year"  -> "2020"
    )
    val request        = FakeRequest(POST, "/rent-details-submit").withFormUrlEncodedBody(validData.toSeq*)
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6020Session, request)
    val controller     = rentDetailsController()
    val result         = controller.submit(2)(sessionRequest)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/add-another-letting?idx=3"
    ) // Adjust according to your actual redirect

  }

  "handle form submission with valid advertising right rent details and update the session" in {
    val validData      = Map(
      "annualRent"      -> "1000",
      "dateInput.day"   -> "15",
      "dateInput.month" -> "5",
      "dateInput.year"  -> "2020"
    )
    val request        = FakeRequest(POST, "/rent-details-submit").withFormUrlEncodedBody(validData.toSeq*)
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6020Session, request)
    val controller     = rentDetailsController()
    val result         = controller.submit(2)(sessionRequest)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/add-another-letting?idx=3"
    ) // Adjust according to your actual redirect

  }

  "handle form submission with valid other rent details and update the session" in {
    val validData      = Map(
      "annualRent"      -> "1000",
      "dateInput.day"   -> "15",
      "dateInput.month" -> "5",
      "dateInput.year"  -> "2020"
    )
    val request        = FakeRequest(POST, "/rent-details-submit").withFormUrlEncodedBody(validData.toSeq*)
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6020Session, request)
    val controller     = rentDetailsController()
    val result         = controller.submit(2)(sessionRequest)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/add-another-letting?idx=3"
    ) // Adjust according to your actual redirect
  }

  "handle form submission redirect to CYA if come from CYA" in {
    val validData = Map(
      "annualRent"      -> "1000",
      "dateInput.day"   -> "15",
      "dateInput.month" -> "5",
      "dateInput.year"  -> "2020"
    )
    val request   = FakeRequest(POST, "/rent-details-submit")
      .withTarget(
        RequestTarget("", "", Map("from" -> Seq("CYA")))
      )
      .withFormUrlEncodedBody(validData.toSeq*)

    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6020Session, request)
    val controller     = rentDetailsController()
    val result         = controller.submit(2)(sessionRequest)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    )
  }

}
