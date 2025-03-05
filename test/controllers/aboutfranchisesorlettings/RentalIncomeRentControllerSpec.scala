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

package controllers.aboutfranchisesorlettings

import connectors.Audit
import controllers.aboutfranchisesorlettings
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class RentalIncomeRentControllerSpec extends TestBaseSpec {

  val mockAboutFranchisesOrLettingsNavigator = mock[AboutFranchisesOrLettingsNavigator]
  val mockAudit: Audit                       = mock[Audit]
  def controller(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings6045)
  ) =
    new RentalIncomeRentController(
      stubMessagesControllerComponents(),
      mockAudit,
      mockAboutFranchisesOrLettingsNavigator,
      rentalIncomeRentView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = controller().show(0)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller().show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
    "render a page with an empty form" when {
      "given an index" which {
        "doesn't already exist in the session" in {
          val result = controller().show(0)(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))

          Option(html.getElementById("annualRent").`val`()).value      shouldBe ""
          Option(html.getElementById("dateInput.day").`val`()).value   shouldBe ""
          Option(html.getElementById("dateInput.month").`val`()).value shouldBe ""
          Option(html.getElementById("dateInput.year").`val`()).value  shouldBe ""
        }
      }
    }

    "display the page with the fields prefilled in" when {
      "given an index" which {
        "exists within the session" in {
          val result = controller().show(1)(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))

          Option(html.getElementById("annualRent").`val`()).value      shouldBe "15000.0"
          Option(html.getElementById("dateInput.day").`val`()).value   shouldBe "1"
          Option(html.getElementById("dateInput.month").`val`()).value shouldBe "1"
          Option(html.getElementById("dateInput.year").`val`()).value  shouldBe "2021"

        }
      }
    }
    "render back link to CYA if come from CYA" in {

      val result  = controller().show(0)(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-franchise-or-lettings")
    }

    "render a correct back link to franchise type details if no query parameters in the url for 6010 " in {
      // franchise is on index 0
      val controller6010 = controller(Some(prefilledAboutFranchiseOrLettings6010and6016))
      val result         = controller6010.show(0)(fakeRequest)
      val content        = contentAsString(result)
      content should include("/franchise-type-details?idx=0")
    }
    "render a correct back link to letting type details if no query parameters in the url for 6010 " in {
      // letting on index 1
      val controller6010 = controller(Some(prefilledAboutFranchiseOrLettings6010and6016))
      val result         = controller6010.show(1)(fakeRequest)
      val content        = contentAsString(result)
      content should include("/letting-type-details?idx=1")
    }

    "render a correct back link to letting type details if no query parameters in the url for 6045 " in {
      // letting on index 1
      val result  = controller().show(1)(fakeRequest)
      val content = contentAsString(result)
      content should include("/letting-type-details?idx=1")
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

}
