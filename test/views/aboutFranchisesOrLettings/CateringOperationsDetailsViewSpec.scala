/*
 * Copyright 2024 HM Revenue & Customs
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

import form.aboutfranchisesorlettings.FranchiseTypeDetailsForm
import models.ForType.*
import models.pages.Summary
import models.submissions.aboutfranchisesorlettings.BusinessDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CateringOperationsDetailsViewSpec extends QuestionViewBehaviours[BusinessDetails] {

  val messageKeyPrefix     = "cateringOperationOrLettingAccommodationDetails"
  val messageKeyPrefix6015 = "concessionDetails"

  override val form = FranchiseTypeDetailsForm.theForm

  def createView = () =>
    cateringOperationDetailsView(
      form,
      Some(0),
      messageKeyPrefix6015,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url,
      Summary("99996010001"),
      FOR6010
    )(fakeRequest, messages)

  def createViewUsingForm = (form: Form[BusinessDetails]) =>
    cateringOperationDetailsView(
      form,
      Some(0),
      messageKeyPrefix6015,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url,
      Summary("99996010001"),
      FOR6010
    )(fakeRequest, messages)

  "Catering operation details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "operatorName",
      "typeOfBusiness",
      "cateringAddress.buildingNameNumber",
      "cateringAddress.street1",
      "cateringAddress.town",
      "cateringAddress.county",
      "cateringAddress.postcode"
    )

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseLettings"))
    }

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
