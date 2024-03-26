/*
 * Copyright 2023 HM Revenue & Customs
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

import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartTwo}
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class IntervalsOfRentReviewControllerSpec extends TestBaseSpec {

  def intervalsOfRentReviewController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new IntervalsOfRentReviewController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      intervalsOfRentReviewView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  def intervalsOfRentReviewNoStartDate(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(
      prefilledAboutLeaseOrAgreementPartTwoNoDate
    )
  ) =
    new IntervalsOfRentReviewController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      intervalsOfRentReviewView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = intervalsOfRentReviewController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = intervalsOfRentReviewController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 vacant property start date is not present in session" in {
      val result = intervalsOfRentReviewNoStartDate().show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
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

  "SUBMIT /" should {
    "throw a See_Other if an empty form is submitted" in {

      val res = intervalsOfRentReviewController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
