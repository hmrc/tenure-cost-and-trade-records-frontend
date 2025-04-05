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

import form.aboutfranchisesorlettings.FranchiseTypeDetailsForm
import models.ForType.*
import models.submissions.aboutfranchisesorlettings.BusinessDetails
import models.submissions.common.{AnswerNo, AnswerYes}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours
import models.pages.Summary
import play.twirl.api.Html

class ConcessionOrFranchiseViewSpec extends QuestionViewBehaviours[BusinessDetails] {

  val messageKeyPrefix = "concessionOrFranchise"

  override val form: Form[BusinessDetails] =
    FranchiseTypeDetailsForm.theForm

  def createView: () => Html = () =>
    concessionOrFranchiseView(
      form,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url,
      Summary("99996010001"),
      FOR6010
    )(fakeRequest, messages)

  def createViewUsingForm: Form[BusinessDetails] => Html = (form: Form[BusinessDetails]) =>
    concessionOrFranchiseView(
      form,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url,
      Summary("99996010001"),
      FOR6010
    )(fakeRequest, messages)

  "Concession or franchise view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController
        .show()
        .url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseLettings"))
    }

    "Hint text is visible" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(doc, messages("hint.concessionOrFranchise"))
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "concessionOrFranchise",
        "concessionOrFranchise",
        AnswerYes.name,
        isChecked = false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "concessionOrFranchise-2",
        "concessionOrFranchise",
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
