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
import play.api.http.Status.{BAD_REQUEST, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class CalculatingTheRentControllerSpec extends TestBaseSpec {

  val mockAboutFranchisesOrLettingsNavigator = mock[AboutFranchisesOrLettingsNavigator]

  val mockAudit: Audit = mock[Audit]
  def calculatingTheRentController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ) =
    new CalculatingTheRentForController(
      stubMessagesControllerComponents(),
      mockAudit,
      mockAboutFranchisesOrLettingsNavigator,
      calculatingTheRentView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = calculatingTheRentController().show(0)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = calculatingTheRentController().show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "redirect the user to the catering Op or Letting Accommodation details page" when {
      "given an index" which {
        "does not exist within the session" in {
          val result = calculatingTheRentController().show(2)(fakeRequest)
          status(result) shouldBe SEE_OTHER

          redirectLocation(result) shouldBe Some(
            aboutfranchisesorlettings.routes.CateringOperationDetailsController.show(None).url
          )
        }
      }
    }

    "display the page with the fields prefilled in" when {
      "given an index" which {
        "exists within the session" in {
          val result = calculatingTheRentController().show(0)(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))
          Option(html.getElementById("dateInput.month").`val`()).value shouldBe "6"
          Option(html.getElementById("dateInput.year").`val`()).value  shouldBe "2022"

        }
      }
    }

  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = calculatingTheRentController().submit(0)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
