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
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class ConcessionTypeDetailsControllerSpec extends TestBaseSpec {

  val mockAboutFranchisesOrLettingsNavigator = mock[AboutFranchisesOrLettingsNavigator]

  def controller(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings6045)
  ) =
    new ConcessionTypeDetailsController(
      stubMessagesControllerComponents(),
      mockAboutFranchisesOrLettingsNavigator,
      concessionTypeDetailsView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /"    should {
    "return 200" in {
      val result = controller().show(0)(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = controller().show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render a page with an empty form if index not present" in {
      val result = controller().show(1)(fakeRequest)
      val html   = Jsoup.parse(contentAsString(result))

      Option(html.getElementById("operatorName").`val`()).value   shouldBe ""
      Option(html.getElementById("typeOfBusiness").`val`()).value shouldBe ""
      Option(html.getElementById("howIsUsed").`val`()).value      shouldBe ""
    }
    "render a page with non empty form if data present" in {
      val result = controller().show(0)(fakeRequest)
      val html   = Jsoup.parse(contentAsString(result))

      Option(html.getElementById("operatorName").`val`()).value   shouldBe "Operator"
      Option(html.getElementById("typeOfBusiness").`val`()).value shouldBe "Bar"
      Option(html.getElementById("howIsUsed").`val`()).value      shouldBe "Leased"
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
