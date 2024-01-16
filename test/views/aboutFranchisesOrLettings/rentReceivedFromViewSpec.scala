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

package views.aboutFranchisesOrLettings

import form.aboutfranchisesorlettings.RentReceivedFromForm
import models.pages.Summary
import models.submissions.aboutfranchisesorlettings.RentReceivedFrom
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class rentReceivedFromViewSpec extends QuestionViewBehaviours[RentReceivedFrom] {

  override val form =
    RentReceivedFromForm.rentReceivedFromForm(messages)

  def createView = () =>
    rentReceivedFromView(
      form,
      0,
      "separate business",
      Summary("99996010001")
    )(fakeRequest, messages)

  def createViewUsingForm = (form: Form[RentReceivedFrom]) =>
    rentReceivedFromView(
      form,
      0,
      "separate business",
      Summary("99996010001")
    )(fakeRequest, messages)

  "Catering operation rent details view" should {

    behave like normalPageWithMessageExtra(createView, "rent.received.from", "separate business")

    behave like pageWithTextFields(createViewUsingForm, "annualRent")

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController
        .show(idx = Some(0))
        .url
    }

    "Section caption is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseConcessions"))
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

    "contain save and continue button with the value Save and Continue" in {
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
