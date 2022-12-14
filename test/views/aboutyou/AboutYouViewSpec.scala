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

package views.aboutyou

import form.aboutyou.AboutYouForm
import models.submissions.aboutyou.CustomerDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class AboutYouViewSpec extends QuestionViewBehaviours[CustomerDetails] {

  def aboutYouView = app.injector.instanceOf[views.html.aboutyou.aboutYou]

  val messageKeyPrefix = "aboutYou"

  override val form = AboutYouForm.aboutYouForm

  def createView = () => aboutYouView(form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[CustomerDetails]) => aboutYouView(form)(fakeRequest, messages)

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

    "contain aboutYou.subheading paragraph" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("aboutYou.subheading")))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain get help section" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("helpWithService.title")))
    }

    "contain get help section basic details heading" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithServiceHeader")))
    }

    "contain get help section basic details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithService")))
    }
  }
}
