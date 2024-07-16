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
import form.aboutthetradinghistory.TurnoverForm6030
import models.submissions.aboutthetradinghistory.TurnoverSection6030
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

import java.time.LocalDate

class Turnover6030ViewSpec extends QuestionViewBehaviours[Seq[TurnoverSection6030]] {

  val messageKeyPrefix = "turnover"

  override val form = TurnoverForm6030.turnoverForm6030(
    3,
    Seq(LocalDate.of(2021, 12, 31), LocalDate.of(2022, 12, 31), LocalDate.of(2023, 12, 31))
  )(messages)

  val sessionRequest = SessionRequest(aboutYourTradingHistory6030YesSession, fakeRequest)

  val sessionRequest6030 = SessionRequest(aboutYourTradingHistory6030YesSession, fakeRequest)

  def createView = () => turnover6030View(form)(sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[TurnoverSection6030]]) => turnover6030View(form)(sessionRequest, messages)

  "Turnover 6030 view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.FinancialYearEndController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").first().text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contains paragraph details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("turnover.p1")))
      // TODO - Reinstate paragraph when cut and paste functionality developed
//      assert(doc.toString.contains(messages("turnover.p2")))
    }

    s"contain an input for weeks" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "0.weeks")
      assertRenderedById(doc, "1.weeks")
      assertRenderedById(doc, "2.weeks")
    }

    s"contain an input for grossIncome" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "0.grossIncome")
      assertRenderedById(doc, "1.grossIncome")
      assertRenderedById(doc, "2.grossIncome")
    }

    s"contain an input for totalVisitorNumber" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "0.totalVisitorNumber")
      assertRenderedById(doc, "1.totalVisitorNumber")
      assertRenderedById(doc, "2.totalVisitorNumber")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
