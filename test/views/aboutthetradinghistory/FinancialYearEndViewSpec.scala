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
import form.aboutthetradinghistory.AccountingInformationForm.accountingInformationForm
import models.submissions.Form6010.DayMonthsDuration
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class FinancialYearEndViewSpec extends QuestionViewBehaviours[(DayMonthsDuration, Boolean)] {

  val messageKeyPrefix = "financialYearEnd"

  val sessionRequest = SessionRequest(aboutYourTradingHistory6010YesSession, fakeRequest)

  override val form: Form[(DayMonthsDuration, Boolean)] = accountingInformationForm

  def createView: () => Html = () => financialYearEndView(form)(sessionRequest, messages)

  def createViewUsingForm: Form[(DayMonthsDuration, Boolean)] => Html =
    (form: Form[(DayMonthsDuration, Boolean)]) => financialYearEndView(form)(sessionRequest, messages)

  "financialYearEnd view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutthetradinghistory.routes.AboutYourTradingHistoryController.show().url
    }

    "contain date format hint for financialYear-hint" in {
      val doc               = asDocument(createViewUsingForm(form))
      val financialYearHint = doc.getElementById("financialYear-hint").text()
      assert(financialYearHint == messages("hint.day.month.example"))
    }

    "contain date field for the value financialYear.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "financialYear.month", "Month")
      assertContainsText(doc, "financialYear.month")
    }

    "contain date field for the value financialYear.day" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "financialYear.day", "Day")
      assertContainsText(doc, "financialYear.day")
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
