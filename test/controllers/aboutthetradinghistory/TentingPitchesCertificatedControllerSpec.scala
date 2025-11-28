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
import form.aboutthetradinghistory.TentingPitchesCertificatedForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class TentingPitchesCertificatedControllerSpec extends TestBaseSpec {

  import utils.FormBindingTestAssertions._

  val mockAudit: Audit = mock[Audit]
  def tentingPitchesCertificatedController(
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(
      prefilledAboutTheTradingHistoryPartOne
    )
  )                    = new TentingPitchesCertificatedController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourTradingHistoryNavigator,
    tentingPitchesCertificatedView,
    preEnrichedActionRefiner(aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne),
    mockSessionRepo
  )

  "TentingPitchesCertificatedController GET /" should {
    "return 200 and HTML when data present in session" in {
      val result = tentingPitchesCertificatedController().show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when data is none in session" in {
      val controller = tentingPitchesCertificatedController(aboutTheTradingHistoryPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = tentingPitchesCertificatedController().show()(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController.show().url
      )
    }

  }

  "TentingPitchesCertificatedController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = tentingPitchesCertificatedController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Form validation" should {

    "error if tentingPitchesTotal is missing" in {
      val formData = Map("tentingPitchesCertificated" -> "")
      val form     = TentingPitchesCertificatedForm.tentingPitchesCertificatedForm.bind(formData)
      mustContainError("tentingPitchesCertificated", "error.tentingPitchesCertificated.missing", form)
    }
  }

}
