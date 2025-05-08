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
import form.aboutthetradinghistory.TentingPitchesOnSiteForm
import models.submissions.common.AnswersYesNo
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class TentingPitchesOnSiteViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  private val sessionRequest = SessionRequest(baseFilled6045Session, fakeRequest)

  val messageKeyPrefix = "touringAndTentingPitches"

  override val form = TentingPitchesOnSiteForm.tentingPitchesOnSiteForm

  val backLink = controllers.routes.TaskListController.show().url

  def createView = () => tentingPitchesOnSiteView(form, backLink)(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    tentingPitchesOnSiteView(form, backLink)(using sessionRequest, messages)

  "Tenting Pitches On Site view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked as backLink leading to Task List" in {
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

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "tentingPitchesOnSite",
        "tentingPitchesOnSite",
        "yes",
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "tentingPitchesOnSite-2",
        "tentingPitchesOnSite",
        "no",
        false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
