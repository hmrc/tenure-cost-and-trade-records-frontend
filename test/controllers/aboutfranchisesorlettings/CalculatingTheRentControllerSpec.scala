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

package controllers.aboutfranchisesorlettings

import connectors.Audit
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import org.jsoup.Jsoup
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import test.ControllerSpec

class CalculatingTheRentControllerSpec extends ControllerSpec:

  val mockAudit: Audit = mock[Audit]

  def controller(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings60156016)
  ): CalculatingTheRentForController =
    CalculatingTheRentForController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutFranchisesOrLettingsNavigator,
      calculatingTheRentView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepository
    )

  "GET /" should {
    "return 200" in {
      val result = controller().show(0)(getRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = controller().show(0)(getRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "display the page with the fields prefilled in" when {
      "given an index" which {
        "exists within the session" in {
          val result = controller().show(0)(getRequest)
          val html   = Jsoup.parse(contentAsString(result))
          Option(html.getElementById("dateInput.month").`val`()).get shouldBe "1"
          Option(html.getElementById("dateInput.year").`val`()).get  shouldBe "2021"
        }
      }
    }

  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = controller().submit(0)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
