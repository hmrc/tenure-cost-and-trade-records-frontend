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

import form.aboutthetradinghistory.AdditionalActivitiesOnSiteForm.additionalActivitiesOnSiteForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, POST, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class AdditionalActivitiesOnSiteControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def additionalActivitiesOnSiteController(
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(
      prefilledAboutTheTradingHistoryPartOne
    )
  ) = new AdditionalActivitiesOnSiteController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    additionalActivitiesOnSiteView,
    preEnrichedActionRefiner(aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne),
    mockSessionRepo
  )

  "AdditionalActivitiesOnSiteController GET /" should {
    "return 200 and HTML when data present in session" in {
      val result = additionalActivitiesOnSiteController().show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when data is none in session" in {
      val controller = additionalActivitiesOnSiteController(aboutTheTradingHistoryPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = additionalActivitiesOnSiteController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = additionalActivitiesOnSiteController().show()(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController.show().url
      )
    }

  }

  "AdditionalActivitiesOnSiteController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = additionalActivitiesOnSiteController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = additionalActivitiesOnSiteController().submit()(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "additionalActivitiesOnSite" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "AdditionalActivitiesOnSiteController" should {
    "error if form answer is missing and display correct error message" in {
      val formData = baseFormData - additionalActivitiesOnSiteErrorKey
      val form     = additionalActivitiesOnSiteForm.bind(formData)

      mustContainError(additionalActivitiesOnSiteErrorKey, "error.additionalActivitiesOnSite.missing", form)
    }
  }

  object TestData {
    val additionalActivitiesOnSiteErrorKey: String = "additionalActivitiesOnSite"

    val baseFormData: Map[String, String] = Map("additionalActivitiesOnSite" -> "yes")
  }

}
