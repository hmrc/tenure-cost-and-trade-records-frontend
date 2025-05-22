/*
 * Copyright 2025 HM Revenue & Customs
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
import form.aboutthetradinghistory.OccupationalInformationForm.occupationalInformationForm
import models.ForType
import models.ForType.*
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec
import utils.FormBindingTestAssertions.mustContainError

import scala.language.reflectiveCalls

class WhenDidYouFirstOccupyControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  val mockAudit: Audit = mock[Audit]
  def aboutYourTradingHistoryController(
    forType: ForType = FOR6010,
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutYourTradingHistory)
  ) = new WhenDidYouFirstOccupyController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourTradingHistoryNavigator,
    whenDidYouFistOccupyView,
    preEnrichedActionRefiner(
      forType = forType,
      aboutTheTradingHistory = aboutTheTradingHistory
    ),
    mockSessionRepo
  )

  "About your trading history controller" should {
    "return 200" in {
      val result = aboutYourTradingHistoryController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 for 6048" in {
      val session6048    = aboutYourTradingHistory6048YesSession
      val sessionRequest = SessionRequest(session6048, FakeRequest())

      val result =
        aboutYourTradingHistoryController(session6048.forType, session6048.aboutTheTradingHistory).show(sessionRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = aboutYourTradingHistoryController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML for 6045" in {
      val controller = aboutYourTradingHistoryController(forType = FOR6045)
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.WhatYouWillNeedController.show().url
      )
    }

    "return 200 and HTML for 6076" in {
      val controller = aboutYourTradingHistoryController(forType = FOR6076)
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.WhatYouWillNeedController.show().url
      )
    }

    "return 200 and HTML for 6048" in {
      val controller = aboutYourTradingHistoryController(forType = FOR6048)
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.AreYouVATRegisteredController.show.url
      )
    }

    "return 200 and HTML when the session is None" in {
      val controller = aboutYourTradingHistoryController(aboutTheTradingHistory = None)
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.WhatYouWillNeedController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = aboutYourTradingHistoryController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.WhatYouWillNeedController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = aboutYourTradingHistoryController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = aboutYourTradingHistoryController().submit(
        FakeRequest(POST, "/path?from=TL").withFormUrlEncodedBody(
          "firstOccupy.month" -> "4",
          "firstOccupy.year"  -> "2021"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "redirect to the next page for 6048" in {
      val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
        .withFormUrlEncodedBody(baseFormData.toSeq*)

      val sessionRequest =
        SessionRequest(
          aboutYourTradingHistory6048YesSession,
          requestWithForm
        )

      val result = aboutYourTradingHistoryController().submit(sessionRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(aboutthetradinghistory.routes.FinancialYearEndController.show().url)
    }
  }

  "About your trading history form" should {
    "error if first occupy month and year are missing " in {
      val formData = baseFormData - errorKey.occupyMonth - errorKey.occupyYear
      val form     = occupationalInformationForm(using messages).bind(formData)

      mustContainError(errorKey.occupyMonth, "error.date.required", form)
    }

    "error if first occupy month is missing " in {
      val formData = baseFormData - errorKey.occupyMonth
      val form     = occupationalInformationForm(using messages).bind(formData)

      mustContainError(errorKey.occupyMonth, "error.date.mustInclude", form)
    }

    "error if first occupy year is missing" in {
      val formData = baseFormData - errorKey.occupyYear
      val form     = occupationalInformationForm(using messages).bind(formData)

      mustContainError(errorKey.occupyYear, "error.date.mustInclude", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
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
