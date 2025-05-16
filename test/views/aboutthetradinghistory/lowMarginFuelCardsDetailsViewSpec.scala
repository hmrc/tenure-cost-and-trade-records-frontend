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

package views.aboutthetradinghistory

import form.aboutthetradinghistory.LowMarginFuelCardDetailsForm.lowMarginFuelCardDetailsForm
import models.pages.Summary
import models.submissions.aboutthetradinghistory.LowMarginFuelCardDetail
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class lowMarginFuelCardsDetailsViewSpec extends QuestionViewBehaviours[LowMarginFuelCardDetail] {

  override val form: Form[LowMarginFuelCardDetail] = lowMarginFuelCardDetailsForm

  val messageKeyPrefix = "lowMarginFuelCardDetails"

  val backLink = controllers.aboutthetradinghistory.routes.PercentageFromFuelCardsController.show().url

  def createView = () =>
    lowMarginFuelCardsDetailsView(
      form,
      None,
      controllers.aboutthetradinghistory.routes.BunkeredFuelQuestionController.show().url,
      Summary("99996010001")
    )(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[LowMarginFuelCardDetail]) =>
    lowMarginFuelCardsDetailsView(form, Some(0), backLink, Summary("99996010001"))(using fakeRequest, messages)

  "Catering bunker fuel cards details view" should {
    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the benefits given Page" in {

      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.BunkeredFuelQuestionController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }

}
