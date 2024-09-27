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
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class TypeOfIncomeControllerSpec extends TestBaseSpec {

  def typeOfIncomeController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(
      prefilledAboutFranchiseOrLettings6045
    )
  ) =
    new TypeOfIncomeController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      typeOfIncomeView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = forType6045),
      mockSessionRepo
    )

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

  // TODO when the new journey cya ready

//  "process data correctly when from is 'CYA' via form submission" in {
//    val controller     = typeOfLettingController()
//    val request        = FakeRequest(POST, "/submit-path")
//      .withFormUrlEncodedBody("typeOfIncome" -> "typeOfIncome", "from" -> "CYA")
//    val sessionRequest = SessionRequest(session, request)
//    val result         = controller.submit(Some(0))(sessionRequest)
//    status(result)           shouldBe SEE_OTHER
//    redirectLocation(result) shouldBe Some(
//      "/send-trade-and-cost-information/check-your-answers-about-franchise-or-lettings"
//    )
//  }
  "update income and redirect to concession/franchise details if type selected is concessionFranchise type" in {
    val controller     = typeOfIncomeController()
    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfIncome" -> "typeConcessionOrFranchise")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6045, request)
    val result         = controller.submit(Some(0))(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/catering-operation-business-details?idx=0")
    verify(mockSessionRepo).saveOrUpdate(any)(any, any)
  }

  "update income and redirect to letting details if type selected is letting type" in {
    val controller     = typeOfIncomeController()
    val request        = FakeRequest(POST, "/submit-path")
      .withFormUrlEncodedBody("typeOfIncome" -> "typeLetting")
    val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6045, request)
    val result         = controller.submit(Some(0))(sessionRequest)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/letting-other-part-of-property-details?idx=0"
    )
  }

}