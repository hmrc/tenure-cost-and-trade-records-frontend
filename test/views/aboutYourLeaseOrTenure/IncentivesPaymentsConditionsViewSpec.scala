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

import form.aboutYourLeaseOrTenure.IncentivesPaymentsConditionsForm
import models.pages.Summary
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class IncentivesPaymentsConditionsViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "formerLeaseSurrendered"

  override val form = IncentivesPaymentsConditionsForm.incentivesPaymentsConditionsForm

  def createView = () => incentivesPaymentsConditionsView(form, Summary("99996010001"))(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    incentivesPaymentsConditionsView(form, Summary("99996010001"))(using fakeRequest, messages)

  "Incentives payment conditions view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to can rent be reduced Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for former lease surrendered with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "formerLeaseSurrendered",
        "formerLeaseSurrendered",
        AnswerYes.toString,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for former lease surrendered with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "formerLeaseSurrendered-2",
        "formerLeaseSurrendered",
        AnswerNo.toString,
        false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
