/*
 * Copyright 2024 HM Revenue & Customs
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

import form.aboutthetradinghistory.{IncomeExpenditureSummary6076Form, IncomeExpenditureSummaryForm}
import models.pages.{IncomeExpenditureEntry, Summary}
import models.submissions.aboutthetradinghistory.{IncomeExpenditure6076Entry, IncomeExpenditureSummary}
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class IncomeExpenditureSummary6076ViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "incomeExpenditureSummary6076"

  override val form = IncomeExpenditureSummary6076Form.incomeExpenditureSummary6076Form

  val entry: Seq[IncomeExpenditure6076Entry] = Seq(
    IncomeExpenditure6076Entry(
      "2023-03-01",
      1,
      "Url1",
      1,
      "Url2",
      1,
      "Url3",
      1,
      "Url4",
      1,
      "Url5",
      1,
      "Url6",
      1,
      "Url7",
      1,
      "Url8",
      8
    )
  )

  def createView = () => incomeExpenditureSummary6076View(form, Summary("99996010001"), entry)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) =>
    incomeExpenditureSummary6076View(form, Summary("99996010001"), entry)(fakeRequest, messages)

  "income and expenditure summary view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutthetradinghistory.routes.HeadOfficeExpensesController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "Page heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-heading-l").text()
      assert(sectionText == messages("incomeExpenditureSummary6076.heading"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
