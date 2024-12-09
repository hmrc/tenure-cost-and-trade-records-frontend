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

import form.aboutthetradinghistory.CheckYourAnswersAdditionalActivitiesForm.checkYourAnswersAdditionalActivitiesForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, charset, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class CheckYourAnswersAdditionalActivitiesSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def controller(
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(prefilledAboutTheTradingHistoryPartOne)
  ) =
    new CheckYourAnswersAdditionalActivitiesController(
      stubMessagesControllerComponents(),
      aboutYourTradingHistoryNavigator,
      checkYourAnswersAdditionalActivitiesView,
      preEnrichedActionRefiner(aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne),
      mockSessionRepo
    )

  "Additional Activities CYA controller GET /" should {
    "return 200 and HTML when data present in session" in {
      val result = controller().show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when data is none in session" in {
      val controllerNoData = controller(aboutTheTradingHistoryPartOne = None)
      val result           = controllerNoData.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

  }

  "Additional Activities CYA controller SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = controller().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = controller().submit()(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "checkYourAnswersAdditionalActivities" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Additional Activities CYA controller" should {
    "error if form answer is missing and display correct error message" in {
      val formData = baseFormData - additionalActivitiesCYAErrorKey
      val form     = checkYourAnswersAdditionalActivitiesForm.bind(formData)

      mustContainError(additionalActivitiesCYAErrorKey, "error.checkYourAnswersRadio.required", form)
    }
  }

  object TestData {
    val additionalActivitiesCYAErrorKey: String = "checkYourAnswersAdditionalActivities"

    val baseFormData: Map[String, String] = Map("checkYourAnswersAdditionalActivities" -> "yes")
  }

}
