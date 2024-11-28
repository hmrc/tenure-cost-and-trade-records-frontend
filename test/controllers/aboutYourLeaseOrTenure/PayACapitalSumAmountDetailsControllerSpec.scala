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

package controllers.aboutYourLeaseOrTenure

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class PayACapitalSumAmountDetailsControllerSpec extends TestBaseSpec {

  def payACapitalSumAmountDetailsController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new PayACapitalSumAmountDetailsController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      payACapitalSumAmountDetailsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "payACapitalSumAmountDetailsController GET /" should {
    "return 200 and HTML with pay a capital sum amount in the session" in {
      val result = payACapitalSumAmountDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = payACapitalSumAmountDetailsController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url + "#pay-a-capital-sum-details"
      )
    }

    "CurrentAnnualRentController123 SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted123" in {
        val res = payACapitalSumAmountDetailsController().submit(
          FakeRequest().withFormUrlEncodedBody(Seq.empty*)
        )
        status(res) shouldBe BAD_REQUEST
      }

      "Redirect when form data currentAnnualRent submitted" in {
        val res = payACapitalSumAmountDetailsController().submit(
          FakeRequest(POST, "/").withFormUrlEncodedBody("capitalSumPaidDetails" -> "1234")
        )
        status(res) shouldBe SEE_OTHER
      }
    }
  }
}
