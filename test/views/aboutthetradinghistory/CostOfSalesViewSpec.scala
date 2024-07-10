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
import form.aboutthetradinghistory.CostOfSalesForm
import models.submissions.aboutthetradinghistory.CostOfSales
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CostOfSalesViewSpec extends QuestionViewBehaviours[Seq[CostOfSales]] {
  // NOTE: this is a holding view test until the cost of sales page is implemented
  def costOfSalesView = inject[views.html.aboutthetradinghistory.costOfSales]

  val messageKeyPrefix = "costOfSales"
  val sessionRequest   = SessionRequest(aboutYourTradingHistory6015YesSession, fakeRequest)

  override val form = CostOfSalesForm.costOfSalesForm(Seq(2025, 2024, 2023).map(_.toString))(messages)

  def createView = () => costOfSalesView(form)(sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[CostOfSales]]) => costOfSalesView(form)(sessionRequest, messages)

  "costOfSales view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutthetradinghistory.routes.TurnoverController.show().url
    }

    "Section heading is visible" in {
      val form1    = CostOfSalesForm.costOfSalesForm(Seq("2025"))(messages)
      val doc      = asDocument(createViewUsingForm(form1))
      val captions = doc.getElementsByClass("govuk-caption-m").eachText()
      assert(captions.contains(messages("label.section.aboutYourTradingHistory")))
    }

    "Page heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-heading-l").text()
      assert(sectionText == messages("costOfSales.heading"))
    }

    "contain get help section" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("costOfSales.details.p1")))
      assert(doc.toString.contains(messages("costOfSales.details.p2")))
    }

    "contain continue button with the value Continue" in {
      val form2       = CostOfSalesForm.costOfSalesForm(Seq("2025", "2024"))(messages)
      val doc         = asDocument(createViewUsingForm(form2))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }

}
