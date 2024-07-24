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

import actions.SessionRequest
import form.aboutthetradinghistory.OccupationalInformationForm
import models.submissions.Form6010.MonthsYearDuration
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class AboutYourTradingHistoryViewSpec extends QuestionViewBehaviours[MonthsYearDuration] {

  val messageKeyPrefix = "firstOccupy"

  val backLink = controllers.routes.TaskListController.show().url

  val sessionRequest = SessionRequest(aboutYourTradingHistory6010YesSession, fakeRequest)

  override val form: Form[MonthsYearDuration] =
    OccupationalInformationForm.occupationalInformationForm(messages)

  def createView: () => Html = () => aboutYourTradingHistoryView(form, backLink)(sessionRequest, messages)

  def createViewUsingForm: Form[MonthsYearDuration] => Html =
    (form: Form[MonthsYearDuration]) => aboutYourTradingHistoryView(form, backLink)(sessionRequest, messages)

  "About the trading history view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.routes.TaskListController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contain date format hint for firstOccupy-hint" in {
      val doc             = asDocument(createViewUsingForm(form))
      val firstOccupyHint = doc.getElementById("firstOccupy-hint").text()
      assert(firstOccupyHint == messages("hint.month.year.example"))
    }

    "contain date field for the value firstOccupy.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "firstOccupy.month", "Month")
      assertContainsText(doc, "firstOccupy.month")
    }

    "contain date field for the value firstOccupy.year" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "firstOccupy.year", "Year")
      assertContainsText(doc, "firstOccupy.year")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
