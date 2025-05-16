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

package views.connectiontoproperty

import actions.SessionRequest
import form.connectiontoproperty.TradingNameOperatingFromPropertyForm
import models.pages.Summary
import models.submissions.connectiontoproperty.TradingNameOperatingFromProperty
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class TradingNameOperatingFromPropertyViewSpec extends QuestionViewBehaviours[TradingNameOperatingFromProperty] {

  private val sessionRequest = SessionRequest(baseFilled6048Session, fakeRequest)

  val messageKeyPrefix = "tradingNameFromProperty"

  val backLink = controllers.connectiontoproperty.routes.VacantPropertiesController.show().url

  override val form: Form[TradingNameOperatingFromProperty] =
    TradingNameOperatingFromPropertyForm.tradingNameOperatingFromPropertyForm

  def createView: () => Html = () =>
    tradingNameOperatingFromProperty(form, backLink, Summary("99996010001"))(using sessionRequest, messages)

  def createViewUsingForm: Form[TradingNameOperatingFromProperty] => Html =
    (form: Form[TradingNameOperatingFromProperty]) =>
      tradingNameOperatingFromProperty(form, backLink, Summary("99996010001"))(using sessionRequest, messages)

  "Trading name operating from property view" must {

    "has a link marked with back.link.label leading to has enforcement action been taken Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.connectionToTheProperty"))
    }

    "contain an input for tradingNameFromProperty" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "tradingNameFromProperty")
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
  }
}
