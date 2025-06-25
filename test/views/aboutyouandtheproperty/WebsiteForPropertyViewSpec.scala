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

import form.aboutyouandtheproperty.WebsiteForPropertyForm
import models.pages.Summary
import models.submissions.aboutyouandtheproperty.WebsiteForPropertyDetails
import models.submissions.common.AnswersYesNo.*
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class WebsiteForPropertyViewSpec extends QuestionViewBehaviours[WebsiteForPropertyDetails] {

  val messageKeyPrefix = "buildingOperatingHaveAWebsite"

  val backLink = controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show().url

  override val form: Form[WebsiteForPropertyDetails] = WebsiteForPropertyForm.websiteForPropertyForm

  def createView: () => Html = () =>
    websiteForPropertyView(form, Summary("99996010001"), backLink)(using fakeRequest, messages)

  def createViewUsingForm: Form[WebsiteForPropertyDetails] => Html = (form: Form[WebsiteForPropertyDetails]) =>
    websiteForPropertyView(form, Summary("99996010001"), backLink)(using fakeRequest, messages)

  "Property website view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show().url
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
        "buildingOperatingHaveAWebsite",
        "buildingOperatingHaveAWebsite",
        AnswerYes.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "buildingOperatingHaveAWebsite-2",
        "buildingOperatingHaveAWebsite",
        AnswerNo.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain an input for websiteAddressForProperty" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "websiteAddressForProperty")
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
