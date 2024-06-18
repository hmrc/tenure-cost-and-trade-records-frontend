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

import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartTwo}
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class HowIsCurrentRentFixedControllerSpec extends TestBaseSpec {

  def howIsCurrentRentFixedController(
    forType: String = ForTypes.for6010,
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne),
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) = new HowIsCurrentRentFixedController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    howIsCurrentRentFixedView,
    preEnrichedActionRefiner(
      forType = forType,
      aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne,
      aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo
    ),
    mockSessionRepo
  )

  "HowIsCurrentRentFixedController GET /" should {
    "return 200 and HTML with Rent Payable Vary On Quantity Of Beers Details Yes in the session for 6010" in {
      val result = howIsCurrentRentFixedController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersDetailsController.show().url
      )
    }

    "return 200 and HTML with Rent Payable Vary On Quantity Of Beers Details No in the session for 6010" in {
      val controller =
        howIsCurrentRentFixedController(aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwoNo))
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController.show().url
      )
    }

    "return 200 and HTML with no Rent Payable Vary On Quantity Of Beers Details in the session for 6010" in {
      val controller = howIsCurrentRentFixedController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 and HTML with Rent open market Yes in the session for other forms for 6020" in {
      val controller = howIsCurrentRentFixedController(forType = ForTypes.for6020)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show().url
      )
    }

    "return 200 and HTML with Rent open market No in the session for other forms for 6020" in {
      val controller = howIsCurrentRentFixedController(
        forType = ForTypes.for6020,
        aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOneNo)
      )
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController.show().url
      )
    }

    "return 200 and HTML with no Rent open market in the session for other forms for 6020" in {
      val controller = howIsCurrentRentFixedController(forType = ForTypes.for6020, aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 and HTML with Rent Payable Vary Gross Or Nets Yes in the session for other forms for 6015" in {
      val controller = howIsCurrentRentFixedController(forType = ForTypes.for6015)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetDetailsController.show().url
      )
    }

    "return 200 and HTML with Rent Payable Vary Gross Or Nets No in the session for other forms for 6015" in {
      val controller = howIsCurrentRentFixedController(
        forType = ForTypes.for6015,
        aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwoNo)
      )
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController.show().url
      )
    }

    "return 200 and HTML with no Rent Payable Vary Gross Or Nets in the session for other forms for 6015" in {
      val controller = howIsCurrentRentFixedController(forType = ForTypes.for6015, aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
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
