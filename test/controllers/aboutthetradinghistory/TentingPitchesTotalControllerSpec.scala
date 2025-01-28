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

import connectors.Audit
import form.aboutthetradinghistory.TentingPitchesTotalForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class TentingPitchesTotalControllerSpec extends TestBaseSpec {

  import utils.FormBindingTestAssertions._

  val mockAudit: Audit = mock[Audit]
  def tentingPitchesTotalController(
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(prefilledAboutTheTradingHistoryPartOne)
  ) = new TentingPitchesTotalController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourTradingHistoryNavigator,
    tentingPitchesTotalView,
    preEnrichedActionRefiner(aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne),
    mockSessionRepo
  )

  "TentingPitchesTotalController GET /" should {
    "return 200 and HTML when data present in session" in {
      val result = tentingPitchesTotalController().show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when data is none in session" in {
      val controller = tentingPitchesTotalController(aboutTheTradingHistoryPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = tentingPitchesTotalController().show()(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController.show().url
      )
    }

  }

  "TentingPitchesTotalController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = tentingPitchesTotalController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Form validation" should {
    "error if tentingPitchesTotal is invalid" in {
      val formData = Map("tentingPitchesTotal" -> "xxx")
      val form     = TentingPitchesTotalForm.tentingPitchesTotalForm.bind(formData)
      mustContainError("tentingPitchesTotal", "error.tentingPitchesTotal.range", form)
    }

    "error if tentingPitchesTotal is missing" in {
      val formData = Map("tentingPitchesTotal" -> "")
      val form     = TentingPitchesTotalForm.tentingPitchesTotalForm.bind(formData)
      mustContainError("tentingPitchesTotal", "error.tentingPitchesTotal.missing", form)
    }
  }

}
