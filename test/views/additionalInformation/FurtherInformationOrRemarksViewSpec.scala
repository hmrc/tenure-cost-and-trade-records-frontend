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

package views.additionalInformation

import form.additionalinformation.FurtherInformationOrRemarksForm
import models.pages.Summary
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class FurtherInformationOrRemarksViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "furtherInformationOrRemarks"

  override val form: Form[String] =
    FurtherInformationOrRemarksForm.furtherInformationOrRemarksForm

  def createView: () => Html = () =>
    furtherInformationOrRemarksView(form, Summary("99996010001"))(using fakeRequest, messages)

  def createViewUsingForm: Form[String] => Html =
    (form: Form[String]) => furtherInformationOrRemarksView(form, Summary("99996010001"))(using fakeRequest, messages)

  "Further Information or Remarks view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.routes.TaskListController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.additionalInformation"))
    }

    "contain an input for furtherInformationOrRemarks" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "furtherInformationOrRemarks")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
