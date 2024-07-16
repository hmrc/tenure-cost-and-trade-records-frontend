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
import form.aboutthetradinghistory.CostOfSales6076Form
import models.submissions.aboutthetradinghistory.CostOfSales6076Sum
import play.api.data.Form
import utils.TestBaseSpec
import views.behaviours.ViewBehaviours

class CostOfSales6076ViewSpec extends TestBaseSpec with ViewBehaviours {

  val messageKeyPrefix = "costOfSales6076"
  val sessionRequest   = SessionRequest(aboutYourTradingHistory6076YesSession, fakeRequest)

  val form: Form[(Seq[CostOfSales6076Sum], String)] =
    CostOfSales6076Form.costOfSales6076Form(Seq("2026", "2025", "2024"))(messages)

  def createView = () => costOfSales6076View(form, "")(sessionRequest, messages)

  def createViewUsingForm = (form: Form[(Seq[CostOfSales6076Sum], String)]) =>
    costOfSales6076View(form, "")(sessionRequest, messages)

  "Cost of sales 6076 view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText.contains(messages("label.section.aboutYourTradingHistory")))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }

}
