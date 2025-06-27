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

package views.aboutFranchisesOrLettings

import actions.SessionRequest
import form.aboutfranchisesorlettings.RentReceivedFromForm
import models.ForType.FOR6010
import models.Session
import models.submissions.aboutfranchisesorlettings.RentReceivedFrom
import play.api.data.Form
import play.api.mvc.AnyContent
import views.behaviours.QuestionViewBehaviours

class rentReceivedFromViewSpec extends QuestionViewBehaviours[RentReceivedFrom] {

  override val form =
    RentReceivedFromForm.rentReceivedFromForm(using messages)

  val fakeSessionRequest = SessionRequest[AnyContent](
    sessionData = Session(
      referenceNumber,
      FOR6010,
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      isWelsh = false
    ),
    /*
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYes),
      removeConnectionDetails = Some(prefilledRemoveConnection),
      aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo)
     */
    request = fakeGetRequest
  )

  def createView = () =>
    rentReceivedFromView(
      form,
      0,
      "separate business",
      Some("/backLinkUrl")
    )(using fakeSessionRequest, messages)

  def createViewUsingForm = (form: Form[RentReceivedFrom]) =>
    rentReceivedFromView(
      form,
      0,
      "separate business",
      Some("/backLinkUrl")
    )(using fakeSessionRequest, messages)

  "Catering operation rent details view" should {

    behave like normalPageWithMessageExtra(createView, "rent.received.from", "separate business")

    behave like pageWithTextFields(createViewUsingForm, "annualRent")

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe "/backLinkUrl"
    }

    "Section caption is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutConcessionsOrLettings"))
    }

    "contain date label for label.annualRent" in {
      val doc      = asDocument(createViewUsingForm(form))
      val forLabel = doc.getElementsByAttributeValue("for", "annualRent").text()
      assert(forLabel == messages("label.annualRent"))
    }

    "contain checkbox" in {
      val doc    = asDocument(createViewUsingForm(form))
      val legend = doc.getElementsByClass("govuk-label govuk-checkboxes__label govuk-!-font-weight-bold").text()
      assert(legend == messages("rent.received.from.confirm"))
    }

    "contain continue button with the value Continue" in {
      val doc            = asDocument(createViewUsingForm(form))
      val continueButton = doc.getElementById("continue").text()
      assert(continueButton == messages("button.label.continue"))
    }

    "contain save as draft button with the value Save as draft" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("save").text()
      assert(loginButton == messages("button.label.save"))
    }
  }
}
