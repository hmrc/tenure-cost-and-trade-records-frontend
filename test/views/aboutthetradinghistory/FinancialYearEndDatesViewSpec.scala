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
import form.aboutthetradinghistory.FinancialYearEndDatesForm.financialYearEndDatesForm
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

import java.time.LocalDate

class FinancialYearEndDatesViewSpec extends QuestionViewBehaviours[Seq[LocalDate]] {

  val messageKeyPrefix = "financialYearEndDates"

  val sessionRequest = SessionRequest(aboutYourTradingHistory6010YesSession, fakeRequest)

  private val today    = LocalDate.now
  private val finYears = Seq(today, today.minusYears(1), today.minusYears(2)).map(_.getYear)

  override val form: Form[Seq[LocalDate]] = financialYearEndDatesForm()(messages)

  def createView: () => Html = () => financialYearEndDatesView(form, finYears)(sessionRequest, messages)

  def createViewUsingForm: Form[Seq[LocalDate]] => Html =
    financialYearEndDatesView(_, finYears)(sessionRequest, messages)

  "financialYearEndDates view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutthetradinghistory.routes.FinancialYearEndController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contain date field for the value financial-year-end[0].date.day" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "financial-year-end[0].date.day", "Day")
      assertContainsText(doc, "financial-year-end[0].date.day")
    }
    "contain date field for the value financial-year-end[0].date.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "financial-year-end[0].date.month", "Month")
      assertContainsText(doc, "financial-year-end[0].date.month")
    }

    "contain date field for the value financial-year-end[0].date.year" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "financial-year-end[0].date.year", "Year")
      assertContainsText(doc, "financial-year-end[0].date.year")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
