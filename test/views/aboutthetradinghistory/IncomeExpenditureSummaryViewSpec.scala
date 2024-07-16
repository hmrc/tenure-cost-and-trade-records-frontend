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

import form.aboutthetradinghistory.IncomeExpenditureSummaryForm
import models.pages.{IncomeExpenditureEntry, Summary}
import models.submissions.aboutthetradinghistory.IncomeExpenditureSummary
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class IncomeExpenditureSummaryViewSpec extends QuestionViewBehaviours[IncomeExpenditureSummary] {
  // NOTE: this is a holding view test until page is implemented
  val messageKeyPrefix = "incomeExpenditureSummary"

  override val form = IncomeExpenditureSummaryForm.incomeExpenditureSummaryForm

  def createView = () =>
    incomeExpenditureSummaryView(form, Summary("99996010001"), createEnteries)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[IncomeExpenditureSummary]) =>
    incomeExpenditureSummaryView(form, Summary("99996010001"), createEnteries)(fakeRequest, messages)

  "income and expenditure summary view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.OtherCostsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "Page heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-heading-l").text()
      assert(sectionText == messages("incomeExpenditureSummary.heading"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }

  def createEnteries: Seq[IncomeExpenditureEntry] = Seq(
    IncomeExpenditureEntry(
      "2023-03-01",
      10,
      "turnoverUrl",
      2,
      "costOfSalesUrl",
      8,
      1,
      "totalPayrollURL",
      2,
      "variableExpensesUrl",
      1,
      "fixedExpensesUrl",
      1,
      "otherCostsUrl",
      3,
      30
    )
  )
}
