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
import form.aboutthetradinghistory.GrossReceiptsForBaseLoadForm
import models.submissions.aboutthetradinghistory.GrossReceiptsForBaseLoad
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class GrossReceiptsForBaseLoadViewSpec extends QuestionViewBehaviours[Seq[GrossReceiptsForBaseLoad]] {

  val messageKeyPrefix = "grossReceiptsForBaseLoadView"
  val sessionRequest   = SessionRequest(aboutYourTradingHistory6076YesSession, fakeRequest)

  val form: Form[Seq[GrossReceiptsForBaseLoad]] =
    GrossReceiptsForBaseLoadForm.grossReceiptsForBaseLoadForm(Seq("2026", "2025", "2024"))(using messages)

  def createView = () => grossReceiptsForBaseLoadView(form, "")(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[GrossReceiptsForBaseLoad]]) =>
    grossReceiptsForBaseLoadView(form, "")(using sessionRequest, messages)

  "GrossReceiptsForBaseLoad 6076 view" should {

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
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
