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

package views.aboutyouandtheproperty

import form.aboutyouandtheproperty.ThreeYearsConstructedForm
import models.pages.Summary
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class ThreeYearsConstructedViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "threeYearsConstructed"

  override val form: Form[AnswersYesNo] = ThreeYearsConstructedForm.threeYearsConstructedForm

  def createView: () => Html = () =>
    threeYearsConstructedView(form, "", Summary("99996010001"))(using fakeRequest, messages)

  def createViewFromTL: () => Html = () =>
    threeYearsConstructedView(form, "TL", Summary("99996010001"))(using fakeRequest, messages)

  def createViewFromCYA: () => Html = () =>
    threeYearsConstructedView(form, "CYA", Summary("99996010001"))(using fakeRequest, messages)

  def createViewUsingForm: Form[AnswersYesNo] => Html = (form: Form[AnswersYesNo]) =>
    threeYearsConstructedView(form, "", Summary("99996010001"))(using fakeRequest, messages)

  "Three years constructed view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to website Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutyouandtheproperty.routes.RenewablesPlantController.show().url
    }
    "has a link marked with back.link.label leading to Task List page if page assessed from Task List" in {
      val doc         = asDocument(createViewFromTL())
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.routes.TaskListController.show().url + "#site-construction-details"
    }
    "has a link marked with back.link.label leading to CYA page if page accessed from Check your answer" in {
      val doc         = asDocument(createViewFromCYA())
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController
        .show()
        .url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "threeYearsConstructed",
        "threeYearsConstructed",
        AnswerYes.name,
        isChecked = false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "threeYearsConstructed-2",
        "threeYearsConstructed",
        AnswerNo.name,
        isChecked = false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain save as draft button with the value Save as draft" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("save").text()
      assert(loginButton == messages("button.label.save"))
    }
  }
}
