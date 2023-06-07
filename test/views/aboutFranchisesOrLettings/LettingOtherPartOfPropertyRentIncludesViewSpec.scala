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

import form.aboutfranchisesorlettings.LettingOtherPartOfPropertyRentIncludesForm.lettingOtherPartOfPropertyRentIncludesForm
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.api.i18n.Lang
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import models.pages.Summary

import java.util.Locale

class LettingOtherPartOfPropertyRentIncludesViewSpec extends QuestionViewBehaviours[List[String]] {

  val messageKeyPrefix = "lettingOtherPartOfPropertyCheckboxesDetails"

  val backLink      =
    controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsRentController.show(0).url
  // No form for checkboxes TODO Add form
  override val form = lettingOtherPartOfPropertyRentIncludesForm

  def createView = () =>
    cateringOperationRentIncludesView(form, 0, messageKeyPrefix, "Wombles Inc", backLink, Summary("99996010001"))(
      fakeRequest,
      messages
    )

  def createViewUsingForm = (form: Form[List[String]]) =>
    cateringOperationRentIncludesView(form, 0, messageKeyPrefix, "Wombles Inc", backLink, Summary("99996010001"))(
      fakeRequest,
      messages
    )

  "Catering operation rent includes view" must {

    behave like normalPageWithMessageExtra(
      createView,
      "cateringOperationOrLettingAccommodationCheckboxesDetails",
      "Wombles Inc"
    )

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe backLink
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseLettings"))
    }

// Tests commented as they have wrong asserts and elements with these ids doesn't exist

//    "contain checkbox for the value Rates" in {
//      val doc      = asDocument(createViewUsingForm(form))
//      val checkbox = doc.getElementById("rates")
//      assert(checkbox != null)
//      assert(checkbox.attr("name") == messageKeyPrefix)
//      assertContainsText(doc, messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.rates"))
//    }

//    "contain checkbox for the value Property insurance" in {
//      val doc      = asDocument(createViewUsingForm(form))
//      val checkbox = doc.getElementById("propertyInsurance")
//      assert(checkbox != null)
//      assert(checkbox.attr("name") == messageKeyPrefix)
//      assertContainsText(
//        doc,
//        messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.propertyInsurance")
//      )
//    }

//    "contain checkbox for the value Outside repairs" in {
//      val doc      = asDocument(createViewUsingForm(form))
//      val checkbox = doc.getElementById("outsideRepairs")
//      assert(checkbox != null)
//      assert(checkbox.attr("name") == messageKeyPrefix)
//      assertContainsText(
//        doc,
//        messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.outsideRepairs")
//      )
//    }

//    "contain checkbox for the value Inside repairs" in {
//      val doc      = asDocument(createViewUsingForm(form))
//      val checkbox = doc.getElementById("insideRepairs")
//      assert(checkbox != null)
//      assert(checkbox.attr("name") == messageKeyPrefix)
//      assertContainsText(
//        doc,
//        messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.insideRepairs")
//      )
//    }

    "contain save and continue button with the value Save and Continue" in {
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
