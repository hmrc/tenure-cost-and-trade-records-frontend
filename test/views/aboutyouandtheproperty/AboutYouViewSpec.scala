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

import form.aboutyouandtheproperty.AboutYouForm
import models.submissions.aboutyouandtheproperty.CustomerDetails
import org.scalatest.matchers.must.Matchers._
import models.pages.Summary
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class AboutYouViewSpec extends QuestionViewBehaviours[CustomerDetails] {

  val messageKeyPrefix = "aboutYou"

  override val form = AboutYouForm.aboutYouForm

  def createView = () => aboutYouView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[CustomerDetails]) =>
    aboutYouView(form, Summary("99996010001"))(fakeRequest, messages)

  "About you view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, "fullName", "contactDetails.email", "contactDetails.phone")

    "has a link marked with back.link.label leading to the Are still connected Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.routes.TaskListController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain aboutYou.subheading paragraph" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("aboutYou.subheading")))
    }

    "contain an input for fullName" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "fullName")
    }

    "contain an input for contactDetails.phone" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "contactDetails.phone")
    }

    "contain an input for contactDetails.email" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "contactDetails.email")
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
