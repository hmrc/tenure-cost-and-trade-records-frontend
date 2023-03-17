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

import form.aboutthetradinghistory.AboutYourTradingHistoryForm
import models.submissions.aboutthetradinghistory.AboutYourTradingHistory
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class AboutYourTradingHistoryViewSpec extends QuestionViewBehaviours[AboutYourTradingHistory] {

  def aboutTheTradingHistoryView = app.injector.instanceOf[views.html.aboutthetradinghistory.aboutYourTradingHistory]

  val messageKeyPrefix = "aboutYourTradingHistory"

  override val form = AboutYourTradingHistoryForm.aboutYourTradingHistoryForm

  val backLink = controllers.abouttheproperty.routes.TiedForGoodsController.show().url

  def createView = () => aboutTheTradingHistoryView(form, backLink)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[AboutYourTradingHistory]) =>
    aboutTheTradingHistoryView(form, backLink)(fakeRequest, messages)

  "About the trading history view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.routes.TaskListController.show.url
    }

    "contain an subhead for page" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(doc, messages("aboutYourTradingHistory.subheader"))
    }

    "contain date format hint for firstOccupy-hint" in {
      val doc             = asDocument(createViewUsingForm(form))
      val firstOccupyHint = doc.getElementById("firstOccupy-hint").text()
      assert(firstOccupyHint == messages("help.date.month.year"))
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

    "contain date format hint for financialYear-hint" in {
      val doc               = asDocument(createViewUsingForm(form))
      val financialYearHint = doc.getElementById("financialYear-hint").text()
      assert(financialYearHint == messages("help.date.month.year"))
    }

    "contain date field for the value financialYear.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "financialYear.month", "Month")
      assertContainsText(doc, "financialYear.month")
    }

    "contain date field for the value financialYear.year" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "financialYear.year", "Year")
      assertContainsText(doc, "financialYear.year")
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
