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

package views.aboutyouandtheproperty

import form.aboutyouandtheproperty.LicensableActivitiesForm
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class LicensableActivitiesViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "licensableActivities"

  override val form: Form[AnswersYesNo] = LicensableActivitiesForm.licensableActivitiesForm

  def createView: () => Html = () => licensableActivitiesView(form)(fakeRequest, messages)

  def createViewUsingForm: Form[AnswersYesNo] => Html = (form: Form[AnswersYesNo]) =>
    licensableActivitiesView(form)(fakeRequest, messages)

  "Property licence activities view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the website for property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show().url
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
        AnswerYes.name,
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
        AnswerNo.name,
        isChecked = false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain save and continue button with the value Save and Continue" in {
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
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("help.licensableActivities.title")))
      assert(doc.toString.contains(messages("help.licensableActivities.heading")))
      assert(doc.toString.contains(messages("help.licensableActivities.p1")))
      assert(doc.toString.contains(messages("help.licensableActivities.list1.p1")))
      assert(doc.toString.contains(messages("help.licensableActivities.list1.p2")))
      assert(doc.toString.contains(messages("help.licensableActivities.list1.p3")))
      assert(doc.toString.contains(messages("help.licensableActivities.list1.p4")))
      assert(doc.toString.contains(messages("help.licensableActivities.p2")))
      assert(doc.toString.contains(messages("help.licensableActivities.p3")))
      assert(doc.toString.contains(messages("help.licensableActivities.p4")))
      assert(doc.toString.contains(messages("help.licensableActivities.list2.p1")))
      assert(doc.toString.contains(messages("help.licensableActivities.list2.p2")))
      assert(doc.toString.contains(messages("help.licensableActivities.list2.p3")))
      assert(doc.toString.contains(messages("help.licensableActivities.list2.p4")))
      assert(doc.toString.contains(messages("help.licensableActivities.list2.p5")))
      assert(doc.toString.contains(messages("help.licensableActivities.list2.p6")))
      assert(doc.toString.contains(messages("help.licensableActivities.list2.p7")))
    }
  }
}
