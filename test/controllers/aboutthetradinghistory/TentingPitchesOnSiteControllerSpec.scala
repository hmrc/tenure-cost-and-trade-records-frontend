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

import form.aboutthetradinghistory.TentingPitchesOnSiteForm.tentingPitchesOnSiteForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class TentingPitchesOnSiteControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def tentingPitchesOnSiteController(
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(
      prefilledAboutTheTradingHistoryPartOne
    )
  ) = new TentingPitchesOnSiteController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    tentingPitchesOnSiteView,
    preEnrichedActionRefiner(aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne),
    mockSessionRepo
  )

  "TentingPitchesOnSiteController GET /" should {
    "return 200 and HTML when data present in session" in {
      val result = tentingPitchesOnSiteController().show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when data is none in session" in {
      val controller = tentingPitchesOnSiteController(aboutTheTradingHistoryPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = tentingPitchesOnSiteController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = tentingPitchesOnSiteController().show()(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController.show().url
      )
    }

  }

  "TentingPitchesOnSiteController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = tentingPitchesOnSiteController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "TentingPitchesOnSiteController" should {
    "error if TentingPitchesOnSiteController answer is missing" in {
      val formData = baseFormData - errorKey.tentingPitchesOnSite
      val form     = tentingPitchesOnSiteForm.bind(formData)

      mustContainError(errorKey.tentingPitchesOnSite, "error.touringAndTentingPitches.missing", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val tentingPitchesOnSite: String
    } = new {
      val tentingPitchesOnSite: String = "tentingPitchesOnSite"
    }

    val baseFormData: Map[String, String] = Map("tentingPitchesOnSite" -> "yes")
  }

}
