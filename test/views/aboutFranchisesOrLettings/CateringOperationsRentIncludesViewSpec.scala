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

import form.aboutfranchisesorlettings.IncomeRecordIncludedForm.incomeRecordIncludedForm
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours
import models.ForType.*
import models.pages.Summary

class CateringOperationsRentIncludesViewSpec extends QuestionViewBehaviours[List[String]] {

  val messageKeyPrefix = "cateringOperationOrLettingAccommodationCheckboxesDetails"

  val backLink      = controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsRentController.show(0).url
  // No form for checkboxes TODO Add form
  override val form = incomeRecordIncludedForm

  def createView = () =>
    cateringOperationRentIncludesView(form, 0, messageKeyPrefix, "{0}", backLink, Summary("99996010001"), FOR6010)(using
      fakeRequest,
      messages
    )

  def createViewUsingForm = (form: Form[List[String]]) =>
    cateringOperationRentIncludesView(form, 0, messageKeyPrefix, "{0}", backLink, Summary("99996010001"), FOR6010)(using
      fakeRequest,
      messages
    )

  "Catering operation rent includes view" must {

    behave like normalPageWithDifferentHeadingAndTitle(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe backLink
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseLettings"))
    }

    "contain checkbox for the rates" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "rates", "itemsInRent[]", "rates", isChecked = false)
      assertContainsText(doc, messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.rates"))
    }

    "contain checkbox for the propertyInsurance" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "propertyInsurance", "itemsInRent[]", "propertyInsurance", isChecked = false)
      assertContainsText(
        doc,
        messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.propertyInsurance")
      )
    }

    "contain checkbox for the outsideRepairs" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "outsideRepairs", "itemsInRent[]", "outsideRepairs", isChecked = false)
      assertContainsText(
        doc,
        messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.outsideRepairs")
      )
    }

    "contain checkbox for the insideRepairs" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "insideRepairs", "itemsInRent[]", "insideRepairs", isChecked = false)
      assertContainsText(
        doc,
        messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.insideRepairs")
      )
    }

    "contain checkbox for the noneOfThese" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "noneOfThese", "itemsInRent[]", "noneOfThese", isChecked = false)
      assertContainsText(doc, messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.noneOfThese"))
    }

// Tests excluded as they created for wrong checkboxes implementation

//    "contain date format hint for cateringOperationOrLettingAccommodationCheckboxesDetails-hint" in {
//      val doc             = asDocument(createViewUsingForm(form))
//      val firstOccupyHint = doc.getElementById(s"$messageKeyPrefix-hint").text()
//      assert(firstOccupyHint == messages(s"hint.$messageKeyPrefix"))
//    }

//    "contain checkbox for the value Rates" in {
//      val doc      = asDocument(createViewUsingForm(form))
//      val checkbox = doc.getElementById("rates")
//      assert(checkbox != null)
//      assert(checkbox.attr("name") == messageKeyPrefix)
////      assert(checkbox.attr("value") == value)
//      assertContainsText(doc, messages(s"checkbox.$messageKeyPrefix.rates"))
//    }

//    "contain checkbox for the value Property insurance" in {
//      val doc      = asDocument(createViewUsingForm(form))
//      val checkbox = doc.getElementById("propertyInsurance")
//      assert(checkbox != null)
//      assert(checkbox.attr("name") == messageKeyPrefix)
//      assertContainsText(doc, messages(s"checkbox.$messageKeyPrefix.propertyInsurance"))
//    }

//    "contain checkbox for the value Outside repairs" in {
//      val doc      = asDocument(createViewUsingForm(form))
//      val checkbox = doc.getElementById("outsideRepairs")
//      assert(checkbox != null)
//      assert(checkbox.attr("name") == messageKeyPrefix)
//      assertContainsText(doc, messages(s"checkbox.$messageKeyPrefix.outsideRepairs"))
//    }

//    "contain checkbox for the value Inside repairs" in {
//      val doc      = asDocument(createViewUsingForm(form))
//      val checkbox = doc.getElementById("insideRepairs")
//      assert(checkbox != null)
//      assert(checkbox.attr("name") == messageKeyPrefix)
//      assertContainsText(doc, messages(s"checkbox.$messageKeyPrefix.insideRepairs"))
//    }

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
