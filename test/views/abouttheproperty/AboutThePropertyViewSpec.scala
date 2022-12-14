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

package views.abouttheproperty

import form.abouttheproperty.AboutThePropertyForm
import models.submissions.abouttheproperty._
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class AboutThePropertyViewSpec extends QuestionViewBehaviours[PropertyDetails] {

  def aboutThePropertyView = app.injector.instanceOf[views.html.abouttheproperty.aboutTheProperty]

  val messageKeyPrefix = "aboutProperty"

  override val form = AboutThePropertyForm.aboutThePropertyForm

  def createView = () => aboutThePropertyView(form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[PropertyDetails]) => aboutThePropertyView(form)(fakeRequest, messages)

  "About the property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutyou.routes.AboutYouController.show.url
    }

    "contain an input for currentOccupierName" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "currentOccupierName")
    }

    "contain radio buttons for the value public house" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyCurrentlyUsed",
        "propertyCurrentlyUsed",
        CurrentPropertyPublicHouse.name,
        false
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
        false
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
        false
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
        false
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
        false
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
        false
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
        false
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
        false
      )
      assertContainsText(doc, messages("propertyCurrentlyUsed.other"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain get help section current occupier details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("currentOccupier.helpWithServicePeopleCurrentOccupierHeader")))
      assert(doc.toString.contains(messages("currentOccupier.helpWithServicePeopleCurrentOccupier")))
    }

    "contain get help section use of property details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("currentOccupier.helpWithServicePeopleUsePropertyHeader")))
      assert(doc.toString.contains(messages("currentOccupier.helpWithServicePeopleUseProperty")))
    }

    "contain get help section basic details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithServiceHeader")))
      assert(doc.toString.contains(messages("common.helpWithService")))
    }
  }
}
