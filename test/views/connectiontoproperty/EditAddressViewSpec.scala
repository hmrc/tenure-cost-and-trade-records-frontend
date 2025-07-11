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

package views.connectiontoproperty

import form.connectiontoproperty.EditAddressForm
import models.pages.Summary
import models.submissions.common.Address
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class EditAddressViewSpec extends QuestionViewBehaviours[Address] {

  val messageKeyPrefix = "editAddress"

  val backLink = controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url

  override val form = EditAddressForm.editAddressForm

  def createView = () => editAddressView(form, Summary("99996010001"), backLink)(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[Address]) =>
    editAddressView(form, Summary("99996010001"), backLink)(using fakeRequest, messages)

  "Edit Address view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "editAddress.buildingNameNumber",
      "editAddress.street1",
      "editAddress.town",
      "editAddress.county",
      "editAddress.postcode"
    )

    "has a link marked with back.link.label leading to the Are still connected Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.connectionToTheProperty"))
    }

    "contain continue button with the value Continue" in {
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
}
