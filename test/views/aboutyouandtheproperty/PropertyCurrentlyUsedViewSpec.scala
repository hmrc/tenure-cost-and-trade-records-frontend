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

import form.aboutyouandtheproperty.PropertyCurrentlyUsedForm.propertyCurrentlyUsedForm
import models.pages.Summary
import models.submissions.aboutyouandtheproperty.PropertyCurrentlyUsed
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class PropertyCurrentlyUsedViewSpec extends QuestionViewBehaviours[PropertyCurrentlyUsed] {

  val messageKeyPrefix = "propertyCurrentlyUsed"

  override val form: Form[PropertyCurrentlyUsed] = propertyCurrentlyUsedForm

  def createView: () => Html = () =>
    propertyCurrentlyUsedView(form, Summary("99996045001"), "backLink")(fakeRequest, messages)

  def createViewUsingForm: Form[PropertyCurrentlyUsed] => Html = (form: Form[PropertyCurrentlyUsed]) =>
    propertyCurrentlyUsedView(form, Summary("99996045001"), "backLink")(fakeRequest, messages)

  "Property currently used view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe "backLink"
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain checkbox for the fleet caravan park" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "fleetCaravanPark", "propertyCurrentlyUsed[]", "fleetCaravanPark", isChecked = false)
      assertContainsText(doc, messages("label.propertyCurrentlyUsed.fleetCaravanPark"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }

}
