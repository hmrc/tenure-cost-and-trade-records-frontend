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
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.test.FakeRequest
import utils.TestBaseSpec
import play.api.test.Helpers._

class TypeOfLettingControllerSpec extends TestBaseSpec {

  def typeOfLettingController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(
      prefilledAboutFranchiseOrLettingsWith6020LettingsAll
    )
  ) =
    new TypeOfLettingController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      typeOfLettingView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = forType6020),
      mockSessionRepo
    )

  "GET /"    should {
    "return 200" in {
      val result = typeOfLettingController().show(Some(0))(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = typeOfLettingController().show(Some(0))(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render back link to CYA if come from CYA" in {
      val result  = typeOfLettingController().show(Some(0))(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-franchise-or-lettings")
      content should not include "/financial-year-end"
    }
  }
  "SUBMIT /" should {
    "throw a BAD_REQUEST on empty form submission" in {

      val res = typeOfLettingController().submit(Some(0))(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "process data correctly when from is 'CYA' via form submission" in {
    val controller     = typeOfLettingController()
    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfLetting" -> "automatedTellerMachine", "from" -> "CYA")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6020Session, request)
    val result         = controller.submit(Some(0))(sessionRequest)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/check-your-answers-about-franchise-or-lettings"
    )
  }
  "update letting and redirect to telcomMast when data is different" in {
    val controller     = typeOfLettingController()
    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfLetting" -> "telecomMast", "from" -> "CYA")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6020Session, request)
    val result         = controller.submit(Some(0))(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/telecom-mast-letting?idx=0")
    verify(mockSessionRepo).saveOrUpdate(any)(any, any)
  }

  "update letting and redirect to ATM when not from CYA" in {
    val controller     = typeOfLettingController()
    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfLetting" -> "automatedTellerMachine")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6020Session, request)
    val result         = controller.submit(Some(0))(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/atm-letting?idx=0")
  }

  "update letting and redirect to Advertising Right when not from CYA" in {
    val controller     = typeOfLettingController()
    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfLetting" -> "advertisingRight")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6020Session, request)
    val result         = controller.submit(Some(0))(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/advertising-right-letting?idx=0")
  }

  "update letting and redirect to Other Letting when not from CYA" in {
    val controller     = typeOfLettingController()
    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfLetting" -> "other")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6020Session, request)
    val result         = controller.submit(Some(0))(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/other-letting?idx=0")
  }
}
