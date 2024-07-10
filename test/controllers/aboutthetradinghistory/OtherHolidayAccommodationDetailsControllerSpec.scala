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

package controllers.aboutthetradinghistory

import form.aboutthetradinghistory.OtherHolidayAccommodationDetailsForm.otherHolidayAccommodationDetailsForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class OtherHolidayAccommodationDetailsControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._
  def otherHolidayAccommodationDetailsController(
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(
      prefilledAboutTheTradingHistoryPartOne
    )
  ) = new OtherHolidayAccommodationDetailsController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    otherHolidayAccommodationDetailsView,
    preEnrichedActionRefiner(aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne),
    mockSessionRepo
  )

  "OtherHolidayAccommodationDetailsController GET /" should {
    "return 200 and HTML" in {
      val result = otherHolidayAccommodationDetailsController().show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML if the session is empty" in {
      val controller = otherHolidayAccommodationDetailsController(aboutTheTradingHistoryPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = otherHolidayAccommodationDetailsController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.OtherHolidayAccommodationController.show().url
      )
    }

  }

  "OtherHolidayAccommodationDetailsController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = otherHolidayAccommodationDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "OtherHolidayAccommodationDetails" should {
    "error if otherHolidayAccommodationOpenAllYear answer is missing" in {
      val formData = baseFormData - errorKey.otherHolidayAccommodationOpenAllYear
      val form     = otherHolidayAccommodationDetailsForm(messages).bind(formData)

      mustContainError(
        errorKey.otherHolidayAccommodationOpenAllYear,
        "error.otherHolidayAccommodationOpenAllYear.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: Object {
      val otherHolidayAccommodationOpenAllYear: String
    } = new {
      val otherHolidayAccommodationOpenAllYear: String = "otherHolidayAccommodationOpenAllYear"
    }

    val baseFormData: Map[String, String] = Map("otherHolidayAccommodationOpenAllYear" -> "yes")
  }

}
