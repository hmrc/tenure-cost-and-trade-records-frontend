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

package views.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.RentIncreasedAnnuallyWithRPIForm
import models.pages.Summary
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class RentIncreaseAnnuallyWithRPIViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "rentIncreasedAnnuallyWithRPIs"

  override val form = RentIncreasedAnnuallyWithRPIForm.rentIncreasedAnnuallyWithRPIDetailsForm

  val backLink = controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show().url

  def createView = () =>
    rentIncreaseAnnuallyWithRPIView(form, backLink, Summary("99996010001"))(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    rentIncreaseAnnuallyWithRPIView(form, backLink, Summary("99996010001"))(using fakeRequest, messages)

  "Rent Increase annually with RPI view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for rent include trade services with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "rentIncreaseAnnuallyWithRPI",
        "rentIncreasedAnnuallyWithRPIs",
        AnswerYes.toString,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for rent include trade services with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "rentIncreaseAnnuallyWithRPI-2",
        "rentIncreasedAnnuallyWithRPIs",
        AnswerNo.toString,
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
