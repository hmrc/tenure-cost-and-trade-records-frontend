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
import form.aboutYourLeaseOrTenure.IntervalsOfRentReviewForm.intervalsOfRentReviewForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.FormBindingTestAssertions.*
import utils.TestBaseSpec

class IntervalsOfRentReviewControllerSpec extends TestBaseSpec {

  val test2001character = "x" * 2001

  val mockAudit: Audit = mock[Audit]

  def intervalsOfRentReviewController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new IntervalsOfRentReviewController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      intervalsOfRentReviewView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "IntervalsOfRentReviewController GET /" should {
    "return 200 and HTML with vacant property start date present in session" in {
      val result = intervalsOfRentReviewController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsRentReviewPlannedController.show().url
      )
    }

    "return 200 and HTML vacant property start date is not present in session" in {
      val controller = intervalsOfRentReviewController(aboutLeaseOrAgreementPartTwo =
        Some(prefilledAboutLeaseOrAgreementPartTwoNoDate)
      )
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsRentReviewPlannedController.show().url
      )
    }

    "return 200 and HTML vacant property start date is none in session" in {
      val controller = intervalsOfRentReviewController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsRentReviewPlannedController.show().url
      )
    }

    "display the page with the fields prefilled in" when {
      "exists within the session" in {
        val result = intervalsOfRentReviewController().show()(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))
        Option(html.getElementById("nextReview.day").`val`()).value   shouldBe "1"
        Option(html.getElementById("nextReview.month").`val`()).value shouldBe "6"
        Option(html.getElementById("nextReview.year").`val`()).value  shouldBe "2022"
      }
    }
  }

  "IntervalsOfRentReviewController SUBMIT /" should {
    "throw a See_Other if an empty form is submitted" in {
      val res = intervalsOfRentReviewController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe SEE_OTHER
    }

    "throw a BAD_REQUEST if intervalsOfRentReview is greater than max length is submitted" in {
      val res = intervalsOfRentReviewController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "intervalsOfRentReview" -> test2001character
        )
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Form validation" should {

    val baseData: Map[String, String] = Map(
      "intervalsOfRentReview" -> "Intervals of rent review"
    )

    "error if intervalsOfRentReview is greater than max length" in {
      val formData = baseData + ("intervalsOfRentReview" -> test2001character)
      val form     = intervalsOfRentReviewForm(using messages).bind(formData)
      mustContainError("intervalsOfRentReview", "error.intervalsOfRent.maxLength", form)
    }
  }
}
