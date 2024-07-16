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

package views.aboutFranchisesOrLettings

import actions.SessionRequest
import form.aboutfranchisesorlettings.AddAnotherCateringOperationOrLettingAccommodationForm
import models.pages.Summary
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class AddAnotherCateringOperationsSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix     = "addAnotherSeparateBusinessOrFranchise"
  val messageKeyPrefix6015 = "addAnotherConcession"
  val messageKeyPrefix6030 = "addAnotherConcessionOrFranchise"

  override val form: Form[AnswersYesNo] =
    AddAnotherCateringOperationOrLettingAccommodationForm.addAnotherCateringOperationForm

  val sessionRequest = SessionRequest(baseFilled6010Session, fakeRequest)

  def createView: () => Html = () =>
    addAnotherOperationConcessionFranchise(
      form,
      0,
      messageKeyPrefix6030,
      messageKeyPrefix6015,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(0).url,
      Summary("99996010001")
    )(sessionRequest, messages)

  def createViewUsingForm: Form[AnswersYesNo] => Html = (form: Form[AnswersYesNo]) =>
    addAnotherOperationConcessionFranchise(
      form,
      0,
      messageKeyPrefix6030,
      messageKeyPrefix6015,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(0).url,
      Summary("99996010001")
    )(sessionRequest, messages)

  "Add another catering operation view" must {

    behave like normalPageWithZeroBusinessOrLettings(createView, messageKeyPrefix, "0")

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController
        .show(0)
        .url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseLettings"))
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        messageKeyPrefix,
        messageKeyPrefix,
        AnswerYes.name,
        isChecked = false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        s"$messageKeyPrefix-2",
        messageKeyPrefix,
        AnswerNo.name,
        isChecked = false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain continue button with the value Continue" in {
      val doc            = asDocument(createViewUsingForm(form))
      val continueButton = doc.getElementById("continue").text()
      assert(continueButton == messages("button.label.continue"))
    }

    "contain save as draft button with the value Save as draft" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("save").text()
      assert(loginButton == messages("button.label.save"))
    }
  }
}
