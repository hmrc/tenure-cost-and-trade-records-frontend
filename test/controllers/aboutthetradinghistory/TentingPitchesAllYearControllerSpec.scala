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

import form.aboutthetradinghistory.TentingPitchesAllYearForm.tentingPitchesAllYearForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import models.submissions.common.AnswerNo
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class TentingPitchesAllYearControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def tentingPitchesAllYearController(
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(prefilledAboutTheTradingHistoryPartOne)
  ) = new TentingPitchesAllYearController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    tentingPitchesAllYearView,
    preEnrichedActionRefiner(aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne),
    mockSessionRepo
  )

  "TentingPitchesAllYearController GET /" should {
    "return 200 and HTML when data present in session" in {
      val result = tentingPitchesAllYearController().show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when data is none in session" in {
      val controller = tentingPitchesAllYearController(aboutTheTradingHistoryPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = tentingPitchesAllYearController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }

    //    "return correct backLink when 'from=CYA' query param is present" in {
    //      val result = tentingPitchesAllYearController().show()(FakeRequest(GET, "/path?from=CYA"))
    //      contentAsString(result) should include(controllers.aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController.show().url)
    //    }

  }

  "TentingPitchesAllYearController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = tentingPitchesAllYearController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "TentingPitchesAllYearController" should {
    "error if TentingPitchesAllYearController answer is missing" in {
      val formData = baseFormData - errorKey.tentingPitchesAllYear
      val form     = tentingPitchesAllYearForm.bind(formData)

      mustContainError(errorKey.tentingPitchesAllYear, "error.areYourPitchesOpen.missing", form)
    }
  }

  "Form validation" should {

    val baseData: Map[String, String] = Map(
      "tentingPitchesAllYear" -> AnswerNo.name
    )

    "error if weekOfPitchesUse is invalid" in {
      val formData = baseData + ("weekOfPitchesUse" -> "xxx")
      val form     = tentingPitchesAllYearForm.bind(formData)
      mustContainError("weekOfPitchesUse", "error.areYourPitchesOpen.conditional.value.invalid", form)
    }

    "error if weekOfPitchesUse is missing" in {
      val formData = baseData + ("vatValue" -> "")
      val form     = tentingPitchesAllYearForm.bind(formData)
      mustContainError("weekOfPitchesUse", "error.areYourPitchesOpen.conditional.value.missing", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val tentingPitchesAllYear: String
    } = new {
      val tentingPitchesAllYear: String = "tentingPitchesAllYear"
    }

    val baseFormData: Map[String, String] = Map("tentingPitchesAllYear" -> "yes")
  }

}
