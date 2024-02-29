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

import form.aboutfranchisesorlettings.CateringOperationOrLettingAccommodationRentForm
import models.submissions.aboutfranchisesorlettings.CateringOperationRentDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours
import models.pages.Summary

class CateringOperationsRentDetailsViewSpec extends QuestionViewBehaviours[CateringOperationRentDetails] {

  val messageKeyPrefix = "cateringOperationOrLettingAccommodationRentDetails"

  override val form =
    CateringOperationOrLettingAccommodationRentForm.cateringOperationOrLettingAccommodationRentForm(messages)

  def createView = () =>
    cateringOperationRentDetailsView(
      form,
      0,
      messageKeyPrefix,
      "separate business",
      controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url,
      Summary("99996010001"),
      "FOR6010"
    )(fakeRequest, messages)

  def createViewUsingForm = (form: Form[CateringOperationRentDetails]) =>
    cateringOperationRentDetailsView(
      form,
      0,
      messageKeyPrefix,
      "separate business",
      controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url,
      Summary("99996010001"),
      "FOR6010"
    )(fakeRequest, messages)

  "Catering operation rent details view" must {

    behave like normalPageWithMessageExtra(createView, messageKeyPrefix, "separate business")

    behave like pageWithTextFields(createViewUsingForm, "annualRent")

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseLettings"))
    }

    "contain date label for label.annualRent" in {
      val doc      = asDocument(createViewUsingForm(form))
      val forLabel = doc.getElementsByAttributeValue("for", "annualRent").text()
      assert(forLabel == messages("label.annualRent"))
    }

    "contain date legend for label.dateInput" in {
      val doc    = asDocument(createViewUsingForm(form))
      val legend = doc.getElementsByClass("govuk-fieldset__legend govuk-!-font-weight-bold").text()
      assert(legend == messages("label.dateInput"))
    }

    "contain date format hint for dateInput-hint" in {
      val doc             = asDocument(createViewUsingForm(form))
      val firstOccupyHint = doc.getElementById("dateInput-hint").text()
      assert(firstOccupyHint == messages("hint.date.example"))
    }

    "contain date field for the value dateInput.day" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "dateInput.day", "Day")
      assertContainsText(doc, "dateInput.day")
    }

    "contain date field for the value dateInput.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "dateInput.month", "Month")
      assertContainsText(doc, "dateInput.month")
    }

    "contain date field for the value dateInput.year" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "dateInput.year", "Year")
      assertContainsText(doc, "dateInput.year")
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
