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

import controllers.aboutfranchisesorlettings.LettingTypeIncludedController
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

import controllers.aboutfranchisesorlettings.LettingTypeIncludedController
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import org.jsoup.Jsoup
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class LettingTypeIncludedControllerSpec extends TestBaseSpec {

  val mockAboutFranchisesOrLettingsNavigator             = mock[AboutFranchisesOrLettingsNavigator]
  val controllerComponents: MessagesControllerComponents = stubMessagesControllerComponents()
  override val messagesApi: MessagesApi                  = controllerComponents.messagesApi
  override val messages: Messages                        = messagesApi.preferred(fakeRequest)

  def controller(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings6045)
  ) =
    new LettingTypeIncludedController(
      controllerComponents,
      mockAboutFranchisesOrLettingsNavigator,
      lettingTypeIncludedView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )(executionContext)

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

    "render a page with necessary content" in {

      val result = controller().show(0)(fakeRequest)

      val html = Jsoup.parse(contentAsString(result))

      val checkboxes = html.select(".govuk-checkboxes__item label")

      checkboxes.get(0).text                         shouldBe messages(
        "checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.rates"
      )
      checkboxes.get(1).text                         shouldBe messages(
        "checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.propertyInsurance"
      )
      checkboxes.get(2).text                         shouldBe messages(
        "checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.outsideRepairs"
      )
      checkboxes.get(3).text                         shouldBe messages(
        "checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.insideRepairs"
      )
      html.select(".govuk-checkboxes__divider").text shouldBe messages("label.or")
      checkboxes.get(4).text                         shouldBe messages(
        "checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.noneOfThese"
      )

    }
  }

  "render back link to CYA if come from CYA" in {

    val result  = controller().show(0)(fakeRequestFromCYA)
    val content = contentAsString(result)
    content should include("/check-your-answers-about-franchise-or-lettings")
  }

  "render a correct back link to letting type rent if no query parameters in the url " in {
    val result  = controller().show(0)(fakeRequest)
    val content = contentAsString(result)
    content should include("/letting-type-rent?idx=0")
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = controller().submit(0)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )

    }
  }
}