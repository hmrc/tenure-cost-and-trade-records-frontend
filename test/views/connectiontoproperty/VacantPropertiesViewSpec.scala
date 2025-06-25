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

package views.connectiontoproperty

import form.connectiontoproperty.VacantPropertiesForm
import models.pages.Summary
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class VacantPropertiesViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "vacantProperties"

  override val form: Form[AnswersYesNo] = VacantPropertiesForm.theForm

  val backLink: String = controllers.connectiontoproperty.routes.EditAddressController.show().url

  def createView: () => Html = () =>
    vacantPropertiesView(form, backLink, Summary("99996010001"), false)(using fakeRequest, messages)

  def createViewUsingForm: Form[AnswersYesNo] => Html = (form: Form[AnswersYesNo]) =>
    vacantPropertiesView(form, backLink, Summary("99996010001"), false)(using fakeRequest, messages)

  "Vacant properties view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.connectiontoproperty.routes.EditAddressController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.connectionToTheProperty"))
    }

    "contain vacant properties paragraph one" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("vacantProperties.p1")))
    }

    "contain vacant properties paragraph two" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("vacantProperties.p2")))
    }

    "contain vacant properties paragraph three" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("vacantProperties.p3")))
    }

    "contains a list" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(doc, messages("vacantProperties.item1"))
      assertContainsText(doc, messages("vacantProperties.item2"))
      assertContainsText(doc, messages("vacantProperties.item3"))
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "vacantProperties",
        "vacantProperties",
        AnswerYes.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "vacantProperties-2",
        "vacantProperties",
        AnswerNo.toString,
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
