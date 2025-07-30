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

import form.aboutyouandtheproperty.PlantAndTechnologyForm
import models.pages.Summary
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class PlantAndTechnologyViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "plantAndTechnology"

  override val form: Form[String] = PlantAndTechnologyForm.plantAndTechnologyForm

  val backLink: String = controllers.aboutyouandtheproperty.routes.ThreeYearsConstructedController.show().url

  def createView: () => Html = () =>
    plantAndTechnologyView(form, backLink, Summary("99996076001"), false)(using fakeRequest, messages)

  def createViewUsingForm: Form[String] => Html = (form: Form[String]) =>
    plantAndTechnologyView(form, backLink, Summary("99996076001"), false)(using fakeRequest, messages)

  "Plant and technology view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }
    "contain an input for plant and technology " in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "plantAndTechnology")
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
