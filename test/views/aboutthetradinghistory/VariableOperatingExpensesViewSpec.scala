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

package views.aboutthetradinghistory

import actions.SessionRequest
import form.aboutthetradinghistory.VariableOperatingExpensesForm
import models.submissions.aboutthetradinghistory.VariableOperatingExpenses
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

import java.time.LocalDate

class VariableOperatingExpensesViewSpec extends QuestionViewBehaviours[Seq[VariableOperatingExpenses]] {
  // NOTE: this is a holding view test until the variable operating expenses page is implemented
  def variableOperatingExpensesView =
    app.injector.instanceOf[views.html.aboutthetradinghistory.variableOperatingExpenses]

  val fakeDates = Seq(LocalDate.of(2021, 4, 1), LocalDate.of(2022, 4, 1), LocalDate.of(2023, 4, 1))

  val messageKeyPrefix = "variableOperatingExpenses"
  val sessionRequest   = SessionRequest(baseFilled6015Session, fakeRequest)

  override val form = VariableOperatingExpensesForm.variableOperatingExpensesForm(3)

  def createView = () => variableOperatingExpensesView(form)(sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[VariableOperatingExpenses]]) =>
    variableOperatingExpensesView(form)(sessionRequest, messages)

  "Variable Operating Expenses view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutthetradinghistory.routes.TotalPayrollCostsController.show.url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
