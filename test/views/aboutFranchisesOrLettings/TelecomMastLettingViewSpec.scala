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

package views.aboutFranchisesOrLettings

import form.aboutfranchisesorlettings.TelecomMastLettingForm
import models.pages.Summary
import models.submissions.aboutfranchisesorlettings.TelecomMastLetting
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class TelecomMastLettingViewSpec extends QuestionViewBehaviours[TelecomMastLetting] {

  def TelecomMastLettingView = inject[views.html.aboutfranchisesorlettings.telecomMastLetting]

  val messageKeyPrefix = "label.telecomMastLetting"
  val backLink         = controllers.routes.TaskListController.show().url

  override val form = TelecomMastLettingForm.telecomMastLettingForm

  def createView = () => TelecomMastLettingView(form, Some(0), backLink, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[TelecomMastLetting]) =>
    TelecomMastLettingView(form, Some(0), backLink, Summary("99996010001"))(fakeRequest, messages)

  "Telecom Mast letting view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "operatingCompanyName"
    )

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.routes.TaskListController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheLettings"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
