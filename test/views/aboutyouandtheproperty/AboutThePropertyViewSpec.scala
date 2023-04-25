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

package views.aboutyouandtheproperty

import form.aboutyouandtheproperty.AboutThePropertyForm
import models.pages.Summary
import models.submissions.aboutyouandtheproperty._
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class AboutThePropertyViewSpec extends QuestionViewBehaviours[PropertyDetails] {

  val messageKeyPrefix = "aboutProperty"

  override val form: Form[PropertyDetails] = AboutThePropertyForm.aboutThePropertyForm

  def createView: () => Html = () =>
    aboutThePropertyView(form, "FOR6010", Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm: Form[PropertyDetails] => Html = (form: Form[PropertyDetails]) =>
    aboutThePropertyView(form, "FOR6010", Summary("99996010001"))(fakeRequest, messages)

  def createView6015: () => Html = () =>
    aboutThePropertyView(form, "FOR6015", Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm6015: Form[PropertyDetails] => Html = (form: Form[PropertyDetails]) =>
    aboutThePropertyView(form, "FOR6015", Summary("99996010001"))(fakeRequest, messages)

  "About the property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, "currentOccupierName")

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutyouandtheproperty.routes.AboutYouController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain radio buttons for the value public house" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyCurrentlyUsed",
        "propertyCurrentlyUsed",
        CurrentPropertyPublicHouse.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.publicHouse"))
    }

    "contain radio buttons for the value wine bar or cafe bar" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyCurrentlyUsed-2",
        "propertyCurrentlyUsed",
        CurrentPropertyWineBarOrCafe.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.wineCafeBar"))
    }

    "contain radio buttons for the value other bar" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyCurrentlyUsed-3",
        "propertyCurrentlyUsed",
        CurrentPropertyOtherBar.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.otherBar"))
    }

    "contain radio buttons for the value pub or restaurant" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyCurrentlyUsed-4",
        "propertyCurrentlyUsed",
        CurrentPropertyPubAndRestaurant.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.pubRestaurant"))
    }

    "contain radio buttons for the value licenced restaurant" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyCurrentlyUsed-5",
        "propertyCurrentlyUsed",
        CurrentPropertyLicencedRestaurant.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.licencedRestaurant"))
    }

    "contain radio buttons for the value hotel" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyCurrentlyUsed-6",
        "propertyCurrentlyUsed",
        CurrentPropertyHotel.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.hotel"))
    }

    "contain radio buttons for the value disco or nightclub" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyCurrentlyUsed-7",
        "propertyCurrentlyUsed",
        CurrentPropertyDiscoOrNightclub.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.discoNightclub"))
    }

    "contain radio buttons for the value other" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyCurrentlyUsed-8",
        "propertyCurrentlyUsed",
        CurrentPropertyOther.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.other"))
    }

    "contain save and continue button with the value Save and Continue" in {
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

  "About the property view 6015" must {

    behave like normalPage(createView6015, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, "currentOccupierName")

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutyouandtheproperty.routes.AboutYouController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain checkbox for the value hotel" in {
      val doc = asDocument(createViewUsingForm6015(form))
      assertContainsCheckBox(
        doc,
        "propertyCurrentlyUsed",
        "propertyCurrentlyUsed",
        CurrentPropertyHotel.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.hotel"))
    }

    "contain checkbox for the value health Spa" in {
      val doc = asDocument(createViewUsingForm6015(form))
      assertContainsCheckBox(
        doc,
        "propertyCurrentlyUsed-2",
        "propertyCurrentlyUsed",
        CurrentPropertyHealthSpa.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.healthSpa"))
    }

    "contain checkbox for the value lodge and restaurant" in {
      val doc = asDocument(createViewUsingForm6015(form))
      assertContainsCheckBox(
        doc,
        "propertyCurrentlyUsed-3",
        "propertyCurrentlyUsed",
        CurrentPropertyLodgeAndRestaurant.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.lodgeAndRestaurant"))
    }

    "contain checkbox for the value conference centre" in {
      val doc = asDocument(createViewUsingForm6015(form))
      assertContainsCheckBox(
        doc,
        "propertyCurrentlyUsed-4",
        "propertyCurrentlyUsed",
        CurrentPropertyConferenceCentre.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.conferenceCentre"))
    }

    "contain checkbox for the value other" in {
      val doc = asDocument(createViewUsingForm6015(form))
      assertContainsCheckBox(
        doc,
        "propertyCurrentlyUsed-5",
        "propertyCurrentlyUsed",
        CurrentPropertyOther.name,
        isChecked = false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.other"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm6015(form))
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
