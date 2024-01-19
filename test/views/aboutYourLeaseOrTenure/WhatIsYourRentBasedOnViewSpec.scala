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

package views.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.WhatIsYourCurrentRentBasedOnForm
import models.pages.Summary
import models.submissions.Form6010._
import models.submissions.aboutYourLeaseOrTenure.{CurrentRentBasedOnFixedAmount, CurrentRentBasedOnIndexedToRPI, CurrentRentBasedOnOther, CurrentRentBasedOnPercentageOpenMarket, CurrentRentBasedOnPercentageTurnover, CurrentRentBasedOnSteppedRent, WhatIsYourCurrentRentBasedOnDetails}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class WhatIsYourRentBasedOnViewSpec extends QuestionViewBehaviours[WhatIsYourCurrentRentBasedOnDetails] {

  val messageKeyPrefix = "currentRentBasedOn"

  override val form = WhatIsYourCurrentRentBasedOnForm.whatIsYourCurrentRentBasedOnForm

  def createView = () => whatIsYourRentBasedOnView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[WhatIsYourCurrentRentBasedOnDetails]) =>
    whatIsYourRentBasedOnView(form, Summary("99996010001"))(fakeRequest, messages)

  "Current rent based on details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, "whatIsYourRentBasedOn")

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show.url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for currentRentBasedOn with the value percentage" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "currentRentBasedOn",
        "currentRentBasedOn",
        CurrentRentBasedOnPercentageOpenMarket.name,
        false
      )
      assertContainsText(doc, messages("label.percentageOpenMarket"))
    }

    "contain radio buttons for currentRentBasedOn with the value fixed" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "currentRentBasedOn-2",
        "currentRentBasedOn",
        CurrentRentBasedOnFixedAmount.name,
        false
      )
      assertContainsText(doc, messages("label.fixed"))
    }

    "contain radio buttons for currentRentBasedOn with the value turnover" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "currentRentBasedOn-3",
        "currentRentBasedOn",
        CurrentRentBasedOnPercentageTurnover.name,
        false
      )
      assertContainsText(doc, messages("label.percentageTurnover"))
    }

    "contain radio buttons for currentRentBasedOn with the value RPI" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "currentRentBasedOn-4",
        "currentRentBasedOn",
        CurrentRentBasedOnIndexedToRPI.name,
        false
      )
      assertContainsText(doc, messages("label.indexed"))
    }

    "contain radio buttons for currentRentBasedOn with the value stepped rent" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "currentRentBasedOn-5",
        "currentRentBasedOn",
        CurrentRentBasedOnSteppedRent.name,
        false
      )
      assertContainsText(doc, messages("label.stepped"))
    }

    "contain radio buttons for currentRentBasedOn with the value other" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "currentRentBasedOn-6",
        "currentRentBasedOn",
        CurrentRentBasedOnOther.name,
        false
      )
      assertContainsText(doc, messages("label.other"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
