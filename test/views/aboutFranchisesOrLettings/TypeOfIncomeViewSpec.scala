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

package views.aboutFranchisesOrLettings

import form.aboutfranchisesorlettings.TypeOfIncomeForm
import models.ForType.FOR6045
import models.pages.Summary
import models.submissions.aboutfranchisesorlettings.TypeOfIncome
import models.submissions.aboutfranchisesorlettings.TypeOfIncome.*
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class TypeOfIncomeViewSpec extends QuestionViewBehaviours[TypeOfIncome] {

  val messageKeyPrefix = "typeOfIncome"

  val backLink = controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url

  override val form: Form[TypeOfIncome] = TypeOfIncomeForm.typeOfIncomeForm

  def createView: () => Html = () =>
    typeOfIncomeView(form, None, Summary("99996045001"), backLink, FOR6045)(using fakeRequest, messages)

  def createViewUsingForm: Form[TypeOfIncome] => Html =
    (form: Form[TypeOfIncome]) =>
      typeOfIncomeView(form, None, Summary("99996045001"), backLink, FOR6045)(using fakeRequest, messages)

  "Type of income view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to franchise/letting question Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe backLink
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheConcessionsFranchisesLettings"))
    }

    "contain radio buttons for the value typeConcession" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "typeOfIncome",
        "typeOfIncome",
        TypeConcession.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("typeOfIncome.concession.label"))
    }

    "contain radio buttons for the value typeLetting" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "typeOfIncome-2",
        "typeOfIncome",
        TypeLetting.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("typeOfIncome.letting.label"))
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
