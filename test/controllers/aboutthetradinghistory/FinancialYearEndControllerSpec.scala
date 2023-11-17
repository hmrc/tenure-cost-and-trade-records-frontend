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

import form.aboutthetradinghistory.AccountingInformationForm.accountingInformationForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class FinancialYearEndControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def financialYearEndController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutYourTradingHistory)
  ) = new FinancialYearEndController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    financialYearEndView,
    preEnrichedActionRefiner(aboutTheTradingHistory = aboutTheTradingHistory),
    mockSessionRepo
  )

  "About your trading history controller" should {
    "return 200" in {
      val result = financialYearEndController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = financialYearEndController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = financialYearEndController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Financial year end form" should {
    "error if financial year end day and month are missing " in {
      val formData = baseFormData - errorKey.financialYearDay - errorKey.financialYearMonth
      val form     = accountingInformationForm(messages).bind(formData)

      mustContainError(errorKey.financialYearDay, "error.date.required", form)
    }

    "error if financial year day is missing " in {
      val formData = baseFormData - errorKey.financialYearDay
      val form     = accountingInformationForm(messages).bind(formData)

      mustContainError(errorKey.financialYearDay, "error.date.mustInclude", form)
    }

    "error if financial year month is missing" in {
      val formData = baseFormData - errorKey.financialYearMonth
      val form     = accountingInformationForm(messages).bind(formData)

      mustContainError(errorKey.financialYearMonth, "error.date.mustInclude", form)
    }

    "error if financial year date is incorrect" in {
      val formData = baseFormData.updated("financialYear.day", "31").updated("financialYear.month", "2")
      val form     = accountingInformationForm(messages).bind(formData)

      mustContainError(errorKey.financialYearDay, "error.date.invalid", form)
    }

    "error if financial year day is incorrect" in {
      val formData = baseFormData.updated("financialYear.day", "32")
      val form     = accountingInformationForm(messages).bind(formData)

      mustContainError(errorKey.financialYearDay, "error.date.day.invalid", form)
    }

    "error if financial year month is incorrect" in {
      val formData = baseFormData.updated("financialYear.month", "13")
      val form     = accountingInformationForm(messages).bind(formData)

      mustContainError(errorKey.financialYearMonth, "error.date.month.invalid", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val financialYearDay: String
      val financialYearMonth: String
    } = new {
      val financialYearDay   = "financialYear.day"
      val financialYearMonth = "financialYear.month"
    }

    val baseFormData: Map[String, String] = Map(
      "financialYear.day"   -> "27",
      "financialYear.month" -> "9"
    )

  }
}
