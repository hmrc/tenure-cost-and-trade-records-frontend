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

package views.aboutFranchisesOrLettings

import form.aboutfranchisesorlettings.FranchiseOrLettingsTiedToPropertyForm
import models.ForType.*
import models.pages.Summary
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class FranchiseOrLettingsTiedToPropertyView6015Spec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "franchiseLettingsIncome"

  override val form: Form[AnswersYesNo] = FranchiseOrLettingsTiedToPropertyForm.franchiseOrLettingsTiedToPropertyForm

  def createView: () => Html = () =>
    franchiseOrLettingsTiedToPropertyView(form, FOR6015, Summary("99996015001"))(fakeRequest, messages)

  def createViewUsingForm: Form[AnswersYesNo] => Html = (form: Form[AnswersYesNo]) =>
    franchiseOrLettingsTiedToPropertyView(form, FOR6015, Summary("99996015001"))(fakeRequest, messages)

  "Franchise or lettings tied to property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.routes.TaskListController.show().url + "#franchise-or-lettings-tied-to-property"
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseConcessions"))
    }

    "Hint text is visible" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(doc, messages("franchiseLettings.subheading"))
      assertContainsText(doc, messages("franchiseLettings.list1"))
      assertContainsText(doc, messages("franchiseLettings.list2"))
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "franchiseOrLettingsTiedToProperty",
        "franchiseOrLettingsTiedToProperty",
        AnswerYes.name,
        isChecked = false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "franchiseOrLettingsTiedToProperty-2",
        "franchiseOrLettingsTiedToProperty",
        AnswerNo.name,
        isChecked = false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain save as draft button with the value Save as draft" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("save").text()
      assert(loginButton == messages("button.label.save"))
    }
  }
}
