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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class HowIsCurrentRentFixedControllerSpec extends TestBaseSpec {

  def howIsCurrentRentFixedController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new HowIsCurrentRentFixedController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      howIsCurrentRentFixedView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  def howIsCurrentRentFixedControllerNo(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwoNo)
  ) =
    new HowIsCurrentRentFixedController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      howIsCurrentRentFixedView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  def howIsCurrentRentFixedControllerNone =
    new HowIsCurrentRentFixedController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      howIsCurrentRentFixedView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = None),
      mockSessionRepo
    )

  def howIsCurrentRentFixedController6015(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new HowIsCurrentRentFixedController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      howIsCurrentRentFixedView,
      preEnrichedActionRefiner(forType = "FOR6015", aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  def howIsCurrentRentFixedController6015No(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwoNo)
  ) =
    new HowIsCurrentRentFixedController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      howIsCurrentRentFixedView,
      preEnrichedActionRefiner(forType = "FOR6015", aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  def howIsCurrentRentFixedController6015None =
    new HowIsCurrentRentFixedController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      howIsCurrentRentFixedView,
      preEnrichedActionRefiner(forType = "FOR6015", aboutLeaseOrAgreementPartTwo = None),
      mockSessionRepo
    )

  "HowIsCurrentRentFixedController GET /" should {
    "return 200 vacant property yes in session" in {
      val result = howIsCurrentRentFixedController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML vacant property yes in session" in {
      val result = howIsCurrentRentFixedController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML vacant property no in session" in {
      val result = howIsCurrentRentFixedControllerNo().show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML vacant property none in session" in {
      val result = howIsCurrentRentFixedControllerNone.show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML vacant property yes in session for 6015" in {
      val result = howIsCurrentRentFixedController6015().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML vacant property no in session for 6015" in {
      val result = howIsCurrentRentFixedController6015No().show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML vacant property none in session for 6015" in {
      val result = howIsCurrentRentFixedController6015None.show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "display the page with the fields prefilled in" when {
      "exists within the session" in {
        val result = howIsCurrentRentFixedController().show()(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))
        Option(html.getElementById("rentActuallyAgreed.day").`val`()).value   shouldBe "1"
        Option(html.getElementById("rentActuallyAgreed.month").`val`()).value shouldBe "6"
        Option(html.getElementById("rentActuallyAgreed.year").`val`()).value  shouldBe "2022"
      }
    }
  }

  "HowIsCurrentRentFixedController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = howIsCurrentRentFixedController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
