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
import form.aboutthetradinghistory.TurnoverForm
import models.submissions.aboutthetradinghistory.TurnoverSection
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

import java.time.LocalDate

class TurnoverViewSpec extends QuestionViewBehaviours[Seq[TurnoverSection]] {

  val messageKeyPrefix = "turnover"

  override val form = TurnoverForm.turnoverForm(
    3,
    Seq(LocalDate.of(2021, 12, 31), LocalDate.of(2022, 12, 31), LocalDate.of(2023, 12, 31))
  )(using messages)

  val sessionRequest = SessionRequest(aboutYourTradingHistory6010YesSession, fakeRequest)

  val sessionRequest6015 = SessionRequest(aboutYourTradingHistory6015YesSession, fakeRequest)

  def createView = () => turnoverView(form)(using sessionRequest, messages)

  def createView6015 = () => turnoverView(form)(using sessionRequest6015, messages)

  def createViewUsingForm = (form: Form[Seq[TurnoverSection]]) => turnoverView(form)(using sessionRequest, messages)

  "Turnover view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show.url
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

    "contain get help section" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("turnover.details.p1")))
      assert(doc.toString.contains(messages("turnover.details.p2")))
    }

    "contain get help section for 6015" in {
      val doc = asDocument(createView6015())
      assert(doc.toString.contains(messages("turnover.details.6015.p1")))
      assert(doc.toString.contains(messages("turnover.details.6015.p2")))
      assert(doc.toString.contains(messages("turnover.details.6015.h3")))
      assert(doc.toString.contains(messages("turnover.details.6015.p4")))
      assert(doc.toString.contains(messages("turnover.details.6015.h4")))
      assert(doc.toString.contains(messages("turnover.details.6015.p5")))
      assert(doc.toString.contains(messages("turnover.details.6015.p6")))
    }

    s"contain an input for weeks" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "0.weeks")
      assertRenderedById(doc, "1.weeks")
      assertRenderedById(doc, "2.weeks")
    }

    s"contain an input for alcoholic-drinks" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "0.alcoholic-drinks")
      assertRenderedById(doc, "1.alcoholic-drinks")
      assertRenderedById(doc, "2.alcoholic-drinks")
    }

    s"contain an input for food" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "0.food")
      assertRenderedById(doc, "1.food")
      assertRenderedById(doc, "2.food")
    }

    s"contain an input for other-receipts" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "0.other-receipts")
      assertRenderedById(doc, "1.other-receipts")
      assertRenderedById(doc, "2.other-receipts")
    }

    s"contain an input for accommodation" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "0.accommodation")
      assertRenderedById(doc, "1.accommodation")
      assertRenderedById(doc, "2.accommodation")
    }

    s"contain an input for average-occupancy-rate" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "0.average-occupancy-rate")
      assertRenderedById(doc, "1.average-occupancy-rate")
      assertRenderedById(doc, "2.average-occupancy-rate")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
