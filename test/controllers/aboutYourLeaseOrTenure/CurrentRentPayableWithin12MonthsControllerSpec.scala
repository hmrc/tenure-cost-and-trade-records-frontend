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

import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class CurrentRentPayableWithin12MonthsControllerSpec extends TestBaseSpec {

  def currentRentPayableWithin12MonthsController = new CurrentRentPayableWithin12MonthsController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    currentRentPayableWithin12MonthsView,
    preEnrichedActionRefiner(),
    mockSessionRepo
  )

  def currentRentPayableWithin12MonthsNoStartDate = new CurrentRentPayableWithin12MonthsController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    currentRentPayableWithin12MonthsView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOneNoStartDate)),
    mockSessionRepo
  )

  "CurrentRentPayableWithin12MonthsController GET /" should {
    "return 200 and HTML with Current Rent Payable Within 12 Months in the session" in {
      val result = currentRentPayableWithin12MonthsController.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show().url
      )
    }

    "return 200 and HTML when no Vacant Property Start Date in the session" in {
      val result = currentRentPayableWithin12MonthsNoStartDate.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show().url
      )
    }

    "display the page with the fields prefilled in" when {
      "exists within the session" in {
        val result = currentRentPayableWithin12MonthsController.show()(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))
        Option(html.getElementById("dateReview.day").`val`()).value   shouldBe "1"
        Option(html.getElementById("dateReview.month").`val`()).value shouldBe "6"
        Option(html.getElementById("dateReview.year").`val`()).value  shouldBe "2022"
      }
    }
  }

  "CurrentRentPayableWithin12MonthsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = currentRentPayableWithin12MonthsController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
