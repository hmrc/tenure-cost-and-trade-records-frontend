/*
 * Copyright 2026 HM Revenue & Customs
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

import actions.SessionRequest
import form.aboutthetradinghistory.StaffCostsForm
import models.submissions.aboutthetradinghistory.StaffCosts
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class StaffCostsViewSpec extends QuestionViewBehaviours[Seq[StaffCosts]]:

  private val sessionRequest = SessionRequest(aboutYourTradingHistory6076YesSession, getRequest)

  val form: Form[Seq[StaffCosts]] = StaffCostsForm.staffCostsForm(Seq("2026", "2025", "2024"))(using messages)

  private def createView = () => staffCostsView(form, "backLink")(using sessionRequest, messages)

  private def createViewUsingForm = (form: Form[Seq[StaffCosts]]) =>
    staffCostsView(form, "")(using sessionRequest, messages)

  "Cost of sales 6076 view" should {

    behave like pageWithBackLink(createView, "backLink", "backLink")

    "Section heading is visible" in {
      val doc  = asDocument(createViewUsingForm(form))
      val html = doc.getElementsByClass("govuk-caption-m").first.html()

      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.aboutYourTradingHistory")}"""
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()

      loginButton shouldBe messages("button.continue.label")
    }
  }
