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

package views.connectiontoproperty

import form.connectiontoproperty.TradingNameOwnThePropertyForm
import models.pages.Summary
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class TradingNameOwnThePropertyViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "tradingNameOwnTheProperty"

  override val form: Form[AnswersYesNo] = TradingNameOwnThePropertyForm.tradingNameOwnThePropertyForm

  val backLink: String = controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show().url

  def createView: () => Html = () =>
    tradingNameOwnThePropertyView(form, backLink, "", Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm: Form[AnswersYesNo] => Html = (form: Form[AnswersYesNo]) =>
    tradingNameOwnThePropertyView(form, backLink, "", Summary("99996010001"))(fakeRequest, messages)

  "Trading name own the property view" must {

    "has a link marked with back.link.label leading to has enforcement action been taken Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.connectionToTheProperty"))
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "tradingNameOwnTheProperty",
        "tradingNameOwnTheProperty",
        AnswerYes.name,
        isChecked = false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "tradingNameOwnTheProperty-2",
        "tradingNameOwnTheProperty",
        AnswerNo.name,
        isChecked = false
      )
      assertContainsText(doc, messages("label.no"))
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
