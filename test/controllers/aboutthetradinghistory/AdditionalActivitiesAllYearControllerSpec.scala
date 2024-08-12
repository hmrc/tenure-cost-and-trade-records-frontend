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

import form.aboutthetradinghistory.AdditionalActivitiesAllYearForm.additionalActivitiesAllYearForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import models.submissions.common.AnswerNo
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class AdditionalActivitiesAllYearControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def additionalActivitiesAllYearController(
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(prefilledAboutTheTradingHistoryPartOne)
  ) = new AdditionalActivitiesAllYearController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    additionalActivitiesAllYearView,
    preEnrichedActionRefiner(aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne),
    mockSessionRepo
  )

  "TentingPitchesAllYearController GET /" should {
    "return 200 and HTML when data present in session" in {
      val result = additionalActivitiesAllYearController().show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when data is none in session" in {
      val controller = additionalActivitiesAllYearController(aboutTheTradingHistoryPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = additionalActivitiesAllYearController().show()(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController.show().url
      )
    }

    "return correct backLink when no query param in url" in {
      val result = additionalActivitiesAllYearController().show(fakeRequest)
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.AdditionalActivitiesOnSiteController.show().url
      )
    }

  }

  "AdditionalActivitiesAllYearController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = additionalActivitiesAllYearController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "AdditionalActivitiesAllYearController" should {
    "error if form answer is missing" in {
      val formData = baseFormData - additionalActivitiesAllYearErrorKey
      val form     = additionalActivitiesAllYearForm.bind(formData)

      mustContainError(additionalActivitiesAllYearErrorKey, "error.additionalActivitiesAllYear.missing", form)
    }
  }

  "Form validation" should {

    val baseData: Map[String, String] = Map(
      "additionalActivitiesAllYear" -> AnswerNo.name
    )

    "error if weekOfPitchesUse is invalid" in {
      val formData = baseData + ("weekOfActivitiesUse" -> "xxx")
      val form     = additionalActivitiesAllYearForm.bind(formData)
      mustContainError("weekOfActivitiesUse", "error.additionalActivitiesAllYear.conditional.value.invalid", form)
    }

    "error if weekOfPitchesUse is missing" in {
      val formData = baseData + ("weekOfActivitiesUse" -> "")
      val form     = additionalActivitiesAllYearForm.bind(formData)
      mustContainError("weekOfActivitiesUse", "error.additionalActivitiesAllYear.conditional.value.missing", form)
    }
  }

  object TestData {
    val additionalActivitiesAllYearErrorKey: String = "additionalActivitiesAllYear"

    val baseFormData: Map[String, String] = Map("additionalActivitiesAllYear" -> "yes")
  }

}
