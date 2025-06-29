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

package views.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.CurrentRentPayableWithin12MonthsForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.CurrentRentPayableWithin12Months
import models.submissions.common.AnswersYesNo.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CurrentRentPayableWithin12MonthsViewSpec extends QuestionViewBehaviours[CurrentRentPayableWithin12Months] {

  val messageKeyPrefix = "currentRentPayableWithin12Months"

  override val form = CurrentRentPayableWithin12MonthsForm.currentRentPayableWithin12MonthsForm(using messages)

  def createView = () => currentRentPayableWithin12MonthsView(form, Summary("99996010001"))(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[CurrentRentPayableWithin12Months]) =>
    currentRentPayableWithin12MonthsView(form, Summary("99996010001"))(using fakeRequest, messages)

  "Lease or agreement years view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "currentRentPayableWithin12Months",
        "rentPayable",
        AnswerYes.toString,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "currentRentPayableWithin12Months-2",
        "rentPayable",
        AnswerNo.toString,
        false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "display the correct date label" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(doc, messages("label.dateReview"))
    }

    "contain date field for the value dateReview.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "dateReview.month", "Month")
      assertContainsText(doc, "dateReview.month")
    }

    "contain date field for the value dateReview.year" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "dateReview.year", "Year")
      assertContainsText(doc, "dateReview.year")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
