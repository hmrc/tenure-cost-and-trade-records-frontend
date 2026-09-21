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

import connectors.Audit
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import test.ControllerSpec

class PitchesForGlampingControllerSpec extends ControllerSpec:

  val mockAudit: Audit = mock[Audit]

  def pitchesForGlampingController: PitchesForGlampingController =
    PitchesForGlampingController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourTradingHistoryNavigator,
      pitchesForGlampingView,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6045),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045)
      ),
      mockSessionRepository
    )

  "GET /" should {
    "return 200" in {
      val result = pitchesForGlampingController.show(getRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = pitchesForGlampingController.show(getRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render back link to CYA if come from CYA" in {
      val result  = pitchesForGlampingController.show(getRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-tenting-pitches")
    }
  }

  "SUBMIT /" should {
    "return 400 for form with errors" in {
      val res = pitchesForGlampingController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }
