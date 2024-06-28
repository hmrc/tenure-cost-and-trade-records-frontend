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

import form.aboutthetradinghistory.OtherHolidayAccommodationForm.otherHolidayAccommodationForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class OtherHolidayAccommodationControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._
  def otherHolidayAccommodationController(
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(
      prefilledAboutTheTradingHistoryPartOne
    )
  ) = new OtherHolidayAccommodationController(
    otherHolidayAccommodationView,
    aboutYourTradingHistoryNavigator,
    preEnrichedActionRefiner(aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "OtherHolidayAccommodationController GET /" should {
    "return 200 and HTML with is parking rent paid separately is present in session" in {
      val result = otherHolidayAccommodationController().show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML is parking rent paid separately is none in session" in {
      val controller = otherHolidayAccommodationController(aboutTheTradingHistoryPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = otherHolidayAccommodationController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }

  }

  "OtherHolidayAccommodationController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = otherHolidayAccommodationController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "OtherHolidayAccommodation" should {
    "error if OtherHolidayAccommodation answer is missing" in {
      val formData = baseFormData - errorKey.otherHolidayAccommodation
      val form     = otherHolidayAccommodationForm.bind(formData)

      mustContainError(errorKey.otherHolidayAccommodation, "error.otherHolidayAccommodation.required", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val otherHolidayAccommodation: String
    } = new {
      val otherHolidayAccommodation: String = "otherHolidayAccommodation"
    }

    val baseFormData: Map[String, String] = Map("otherHolidayAccommodation" -> "yes")
  }

}
