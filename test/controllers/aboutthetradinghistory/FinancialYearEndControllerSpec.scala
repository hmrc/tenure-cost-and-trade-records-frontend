/*
 * Copyright 2026 HM Revenue & Customs
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

import actions.SessionRequest
import connectors.Audit
import controllers.aboutthetradinghistory
import form.aboutthetradinghistory.AccountingInformationForm.accountingInformationForm
import models.ForType.*
import models.submissions.Form6010.DayMonthsDuration
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import models.{ForType, Session}
import play.api.data.Form
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.concurrent.Future
import scala.language.reflectiveCalls

class FinancialYearEndControllerSpec extends TestBaseSpec:

  import TestData.{baseFormData, errorKey}
  val mockAudit: Audit = mock[Audit]

  trait ControllerFixture:

    def financialYearEndController(
      forType: ForType = FOR6010,
      aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutYourTradingHistory)
    ): FinancialYearEndController = FinancialYearEndController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourTradingHistoryNavigator,
      financialYearEndView,
      preEnrichedActionRefiner(forType = forType, aboutTheTradingHistory = aboutTheTradingHistory),
      mockSessionRepo
    )

  "About your trading history controller" should {
    "return 200" in new ControllerFixture {
      val result: Future[Result] = financialYearEndController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in new ControllerFixture {
      val result: Future[Result] = financialYearEndController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in new ControllerFixture {
        val res: Future[Result] = financialYearEndController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Financial year end form" should {
    "error if financial year end day and month are missing " in new ControllerFixture {
      val formData: Map[String, String]            = baseFormData - errorKey.financialYearDay - errorKey.financialYearMonth
      val form: Form[(DayMonthsDuration, Boolean)] = accountingInformationForm(using messages).bind(formData)

      mustContainError(errorKey.financialYearDay, "error.date.required", form)
    }

    "error if financial year day is missing " in new ControllerFixture {
      val formData: Map[String, String]            = baseFormData - errorKey.financialYearDay
      val form: Form[(DayMonthsDuration, Boolean)] = accountingInformationForm(using messages).bind(formData)

      mustContainError(errorKey.financialYearDay, "error.date.mustInclude", form)
    }

    "error if financial year month is missing" in new ControllerFixture {
      val formData: Map[String, String]            = baseFormData - errorKey.financialYearMonth
      val form: Form[(DayMonthsDuration, Boolean)] = accountingInformationForm(using messages).bind(formData)

      mustContainError(errorKey.financialYearMonth, "error.date.mustInclude", form)
    }

    "error if financial year date is incorrect" in new ControllerFixture {
      val formData: Map[String, String]            = baseFormData.updated("financialYear.day", "31").updated("financialYear.month", "2")
      val form: Form[(DayMonthsDuration, Boolean)] = accountingInformationForm(using messages).bind(formData)

      mustContainError(errorKey.financialYearDay, "error.date.invalid", form)
    }

    "error if financial year day is incorrect" in new ControllerFixture {
      val formData: Map[String, String]            = baseFormData.updated("financialYear.day", "32")
      val form: Form[(DayMonthsDuration, Boolean)] = accountingInformationForm(using messages).bind(formData)

      mustContainError(errorKey.financialYearDay, "error.date.day.invalid", form)
    }

    "error if financial year month is incorrect" in new ControllerFixture {
      val formData: Map[String, String]            = baseFormData.updated("financialYear.month", "13")
      val form: Form[(DayMonthsDuration, Boolean)] = accountingInformationForm(using messages).bind(formData)

      mustContainError(errorKey.financialYearMonth, "error.date.month.invalid", form)
    }
  }

  "submit" should {
    "redirect to the next page when valid data is submitted" in new ControllerFixture {
      // Arrange
      val validFormData: Map[String, String]               = Map(
        "financialYear.day"   -> "1",
        "financialYear.month" -> "4",
        "yearEndChanged"      -> "true"
      )
      val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/your-route").withFormUrlEncodedBody(validFormData.toSeq*)
      when(mockSessionRepo.saveOrUpdate(any[Session])(using any[HeaderCarrier]))
        .thenReturn(Future.unit)

      // Act
      val result: Future[Result] = financialYearEndController().submit(request)

      // Assert
      status(result)                 shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.FinancialYearEndDatesSummaryController.show().url
    }
    "redirect to the next page when valid 6030 data is submitted" in new ControllerFixture {
      // Arrange
      val validFormData: Map[String, String]                         = Map(
        "financialYear.day"   -> "1",
        "financialYear.month" -> "4",
        "yearEndChanged"      -> "true"
      )
      val request: FakeRequest[AnyContentAsFormUrlEncoded]           = FakeRequest(POST, "/your-route").withFormUrlEncodedBody(validFormData.toSeq*)
      val sessionRequest: SessionRequest[AnyContentAsFormUrlEncoded] = SessionRequest(aboutYourTradingHistory6030YesSession, request)
      when(mockSessionRepo.saveOrUpdate(any[Session])(using any[HeaderCarrier]))
        .thenReturn(Future.unit)

      // Act
      val result: Future[Result] =
        financialYearEndController(FOR6030, Some(prefilledAboutYourTradingHistory6030)).submit(sessionRequest)

      // Assert
      status(result)                 shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.FinancialYearEndDatesSummaryController.show().url
    }

    "redirect to the next page when valid 6020 data is submitted" in new ControllerFixture {
      // Arrange
      val validFormData: Map[String, String]                         = Map(
        "financialYear.day"   -> "1",
        "financialYear.month" -> "4",
        "yearEndChanged"      -> "true"
      )
      val request: FakeRequest[AnyContentAsFormUrlEncoded]           = FakeRequest(POST, "/your-route").withFormUrlEncodedBody(validFormData.toSeq*)
      val sessionRequest: SessionRequest[AnyContentAsFormUrlEncoded] = SessionRequest(aboutYourTradingHistory6020YesSession, request)
      when(mockSessionRepo.saveOrUpdate(any[Session])(using any[HeaderCarrier]))
        .thenReturn(Future.unit)

      // Act
      val result: Future[Result] =
        financialYearEndController(FOR6020, Some(prefilledAboutYourTradingHistory6020)).submit(sessionRequest)

      // Assert
      status(result)                 shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.FinancialYearEndDatesSummaryController.show().url
    }

    "redirect to the next page when valid 6045 data is submitted" in new ControllerFixture {
      val validFormData: Map[String, String]                         = Map(
        "financialYear.day"   -> "25",
        "financialYear.month" -> "4",
        "yearEndChanged"      -> "true"
      )
      val session6045                                                = aboutYourTradingHistory6045YesSession
      val request: FakeRequest[AnyContentAsFormUrlEncoded]           = FakeRequest(POST, "/").withFormUrlEncodedBody(validFormData.toSeq*)
      val sessionRequest: SessionRequest[AnyContentAsFormUrlEncoded] = SessionRequest(session6045, request)

      when(mockSessionRepo.saveOrUpdate(any[Session])(using any[HeaderCarrier]))
        .thenReturn(Future.unit)

      val result: Future[Result] =
        financialYearEndController(session6045.forType, session6045.aboutTheTradingHistory).submit(sessionRequest)

      status(result)                 shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.FinancialYearEndDatesSummaryController.show().url
    }

    "redirect to the next page when valid 6048 data is submitted" in new ControllerFixture {
      val validFormData: Map[String, String]                         = Map(
        "financialYear.day"   -> "25",
        "financialYear.month" -> "4",
        "yearEndChanged"      -> "true"
      )
      val session6048                                                = aboutYourTradingHistory6048YesSession
      val request: FakeRequest[AnyContentAsFormUrlEncoded]           = FakeRequest(POST, "/").withFormUrlEncodedBody(validFormData.toSeq*)
      val sessionRequest: SessionRequest[AnyContentAsFormUrlEncoded] = SessionRequest(session6048, request)

      when(mockSessionRepo.saveOrUpdate(any[Session])(using any[HeaderCarrier]))
        .thenReturn(Future.unit)

      val result: Future[Result] =
        financialYearEndController(session6048.forType, session6048.aboutTheTradingHistory).submit(sessionRequest)

      status(result)                 shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.FinancialYearEndDatesSummaryController.show().url
    }

    "redirect to the next page when valid 6076 data is submitted" in new ControllerFixture {
      val validFormData: Map[String, String]                         = Map(
        "financialYear.day"   -> "6",
        "financialYear.month" -> "4",
        "yearEndChanged"      -> "true"
      )
      val session6076                                                = aboutYourTradingHistory6076YesSession
      val request: FakeRequest[AnyContentAsFormUrlEncoded]           = FakeRequest(POST, "/").withFormUrlEncodedBody(validFormData.toSeq*)
      val sessionRequest: SessionRequest[AnyContentAsFormUrlEncoded] = SessionRequest(session6076, request)

      when(mockSessionRepo.saveOrUpdate(any[Session])(using any[HeaderCarrier]))
        .thenReturn(Future.unit)

      val result: Future[Result] =
        financialYearEndController(session6076.forType, session6076.aboutTheTradingHistory).submit(sessionRequest)

      status(result)                 shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.FinancialYearEndDatesSummaryController.show().url
    }
  }

  object TestData:
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey:
      val financialYearDay   = "financialYear.day"
      val financialYearMonth = "financialYear.month"

    val baseFormData: Map[String, String] = Map(
      "financialYear.day"   -> "27",
      "financialYear.month" -> "9"
    )
