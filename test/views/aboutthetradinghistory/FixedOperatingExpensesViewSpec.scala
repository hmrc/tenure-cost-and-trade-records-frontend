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

/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package views.aboutthetradinghistory

import actions.SessionRequest
import form.aboutthetradinghistory.FixedOperatingExpensesForm
import models.submissions.aboutthetradinghistory.FixedOperatingExpenses
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class FixedOperatingExpensesViewSpec extends QuestionViewBehaviours[Seq[FixedOperatingExpenses]]:

  private val messageKeyPrefix = "fixedOperatingExpenses"
  private val sessionRequest   = SessionRequest(baseFilled6015Session, fakeRequest)

  override val form: Form[Seq[FixedOperatingExpenses]] =
    FixedOperatingExpensesForm.fixedOperatingExpensesForm(Seq("2026", "2025", "2024"))(using messages)

  private def createView = () => fixedOperatingExpensesView(form)(using sessionRequest, messages)

  private def createViewUsingForm: Form[Seq[FixedOperatingExpenses]] => Html =
    form => fixedOperatingExpensesView(form)(using sessionRequest, messages)

  "Fixed Operating Expenses view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.VariableOperatingExpensesController.show().url
    }

    "Section heading is visible" in {
      val doc  = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val html = doc.getElementsByClass("govuk-caption-m").html()
      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.aboutYourTradingHistory")}"""
    }

    "Page heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-heading-l").text()
      assert(sectionText == messages("fixedOperatingExpenses.heading"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()
      assert(loginButton == messages("button.continue.label"))
    }
  }
