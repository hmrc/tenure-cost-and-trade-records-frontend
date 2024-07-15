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

import form.aboutthetradinghistory.TentingPitchesAllYearForm
import models.pages.Summary
import models.submissions.aboutthetradinghistory.TentingPitchesAllYear
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class TentingPitchesAllYearViewSpec extends QuestionViewBehaviours[TentingPitchesAllYear] {

  val messageKeyPrefix = "areYourPitchesOpen"

  override val form = TentingPitchesAllYearForm.tentingPitchesAllYearForm

  val backLink = controllers.aboutthetradinghistory.routes.TentingPitchesOnSiteController.show().url

  def createView = () => tentingPitchesAllYearView(form, backLink, Summary("99996045001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[TentingPitchesAllYear]) =>
    tentingPitchesAllYearView(form, backLink, Summary("99996045001"))(fakeRequest, messages)

  "Tenting Pitches All Year view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked as backLink leading to Tenting pitches on site page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.TentingPitchesOnSiteController.show().url
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
        "tentingPitchesAllYear",
        "tentingPitchesAllYear",
        "yes",
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "tentingPitchesAllYear-2",
        "tentingPitchesAllYear",
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
