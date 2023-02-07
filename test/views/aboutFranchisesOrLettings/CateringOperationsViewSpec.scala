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

import form.Form6010.{CateringOperationForm, FranchiseOrLettingsTiedToPropertyForm}
import models.submissions.Form6010.{CateringOperationDetails, CateringOperationNo, CateringOperationYes, FranchiseOrLettingsTiedToPropertiesDetails, FranchiseOrLettingsTiedToPropertiesNo, FranchiseOrLettingsTiedToPropertiesYes}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CateringOperationsViewSpec extends QuestionViewBehaviours[CateringOperationDetails] {

  def cateringOperationView = app.injector.instanceOf[views.html.form.cateringOperationOrLettingAccommodation]

  val messageKeyPrefix = "cateringOperationOrLettingAccommodation"

  override val form = CateringOperationForm.cateringOperationForm

  def createView = () =>
    cateringOperationView(
      form,
      messageKeyPrefix,
      controllers.Form6010.routes.FranchiseOrLettingsTiedToPropertyController.show().url
    )(fakeRequest, messages)

  def createViewUsingForm = (form: Form[CateringOperationDetails]) =>
    cateringOperationView(
      form,
      messageKeyPrefix,
      controllers.Form6010.routes.FranchiseOrLettingsTiedToPropertyController.show().url
    )(fakeRequest, messages)

  "Franchise or lettings tied to property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.Form6010.routes.FranchiseOrLettingsTiedToPropertyController.show.url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseLettings"))
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "cateringOperationOrLettingAccommodation",
        "cateringOperationOrLettingAccommodation",
        CateringOperationYes.name,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "cateringOperationOrLettingAccommodation-2",
        "cateringOperationOrLettingAccommodation",
        CateringOperationNo.name,
        false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
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
