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

package controllers.aboutYourLeaseOrTenure

import connectors.Audit
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class PayACapitalSumDetailsControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def payACapitalSumDetailsController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new PayACapitalSumDetailsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      payACapitalSumDetailsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "PayACapitalSumDetailsController GET /" should {
    "return 200 and HTML with capital sum or premium with yes in the session" in {
      val result = payACapitalSumDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
      )
    }

    "return 200 and HTML with capital sum or premium with no in the session" in {
      val controller =
        payACapitalSumDetailsController(aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwoNo))
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
      )
    }

    "return 200 and HTML capital sum or premium with none in the session" in {
      val controller = payACapitalSumDetailsController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = payACapitalSumDetailsController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url + "#pay-a-capital-sum-details"
      )
    }

    "display the page with the fields prefilled in" when {
      "exists within the session" in {
        val result = payACapitalSumDetailsController().show()(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))
        Option(html.getElementById("capitalSumPaidDetailsDateInput.day").`val`()).value   shouldBe "1"
        Option(html.getElementById("capitalSumPaidDetailsDateInput.month").`val`()).value shouldBe "6"
        Option(html.getElementById("capitalSumPaidDetailsDateInput.year").`val`()).value  shouldBe "2022"
      }
    }
  }

  "PayACapitalSumDetailsController SUBMIT /" should {
    "throw a See_Other if an empty form is submitted" in {
      val res = payACapitalSumDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
