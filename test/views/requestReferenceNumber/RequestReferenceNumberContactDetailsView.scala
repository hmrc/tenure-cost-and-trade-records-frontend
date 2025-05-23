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

package views.requestReferenceNumber

import form.requestReferenceNumber.RequestReferenceNumberContactDetailsForm
import models.submissions.requestReferenceNumber.RequestReferenceNumberContactDetails
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class RequestReferenceNumberContactDetailsView extends QuestionViewBehaviours[RequestReferenceNumberContactDetails] {

  val messageKeyPrefix = "requestReferenceNumberContactDetails"

  override val form = RequestReferenceNumberContactDetailsForm.theForm

  def createView = () => requestReferenceNumberContactDetailsView(form)(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[RequestReferenceNumberContactDetails]) =>
    requestReferenceNumberContactDetailsView(form)(using fakeRequest, messages)

  "No reference number view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "requestReferenceNumberContactDetailsFullName",
      "requestReferenceNumberContactDetails.phone",
      "requestReferenceNumberContactDetails.email"
    )

    "has a link marked with back.link.label leading to the Login Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.requestReferenceNumber.routes.RequestReferenceNumberPropertyDetailsController
        .show()
        .url
    }

    "contain an input for requestReferenceNumberContactDetailsFullName" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "requestReferenceNumberContactDetailsFullName")
    }

    "contain an input for requestReferenceNumberContactDetails.phone" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "requestReferenceNumberContactDetails.phone")
    }

    "contain an input for requestReferenceNumberContactDetails.email" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "requestReferenceNumberContactDetails.email")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
