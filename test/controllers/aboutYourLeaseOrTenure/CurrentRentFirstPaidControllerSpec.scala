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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import org.jsoup.Jsoup

class CurrentRentFirstPaidControllerSpec extends TestBaseSpec {

  val mockAboutLeaseOrTenureNavigator = mock[AboutYourLeaseOrTenureNavigator]

  def currentRentFirstPaidController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new CurrentRentFirstPaidController(
      stubMessagesControllerComponents(),
      mockAboutLeaseOrTenureNavigator,
      currentRentFirstPaidView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def currentRentFirstPaidNoStartDate(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(
      prefilledAboutLeaseOrAgreementPartOneNoStartDate
    )
  ) =
    new CurrentRentFirstPaidController(
      stubMessagesControllerComponents(),
      mockAboutLeaseOrTenureNavigator,
      currentRentFirstPaidView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def currentRentFirstPaidController6011None = new CurrentRentFirstPaidController(
    stubMessagesControllerComponents(),
    mockAboutLeaseOrTenureNavigator,
    currentRentFirstPaidView,
    preEnrichedActionRefiner(forType = "FOR6011", aboutLeaseOrAgreementPartOne = None),
    mockSessionRepo
  )

  "CurrentRentFirstPaidController GET /" should {
    "return 200" in {
      val result = currentRentFirstPaidController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = currentRentFirstPaidController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML vacant property start date is not present in session" in {
      val result = currentRentFirstPaidNoStartDate().show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML with None in session for 6011" in {
      val result = currentRentFirstPaidController6011None.show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "display the page with the fields prefilled in" when {
      "exists within the session" in {
        val result = currentRentFirstPaidController().show()(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))
        Option(html.getElementById("currentRentFirstPaid.day").`val`()).value   shouldBe "1"
        Option(html.getElementById("currentRentFirstPaid.month").`val`()).value shouldBe "6"
        Option(html.getElementById("currentRentFirstPaid.year").`val`()).value  shouldBe "2022"
      }
    }
  }

  "CurrentRentFirstPaidController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = currentRentFirstPaidController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
