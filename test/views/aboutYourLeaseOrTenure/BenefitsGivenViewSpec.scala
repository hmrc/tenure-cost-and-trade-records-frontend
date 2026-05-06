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

package views.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.BenefitsGivenForm
import models.pages.Summary
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class BenefitsGivenViewSpec extends QuestionViewBehaviours[AnswersYesNo]:

  private val messageKeyPrefix = "benefitsGiven"

  override val form: Form[AnswersYesNo] = BenefitsGivenForm.benefitsGivenForm

  private def createView = () => benefitsGivenView(form, Summary("99996010001"))(using fakeRequest, messages)

  private def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    benefitsGivenView(form, Summary("99996010001"))(using fakeRequest, messages)

  "Benefits given view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to lease surrendered early page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.LeaseSurrenderedEarlyController.show().url
    }

    "Section heading is visible" in {
      val doc  = asDocument(createViewUsingForm(form))
      val html = doc.getElementsByClass("govuk-caption-m").html()
      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.aboutYourLeaseOrTenure")}"""
    }

    "contain radio buttons for benefits given with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "benefitsGiven",
        "benefitsGiven",
        AnswerYes.toString,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for benefits given with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "benefitsGiven-2",
        "benefitsGiven",
        AnswerNo.toString,
        false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()
      assert(loginButton == messages("button.continue.label"))
    }
  }
