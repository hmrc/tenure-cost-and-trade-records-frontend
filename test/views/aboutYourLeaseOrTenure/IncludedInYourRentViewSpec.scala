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

import form.aboutYourLeaseOrTenure.IncludedInYourRentForm
import models.ForType.*
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class IncludedInYourRentViewSpec extends QuestionViewBehaviours[IncludedInYourRentDetails] {

  val messageKeyPrefix = "includedInYourRent"

  private val forType     = FOR6011
  private val forType6045 = FOR6045

  override val form: Form[IncludedInYourRentDetails] = IncludedInYourRentForm.includedInYourRentForm(forType)

  def createView = () => includedInYourRentView(form, Summary("99996010001"), forType)(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[IncludedInYourRentDetails]) =>
    includedInYourRentView(form, Summary("99996010001"), forType)(using fakeRequest, messages)

  def createViewUsingForm6045 = (form: Form[IncludedInYourRentDetails]) =>
    includedInYourRentView(form, Summary("99996045001"), forType6045)(using fakeRequest, messages)

  "Included in rent view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to current lease begin Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain checkbox for the vat" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "vat", "includedInYourRent[]", "vat", false)
      assertContainsText(doc, messages("label.includedInYourRent.vat"))
    }

    "contain checkbox for the nondomesticRates" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "nondomesticRates", "includedInYourRent[]", "nondomesticRates", false)
      assertContainsText(doc, messages("label.includedInYourRent.nondomesticRates"))
    }

    "contain checkbox for the waterCharges" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "waterCharges", "includedInYourRent[]", "waterCharges", false)
      assertContainsText(doc, messages("label.includedInYourRent.waterCharges"))
    }

    "contain checkbox for none" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "none", "includedInYourRent[]", "none", false)
      assertContainsText(doc, messages("label.includedInYourRent.noneOfThese"))
    }

    "contain an input for vatValue" in {
      val doc = asDocument(createViewUsingForm6045(form))
      assertRenderedById(doc, "vatValue")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

  }

}
