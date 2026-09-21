/*
 * Copyright 2026 HM Revenue & Customs
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

import form.aboutfranchisesorlettings.CalculatingTheRentForm
import models.pages.Summary
import models.submissions.aboutfranchisesorlettings.CalculatingTheRent
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CalculatingTheRentForViewSpec extends QuestionViewBehaviours[CalculatingTheRent]:

  override val form: Form[CalculatingTheRent] = CalculatingTheRentForm.calculatingTheRentForm(using messages)

  private def createView = () =>
    calculatingTheRentView(
      form,
      0,
      "separate business",
      Summary("99996010001")
    )(using getRequest, messages)

  private def createViewUsingForm = (form: Form[CalculatingTheRent]) =>
    calculatingTheRentView(
      form,
      0,
      "separate business",
      Summary("99996010001")
    )(using getRequest, messages)

  "Catering operation rent details view" should {

    behave like normalPageWithMessageExtra(createView, "calculating.the.rent.for", "separate business")

    behave like pageWithBackLink(createView, "Rent received", controllers.aboutfranchisesorlettings.routes.RentReceivedFromController.show(idx = 0).url)

    "Section heading is visible" in {
      val doc  = asDocument(createViewUsingForm(form))
      val html = doc.getElementsByClass("govuk-caption-m").html()

      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.aboutConcessionsOrLettings")}"""
    }

    "contain text box " in {
      val doc      = asDocument(createViewUsingForm(form))
      val forLabel = doc.getElementsByAttributeValue("for", "rentDetails").text()

      forLabel shouldBe messages("calculating.the.rent.for.explain")
    }

    "contain date legend for label.dateInput" in {
      val doc    = asDocument(createViewUsingForm(form))
      val legend = doc.getElementsByClass("govuk-fieldset__legend govuk-!-font-weight-bold").text()

      legend shouldBe messages("calculating.the.rent.for.label")
    }

    "contain date format hint for dateInput-hint" in {
      val doc             = asDocument(createViewUsingForm(form))
      val firstOccupyHint = doc.getElementById("dateInput-hint").text()

      firstOccupyHint shouldBe messages("hint.date.example")
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
      val continueButton = doc.getElementById("continue-button").text()

      continueButton shouldBe messages("button.continue.label")
    }

    "contain save as draft button with the value Save as draft" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("save-button").text()

      loginButton shouldBe messages("button.save.label")
    }
  }
