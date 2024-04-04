/*
 * Copyright 2024 HM Revenue & Customs
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

import form.aboutyouandtheproperty.ContactDetailsQuestionForm
import models.pages.Summary
import models.submissions.aboutyouandtheproperty.ContactDetailsQuestion
import models.submissions.common.{AnswerNo, AnswerYes}
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class ContactDetailsQuestionViewSpec extends QuestionViewBehaviours[ContactDetailsQuestion] {

  val messageKeyPrefix = "contactDetailsQuestion"

  override val form: Form[ContactDetailsQuestion] = ContactDetailsQuestionForm.contactDetailsQuestionForm

  val backLink: String = controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show().url

  def createView: () => Html = () => contactDetailsQuestionView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm: Form[ContactDetailsQuestion] => Html = (form: Form[ContactDetailsQuestion]) =>
    contactDetailsQuestionView(form, Summary("99996010001"))(fakeRequest, messages)

  "Contact details question view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to website Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutyouandtheproperty.routes.AboutYouController.show().url
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
        "contactDetailsQuestion",
        "contactDetailsQuestion",
        AnswerYes.name,
        isChecked = false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "contactDetailsQuestion-2",
        "contactDetailsQuestion",
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
