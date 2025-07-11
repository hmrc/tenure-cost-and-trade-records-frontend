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

import actions.SessionRequest
import form.aboutthetradinghistory.TotalPayrollCostForm
import models.submissions.aboutthetradinghistory.TotalPayrollCost
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

import java.time.LocalDate

class TotalPayrollCostsViewSpec extends QuestionViewBehaviours[Seq[TotalPayrollCost]] {

  val sessionRequest = SessionRequest(aboutYourTradingHistory6015YesSession, fakeRequest)

  val messageKeyPrefix = "totalPayrollCosts"

  override val form = TotalPayrollCostForm.totalPayrollCostForm(Seq(2025, 2024, 2023).map(_.toString))(using messages)
  val fakeDates     = Seq(LocalDate.of(2021, 4, 1), LocalDate.of(2022, 4, 1), LocalDate.of(2023, 4, 1))

  def createView = () => totalPayrollCostsView(form)(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[TotalPayrollCost]]) =>
    totalPayrollCostsView(form)(using sessionRequest, messages)

  "Total Payroll Costs view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.CostOfSalesController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "Page heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-heading-l").text()
      assert(sectionText == messages("totalPayrollCosts.heading"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
