/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class LettingOtherPartOfPropertyDetailsControllerSpec extends TestBaseSpec {

  val mockAboutFranchisesOrLettingsNavigator = mock[AboutFranchisesOrLettingsNavigator]

  def lettingOtherPartOfPropertyDetailsController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ) =
    new LettingOtherPartOfPropertyDetailsController(
      stubMessagesControllerComponents(),
      mockAboutFranchisesOrLettingsNavigator,
      lettingOtherPartOfPropertyDetailsView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = lettingOtherPartOfPropertyDetailsController().show(None)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = lettingOtherPartOfPropertyDetailsController().show(None)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render a page with an empty form" when {
      "not given an index" in {
        val result = lettingOtherPartOfPropertyDetailsController().show(None)(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))

        Option(html.getElementById("lettingOperatorName").`val`()).value               shouldBe ""
        Option(html.getElementById("lettingTypeOfBusiness").`val`()).value             shouldBe ""
        Option(html.getElementById("lettingAddress.buildingNameNumber").`val`()).value shouldBe ""
        Option(html.getElementById("lettingAddress.street1").`val`()).value            shouldBe ""
        Option(html.getElementById("lettingAddress.town").`val`()).value               shouldBe ""
        Option(html.getElementById("lettingAddress.county").`val`()).value             shouldBe ""
        Option(html.getElementById("lettingAddress.postcode").`val`()).value           shouldBe ""
      }
      "given an index" which {
        "doesn't already exist in the session" in {
          val result = lettingOtherPartOfPropertyDetailsController().show(Some(2))(fakeRequest)
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
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = lettingOtherPartOfPropertyDetailsController().submit(None)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
