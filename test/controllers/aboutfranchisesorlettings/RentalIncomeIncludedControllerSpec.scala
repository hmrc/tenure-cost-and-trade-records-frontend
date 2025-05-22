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
import navigation.AboutFranchisesOrLettingsNavigator
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class RentalIncomeIncludedControllerSpec extends TestBaseSpec {

  val mockAboutFranchisesOrLettingsNavigator             = mock[AboutFranchisesOrLettingsNavigator]
  val controllerComponents: MessagesControllerComponents = stubMessagesControllerComponents()
  override val messagesApi: MessagesApi                  = controllerComponents.messagesApi
  override val messages: Messages                        = messagesApi.preferred(fakeRequest)
  val mockAudit: Audit                                   = mock[Audit]
  def controller(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings6045)
  ) =
    new RentalIncomeIncludedController(
      controllerComponents,
      mockAudit,
      mockAboutFranchisesOrLettingsNavigator,
      rentalIncomeIncludedView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )(using executionContext)

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
      html.select(".govuk-checkboxes__divider").text shouldBe messages("checkbox.divider")
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

  "render a correct back link to franchise type details if no query parameters in the url for 6010 " in {
    // franchise is on index 0
    val controller6010 = controller(Some(prefilledAboutFranchiseOrLettings6010))
    val result         = controller6010.show(0)(fakeRequest)
    val content        = contentAsString(result)
    content should include("/rental-income-rent?idx=0")
  }
  "render a correct back link to letting type details if no query parameters in the url for 6010 " in {
    // letting is on index 1
    val controller6010 = controller(Some(prefilledAboutFranchiseOrLettings6010))
    val result         = controller6010.show(1)(fakeRequest)
    val content        = contentAsString(result)
    content should include("/rental-income-rent?idx=1")
  }

  "render a correct back link to letting type rent if no query parameters in the url for 6045 " in {
    val result  = controller().show(1)(fakeRequest)
    val content = contentAsString(result)
    content should include("/rental-income-rent?idx=1")
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
