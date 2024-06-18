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

package controllers.aboutthetradinghistory

import form.aboutthetradinghistory.OccupationalInformationForm.occupationalInformationForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import utils.FormBindingTestAssertions.mustContainError

class AboutYourTradingHistoryControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def aboutYourTradingHistoryController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutYourTradingHistory)
  ) = new AboutYourTradingHistoryController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    aboutYourTradingHistoryView,
    preEnrichedActionRefiner(aboutTheTradingHistory = aboutTheTradingHistory),
    mockSessionRepo
  )

  "About your trading history controller" should {
    "return 200" in {
      val result = aboutYourTradingHistoryController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = aboutYourTradingHistoryController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = aboutYourTradingHistoryController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = aboutYourTradingHistoryController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
      status(res) shouldBe BAD_REQUEST
    }
  }

  "About your trading history form" should {
    "error if first occupy month and year are missing " in {
      val formData = baseFormData - errorKey.occupyMonth - errorKey.occupyYear
      val form     = occupationalInformationForm(messages).bind(formData)

      mustContainError(errorKey.occupyMonth, "error.date.required", form)
    }

    "error if first occupy month is missing " in {
      val formData = baseFormData - errorKey.occupyMonth
      val form     = occupationalInformationForm(messages).bind(formData)

      mustContainError(errorKey.occupyMonth, "error.date.mustInclude", form)
    }

    "error if first occupy year is missing" in {
      val formData = baseFormData - errorKey.occupyYear
      val form     = occupationalInformationForm(messages).bind(formData)

      mustContainError(errorKey.occupyYear, "error.date.mustInclude", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val occupyMonth: String
      val occupyYear: String
      val financialYearDay: String
      val financialYearMonth: String
    } = new {
      val occupyMonth        = "firstOccupy.month"
      val occupyYear         = "firstOccupy.year"
      val financialYearDay   = "financialYear.day"
      val financialYearMonth = "financialYear.month"
    }

    val baseFormData: Map[String, String] = Map(
      "firstOccupy.month" -> "9",
      "firstOccupy.year"  -> "2017"
    )

  }
}
