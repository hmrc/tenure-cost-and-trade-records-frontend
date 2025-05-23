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

import form.aboutYourLeaseOrTenure.DoesTheRentPayableForm
import models.ForType.*
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.DoesTheRentPayable
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class DoesTheRentPayableViewSpec extends QuestionViewBehaviours[DoesTheRentPayable] {

  val messageKeyPrefix = "rentPayable"

  override val form = DoesTheRentPayableForm.doesTheRentPayableForm

  def createView = () => doesTheRentPayableView(form, FOR6010, Summary("99996010001"))(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[DoesTheRentPayable]) =>
    doesTheRentPayableView(form, FOR6010, Summary("99996010001"))(using fakeRequest, messages)

  def createView6045 = () => doesTheRentPayableView(form, FOR6045, Summary("99996045001"))(using fakeRequest, messages)

  def createViewUsingForm6045 = (form: Form[DoesTheRentPayable]) =>
    doesTheRentPayableView(form, FOR6045, Summary("99996045001"))(using fakeRequest, messages)

  def createView6048 = () => doesTheRentPayableView(form, FOR6048, Summary("99996048001"))(using fakeRequest, messages)

  def createViewUsingForm6048 = (form: Form[DoesTheRentPayable]) =>
    doesTheRentPayableView(form, FOR6048, Summary("99996048001"))(using fakeRequest, messages)

  "Rent payable view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, "detailsToQuestions")

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "has a link marked with back.link.label leading to the task list Page 6048" in {
      val doc          = asDocument(createView6048())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show().url
    }

    "Section heading is visible 6045" in {
      val doc         = asDocument(createViewUsingForm6045(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "has a link marked with back.link.label leading to the task list Page 6045" in {
      val doc          = asDocument(createView6045())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show().url
    }

    "Section heading is visible 6048" in {
      val doc         = asDocument(createViewUsingForm6048(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain checkbox for the proprietor" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "proprietor", "rentPayable[]", "proprietor", false)
      assertContainsText(doc, messages("checkbox.rentPayable.proprietor"))
    }

    "contain checkbox for the otherProperty" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "otherProperty", "rentPayable[]", "otherProperty", false)
      assertContainsText(doc, messages("checkbox.rentPayable.otherProperty"))
    }

    "contain checkbox for the onlyPart" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "onlyPart", "rentPayable[]", "onlyPart", false)
      assertContainsText(doc, messages("checkbox.rentPayable.onlyPart"))
    }

    "contain checkbox for the onlyLand" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "onlyLand", "rentPayable[]", "onlyLand", false)
      assertContainsText(doc, messages("checkbox.rentPayable.onlyLand"))
    }

    "contain checkbox for the shellUnit" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "shellUnit", "rentPayable[]", "shellUnit", false)
      assertContainsText(doc, messages("checkbox.rentPayable.shellUnit"))
    }

    "contain checkbox for the parkingSpacesGarages" in {
      val doc = asDocument(createViewUsingForm6048(form))
      assertContainsCheckBox(doc, "parkingSpaceGarage", "rentPayable[]", "parkingSpaceGarage", false)
      assertContainsText(doc, messages("checkbox.rentPayable.parkingSpaceGarage"))
    }

    "contain checkbox for the none" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "none", "rentPayable[]", "none", false)
      assertContainsText(doc, messages("checkbox.rentPayable.noneOfThese"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }

}
