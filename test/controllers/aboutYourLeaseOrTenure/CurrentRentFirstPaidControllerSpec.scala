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

import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import org.jsoup.Jsoup

class CurrentRentFirstPaidControllerSpec extends TestBaseSpec {

  def currentRentFirstPaidController = new CurrentRentFirstPaidController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    currentRentFirstPaidView,
    preEnrichedActionRefiner(),
    mockSessionRepo
  )

  def currentRentFirstPaidNoStartDate = new CurrentRentFirstPaidController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    currentRentFirstPaidView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOneNoStartDate)),
    mockSessionRepo
  )

  def currentRentFirstPaidController6011None = new CurrentRentFirstPaidController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    currentRentFirstPaidView,
    preEnrichedActionRefiner(forType = "FOR6011", aboutLeaseOrAgreementPartOne = None),
    mockSessionRepo
  )

  def currentRentFirstPaidController6020 = new CurrentRentFirstPaidController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    currentRentFirstPaidView,
    preEnrichedActionRefiner(forType = "FOR6020"),
    mockSessionRepo
  )

  def currentRentFirstPaidController6020None = new CurrentRentFirstPaidController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    currentRentFirstPaidView,
    preEnrichedActionRefiner(forType = "FOR6020", aboutLeaseOrAgreementPartThree = None),
    mockSessionRepo
  )

  "CurrentRentFirstPaidController GET /" should {
    "return 200 and HTML with Current Rent First Paid in the session" in {
      val result = currentRentFirstPaidController.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show().url
      )
    }

    "return 200 and HTML with no Current Rent First Paid in the session" in {
      val result = currentRentFirstPaidNoStartDate.show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show().url
      )
    }

    "return 200 and HTML when None in session for 6011" in {
      val result = currentRentFirstPaidController6011None.show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludesVatController.show().url
      )
    }

    "return 200 and HTML with throughput affect rent, does rent vary in the session for 6020" in {
      val result = currentRentFirstPaidController6020.show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ThroughputAffectsRentDetailsController.show().url
      )
    }

    "return 200 and HTML when no throughput affect rent, does rent vary in the session for 6020" in {
      val result = currentRentFirstPaidController6020None.show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ThroughputAffectsRentController.show().url
      )
    }

    "display the page with the fields prefilled in" when {
      "exists within the session" in {
        val result = currentRentFirstPaidController.show()(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))
        Option(html.getElementById("currentRentFirstPaid.day").`val`()).value   shouldBe "1"
        Option(html.getElementById("currentRentFirstPaid.month").`val`()).value shouldBe "6"
        Option(html.getElementById("currentRentFirstPaid.year").`val`()).value  shouldBe "2022"
      }
    }
  }

  "CurrentRentFirstPaidController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = currentRentFirstPaidController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
