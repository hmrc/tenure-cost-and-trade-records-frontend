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

import form.Form6010.CateringOperationOrLettingAccommodationForm
import models.submissions.Form6010.CateringOperationOrLettingAccommodationDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class LettingOtherPartOfPropertyDetailsViewSpec
    extends QuestionViewBehaviours[CateringOperationOrLettingAccommodationDetails] {

  def cateringOperationDetailsView =
    app.injector.instanceOf[views.html.form.cateringOperationOrLettingAccommodationDetails]

  val messageKeyPrefix = "lettingOtherPartOfPropertyDetails"

  override val form = CateringOperationOrLettingAccommodationForm.cateringOperationOrLettingAccommodationForm

  def createView = () =>
    cateringOperationDetailsView(
      form,
      messageKeyPrefix,
      controllers.Form6010.routes.CateringOperationOrLettingAccommodationController.show().url
    )(fakeRequest, messages)

  def createViewUsingForm = (form: Form[CateringOperationOrLettingAccommodationDetails]) =>
    cateringOperationDetailsView(
      form,
      messageKeyPrefix,
      controllers.Form6010.routes.CateringOperationOrLettingAccommodationController.show().url
    )(fakeRequest, messages)

  "Catering operation details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "lettingOperatorName",
      "lettingTypeOfBusiness",
      "lettingAddress.buildingNameNumber",
      "lettingAddress.street1",
      "lettingAddress.town",
      "lettingAddress.county",
      "lettingAddress.postcode"
    )

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.Form6010.routes.CateringOperationOrLettingAccommodationController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseLettings"))
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
