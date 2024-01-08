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
import form.aboutthetradinghistory.TotalPayrollCostForm
import models.pages.Summary
import models.submissions.aboutthetradinghistory.TotalPayrollCost
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

import java.time.LocalDate

class TotalPayrollCostsViewSpec extends QuestionViewBehaviours[Seq[TotalPayrollCost]] {
  //NOTE: this is a holding view test until the total payroll costs page is implemented
  def totalPayrollCostsView = app.injector.instanceOf[views.html.aboutthetradinghistory.totalPayrollCosts]

  val sessionRequest   = SessionRequest(aboutYourTradingHistory6015YesSession, fakeRequest)

  val messageKeyPrefix = "totalPayrollCosts"

  override val form = TotalPayrollCostForm.totalPayrollCostForm(Seq(2025, 2024, 2023).map(_.toString))(messages)
  val fakeDates     = Seq(LocalDate.of(2021, 4, 1), LocalDate.of(2022, 4, 1), LocalDate.of(2023, 4, 1))

  def createView = () => totalPayrollCostsView(form, 3, fakeDates, Summary("99996010001"))(sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[TotalPayrollCost]]) =>
    totalPayrollCostsView(form, 3, fakeDates, Summary("99996010001"))(sessionRequest, messages)

  "Total Payroll Costs view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutthetradinghistory.routes.CostOfSalesController.show.url
    }

    "Page heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-heading-l").text()
      assert(sectionText == messages("totalPayrollCosts.heading"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
