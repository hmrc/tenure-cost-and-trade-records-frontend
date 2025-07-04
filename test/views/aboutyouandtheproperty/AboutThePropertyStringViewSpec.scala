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

import form.aboutyouandtheproperty.AboutThePropertyStringForm
import models.ForType.*
import models.pages.Summary
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class AboutThePropertyStringViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "aboutProperty"

  val backLink = controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show().url

  override val form: Form[String] = AboutThePropertyStringForm.aboutThePropertyStringForm

  def createView: () => Html = () =>
    aboutThePropertyStringView(form, FOR6010, Summary("99996010001"), backLink)(using fakeRequest, messages)

  def createViewUsingForm: Form[String] => Html = (form: Form[String]) =>
    aboutThePropertyStringView(form, FOR6010, Summary("99996010001"), backLink)(using fakeRequest, messages)

  def createViewUsingForm6020: Form[String] => Html = (form: Form[String]) =>
    aboutThePropertyStringView(form, FOR6020, Summary("99996020001"), backLink)(using fakeRequest, messages)

  "About the property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain an input for how the property is used" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "propertyCurrentlyUsedString")
    }

    "contain a hint text for text box for 6020" in {
      val doc = asDocument(createViewUsingForm6020(form))
      assertRenderedById(doc, "propertyCurrentlyUsedString")
    }

    "contain save as draft button with the value Save as draft" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("save").text()
      assert(loginButton == messages("button.label.save"))
    }
  }
}
