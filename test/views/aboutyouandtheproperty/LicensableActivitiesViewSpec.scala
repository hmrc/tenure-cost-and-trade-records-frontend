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

import form.aboutyouandtheproperty.LicensableActivitiesForm
import models.pages.Summary
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class LicensableActivitiesViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "licensableActivities"

  override val form: Form[AnswersYesNo] = LicensableActivitiesForm.licensableActivitiesForm

  def createView: () => Html = () => licensableActivitiesView(form, Summary("99996010001"))(using fakeRequest, messages)

  def createViewUsingForm: Form[AnswersYesNo] => Html = (form: Form[AnswersYesNo]) =>
    licensableActivitiesView(form, Summary("99996010001"))(using fakeRequest, messages)

  "Property licence activities view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the website for property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show().url
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
        "licensableActivities",
        "licensableActivities",
        AnswerYes.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "licensableActivities-2",
        "licensableActivities",
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

    "contain get help section" in {
      val doc = asDocument(createView()).toString
      assert(doc.contains(messages("help.licensableActivities.title")))
      assert(doc.contains(messages("help.licensableActivities.p1")))
      assert(doc.contains(messages("help.licensableActivities.list1.item1")))
      assert(doc.contains(messages("help.licensableActivities.list1.item2")))
      assert(doc.contains(messages("help.licensableActivities.list1.item3")))
      assert(doc.contains(messages("help.licensableActivities.list1.item4")))
      assert(doc.contains(messages("help.licensableActivities.list1.item5")))
      assert(doc.contains(messages("help.licensableActivities.list1.item6")))
      assert(doc.contains(messages("help.licensableActivities.list1.item7")))
    }
  }

}
