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
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.http.Status.*
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.JsoupHelpers.contentAsJsoup
import utils.TestBaseSpec

class FranchiseTypeDetailsControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]
  def controller(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings6010and6016)
  ) =
    new FranchiseTypeDetailsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutFranchisesOrLettingsNavigator,
      franchiseTypeDetailsView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {

    val result = controller().show(0)(fakeRequest)
    val html   = contentAsJsoup(result)

    "return 200" in {
      status(result) shouldBe OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render a page with an empty form if index not present" in {
      val result = controller().show(1)(fakeRequest)
      val html   = contentAsJsoup(result)

      Option(html.getElementById("operatorName").`val`()).value                       shouldBe ""
      Option(html.getElementById("typeOfBusiness").`val`()).value                     shouldBe ""
      Option(html.getElementById("cateringAddress.buildingNameNumber").`val`()).value shouldBe ""
      Option(html.getElementById("cateringAddress.street1").`val`()).value            shouldBe ""
      Option(html.getElementById("cateringAddress.town").`val`()).value               shouldBe ""
      Option(html.getElementById("cateringAddress.county").`val`()).value             shouldBe ""
      Option(html.getElementById("cateringAddress.postcode").`val`()).value           shouldBe ""
    }

    "render a page with non empty form if data present" in {

      Option(html.getElementById("operatorName").`val`()).value   shouldBe "Bob Green"
      Option(html.getElementById("typeOfBusiness").`val`()).value shouldBe "Bob's buisness"

      Option(html.getElementById("cateringAddress.buildingNameNumber").`val`()).value shouldBe "004"
      Option(html.getElementById("cateringAddress.street1").`val`()).value            shouldBe "GORING ROAD"
      Option(html.getElementById("cateringAddress.town").`val`()).value               shouldBe "GORING-BY-SEA, WORTHING"
      Option(html.getElementById("cateringAddress.county").`val`()).value             shouldBe "West sussex"
      Option(html.getElementById("cateringAddress.postcode").`val`()).value           shouldBe "BN12 4AX"
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

      val res = controller().submit(0)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
