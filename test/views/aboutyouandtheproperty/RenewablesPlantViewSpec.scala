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

import form.aboutyouandtheproperty.RenewablesPlantForm
import models.pages.Summary
import models.submissions.aboutyouandtheproperty.RenewablesPlant
import models.submissions.aboutyouandtheproperty.RenewablesPlantDetails.*
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class RenewablesPlantViewSpec extends QuestionViewBehaviours[RenewablesPlant] {

  val messageKeyPrefix = "renewablesPlant"

  val backLink = controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url

  override val form: Form[RenewablesPlant] = RenewablesPlantForm.renewablesPlantForm

  def createView: () => Html = () =>
    renewablesPlantView(form, backLink, Summary("99996076001"))(using fakeRequest, messages)

  def createViewUsingForm: Form[RenewablesPlant] => Html =
    (form: Form[RenewablesPlant]) =>
      renewablesPlantView(form, backLink, Summary("99996076001"))(using fakeRequest, messages)

  "Renewables plant view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe backLink
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain radio buttons for the value intermitten" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "renewablesPlant",
        "renewablesPlant",
        Intermittent.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("renewablesPlant.intermittent.label"))
    }

    "contain radio buttons for the value base load" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "renewablesPlant-2",
        "renewablesPlant",
        Baseload.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("renewablesPlant.baseload.label"))
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
