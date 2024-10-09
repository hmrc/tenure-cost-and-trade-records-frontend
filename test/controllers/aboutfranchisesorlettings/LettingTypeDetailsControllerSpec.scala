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

import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class LettingTypeDetailsControllerSpec extends TestBaseSpec {

  val mockAboutFranchisesOrLettingsNavigator = mock[AboutFranchisesOrLettingsNavigator]

  def controller(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings6045)
  ) =
    new LettingTypeDetailsController(
      stubMessagesControllerComponents(),
      mockAboutFranchisesOrLettingsNavigator,
      lettingTypeDetailsView,
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

          Option(html.getElementById("lettingOperatorName").`val`()).value               shouldBe ""
          Option(html.getElementById("lettingTypeOfBusiness").`val`()).value             shouldBe ""
          Option(html.getElementById("lettingAddress.buildingNameNumber").`val`()).value shouldBe ""
          Option(html.getElementById("lettingAddress.street1").`val`()).value            shouldBe ""
          Option(html.getElementById("lettingAddress.town").`val`()).value               shouldBe ""
          Option(html.getElementById("lettingAddress.county").`val`()).value             shouldBe ""
          Option(html.getElementById("lettingAddress.postcode").`val`()).value           shouldBe ""
        }
      }
    }
    "render a page with a pre-filled form" when {
      "given an index" which {
        "already exists in the session" in {
          val result = controller().show(1)(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))

          Option(html.getElementById("lettingOperatorName").`val`()).value               shouldBe "Letting Operator"
          Option(html.getElementById("lettingTypeOfBusiness").`val`()).value             shouldBe "Property Letting"
          Option(html.getElementById("lettingAddress.buildingNameNumber").`val`()).value shouldBe "123"
          Option(html.getElementById("lettingAddress.street1").`val`()).value            shouldBe "Main Street"
          Option(html.getElementById("lettingAddress.town").`val`()).value               shouldBe "Bristol"
          Option(html.getElementById("lettingAddress.county").`val`()).value             shouldBe "Bristol"
          Option(html.getElementById("lettingAddress.postcode").`val`()).value           shouldBe "AN12 3YZ"
        }
      }
    }

    "render back link to CYA if come from CYA" in {

      val result  = controller().show(0)(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-franchise-or-lettings")
    }

    "render a correct back link to type of income page if no query parameters in the url " in {
      val result  = controller().show(0)(fakeRequest)
      val content = contentAsString(result)
      content should include("/type-of-income")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = controller().submit(0)(fakeRequest.withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }
}
