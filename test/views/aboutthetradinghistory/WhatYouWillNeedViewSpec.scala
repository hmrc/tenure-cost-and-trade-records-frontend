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
import form.aboutthetradinghistory.WhatYouWillNeedForm
import models.pages.Summary
import org.scalatest.matchers.must.Matchers.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class WhatYouWillNeedViewSpec extends QuestionViewBehaviours[String] {

  private val sessionRequest = SessionRequest(baseFilled6076Session, fakeRequest)

  val messageKeyPrefix = "whatYouWillNeed"
  override val form    = WhatYouWillNeedForm.whatYouWillNeedForm

  def createView = () => whatYouWillNeedView(form, Summary("99996076001"))(sessionRequest, messages)

  def createViewUsingForm = (form: Form[String]) =>
    whatYouWillNeedView(form, Summary("99996076001"))(sessionRequest, messages)

  "What you will need view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.routes.TaskListController.show().url
    }

    "contain whatYouWillNeed.header1" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("whatYouWillNeed.header1")))
    }

    "contain list" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("whatYouWillNeed.l1")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p1")))
      assert(doc.toString.contains(messages("whatYouWillNeed.l2")))
      assert(doc.toString.contains(messages("whatYouWillNeed.l3")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p3")))
      assert(doc.toString.contains(messages("whatYouWillNeed.l4")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p4")))
      assert(doc.toString.contains(messages("whatYouWillNeed.l5")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p5")))
      assert(doc.toString.contains(messages("whatYouWillNeed.l6")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p6")))
      assert(doc.toString.contains(messages("whatYouWillNeed.l7")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p7")))
      assert(doc.toString.contains(messages("whatYouWillNeed.l8")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p8")))
      assert(doc.toString.contains(messages("whatYouWillNeed.l9")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p9")))
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

  }
}
