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

import form.aboutYourLeaseOrTenure.CurrentAnnualRentForm
import models.AnnualRent
import models.pages.Summary
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CurrentAnnualRentViewSpec extends QuestionViewBehaviours[AnnualRent] {

  val messageKeyPrefix = "currentAnnualRent"

  override val form = CurrentAnnualRentForm.currentAnnualRentForm()

  val backLink = controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show().url

  def createView = () => currentAnnualRentView(form, backLink, Summary("99996010001"))(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[AnnualRent]) =>
    currentAnnualRentView(form, backLink, Summary("99996010001"))(using fakeRequest, messages)
  "Current annual rent view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the about the landlord Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain currency format hint for currentAnnualRent-hint" in {
      val doc             = asDocument(createViewUsingForm(form))
      val firstOccupyHint = doc.getElementById("currentAnnualRent-hint").text()
      assert(firstOccupyHint == messages("hint.currentAnnualRent"))
    }

    "contain currency field for the value currentAnnualRent" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "currentAnnualRent")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
