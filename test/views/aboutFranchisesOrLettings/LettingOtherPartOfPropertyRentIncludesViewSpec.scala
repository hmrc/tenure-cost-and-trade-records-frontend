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

import form.aboutfranchisesorlettings.CateringOperationOrLettingAccommodationForm
import models.submissions.aboutfranchisesorlettings.CateringOperationDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.api.i18n.Lang
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours

import java.util.Locale

class LettingOtherPartOfPropertyRentIncludesViewSpec extends QuestionViewBehaviours[CateringOperationDetails] {

  val messageKeyPrefix = "lettingOtherPartOfPropertyCheckboxesDetails"

  val backLink      =
    controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsRentController.show(0).url
  // No form for checkboxes TODO Add form
  override val form = CateringOperationOrLettingAccommodationForm.cateringOperationOrLettingAccommodationForm

  def createView = () =>
    cateringOperationRentIncludesView(0, messageKeyPrefix, "Wombles Inc", backLink)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[CateringOperationDetails]) =>
    cateringOperationRentIncludesView(0, messageKeyPrefix, "Wombles Inc", backLink)(fakeRequest, messages)

  "Catering operation rent includes view" must {

    behave like normalPageLocal(createView, "cateringOperationOrLettingAccommodationCheckboxesDetails")

    def normalPageLocal(view: () => HtmlFormat.Appendable, messageKeyPrefix: String, expectedGuidanceKeys: String*) =
      "behave like a normal page" when {
        "rendered" must {
          "have the correct banner title" in {
            val doc  = asDocument(view())
            val nav  = Option {
              doc.getElementById("proposition-menu")
            }.getOrElse(
              doc
                .getElementsByAttributeValue("class", "hmrc-header__service-name hmrc-header__service-name--linked")
                .first()
                .parent()
            )
            val span = nav.children.first
            span.text mustBe messagesApi("site.service_name")(Lang(Locale.UK))
          }

          "display the correct browser title" in {
            val doc = asDocument(view())
            assertEqualsValue(
              doc,
              "title",
              messages("service.title", messages(s"$messageKeyPrefix.heading", "Wombles Inc"))
            )
          }

          "display the correct page title" in {
            val doc = asDocument(view())
            assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", "Wombles Inc")
          }

          "display the correct guidance" in {
            val doc = asDocument(view())
            for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
          }

          "display language toggles" in {
            val doc = asDocument(view())
            doc.getElementById("cymraeg-switch") != null || !doc
              .getElementsByAttributeValue("href", "/valuation-office-agency-contact-frontend/language/cymraeg")
              .isEmpty
          }
        }
      }

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

    "contain checkbox hint for cateringOperationOrLettingAccommodationCheckboxesDetails-hint" in {
      val doc          = asDocument(createViewUsingForm(form))
      val checkboxHint = doc.getElementById(s"$messageKeyPrefix-hint").text()
      assert(checkboxHint == messages("hint.cateringOperationOrLettingAccommodationCheckboxesDetails"))
    }

    "contain checkbox for the value Rates" in {
      val doc      = asDocument(createViewUsingForm(form))
      val checkbox = doc.getElementById("rates")
      assert(checkbox != null)
      assert(checkbox.attr("name") == messageKeyPrefix)
      //      assert(checkbox.attr("value") == value)
      assertContainsText(doc, messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.rates"))
    }

    "contain checkbox for the value Property insurance" in {
      val doc      = asDocument(createViewUsingForm(form))
      val checkbox = doc.getElementById("propertyInsurance")
      assert(checkbox != null)
      assert(checkbox.attr("name") == messageKeyPrefix)
      //      assert(checkbox.attr("value") == value)
      assertContainsText(
        doc,
        messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.propertyInsurance")
      )
    }

    "contain checkbox for the value Outside repairs" in {
      val doc      = asDocument(createViewUsingForm(form))
      val checkbox = doc.getElementById("outsideRepairs")
      assert(checkbox != null)
      assert(checkbox.attr("name") == messageKeyPrefix)
      //      assert(checkbox.attr("value") == value)
      assertContainsText(
        doc,
        messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.outsideRepairs")
      )
    }

    "contain checkbox for the value Inside repairs" in {
      val doc      = asDocument(createViewUsingForm(form))
      val checkbox = doc.getElementById("insideRepairs")
      assert(checkbox != null)
      assert(checkbox.attr("name") == messageKeyPrefix)
      //      assert(checkbox.attr("value") == value)
      assertContainsText(
        doc,
        messages("checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.insideRepairs")
      )
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc            = asDocument(createViewUsingForm(form))
      val continueButton = doc.getElementById("continue").text()
      assert(continueButton == messages("button.label.continue"))
    }

    "contain get help section" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("helpWithService.title")))
    }

    "contain get help section basic details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithServiceHeader")))
      assert(doc.toString.contains(messages("common.helpWithService")))
    }
  }
}
