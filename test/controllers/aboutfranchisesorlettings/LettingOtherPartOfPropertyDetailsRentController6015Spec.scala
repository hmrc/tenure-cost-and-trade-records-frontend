/*
 * Copyright 2025 HM Revenue & Customs
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
import models.ForType.*
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class LettingOtherPartOfPropertyDetailsRentController6015Spec extends TestBaseSpec {

  val mockAboutFranchisesOrLettingsNavigator = mock[AboutFranchisesOrLettingsNavigator]
  val mockAudit: Audit                       = mock[Audit]
  def lettingOtherPartOfPropertyDetailsRentController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ) =
    new LettingOtherPartOfPropertyDetailsRentController(
      stubMessagesControllerComponents(),
      mockAudit,
      mockAboutFranchisesOrLettingsNavigator,
      lettingOtherPartOfPropertyRentDetailsView,
      preEnrichedActionRefiner(forType = FOR6015, aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = lettingOtherPartOfPropertyDetailsRentController().show(0)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = lettingOtherPartOfPropertyDetailsRentController().show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "redirect the user to the other Letting Accommodation details page" when {
      "given an index" which {
        "does not exist within the session" in {
          val result = lettingOtherPartOfPropertyDetailsRentController().show(2)(fakeRequest)
          status(result) shouldBe SEE_OTHER

          redirectLocation(result) shouldBe Some(
            aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show(None).url
          )
        }
      }
    }

    "display the page with the fields prefilled in" when {
      "given an index" which {
        "exists within the session" in {
          val result = lettingOtherPartOfPropertyDetailsRentController().show(0)(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))

          Option(html.getElementById("annualRent").`val`()).value      shouldBe "1500"
          Option(html.getElementById("dateInput.month").`val`()).value shouldBe "6"
          Option(html.getElementById("dateInput.year").`val`()).value  shouldBe "2022"

        }
      }
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = lettingOtherPartOfPropertyDetailsRentController().submit(0)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
