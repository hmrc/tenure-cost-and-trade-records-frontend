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

package views.connectiontoproperty

import form.requestReferenceNumber.NoReferenceNumberContactDetailsForm
import models.pages.Summary
import models.submissions.requestReferenceNumber.NoReferenceNumberContactDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class NoReferenceNumberContactDetailsViewSpec extends QuestionViewBehaviours[NoReferenceNumberContactDetails] {

  val messageKeyPrefix = "noReferenceNumberContactDetails"

  override val form = NoReferenceNumberContactDetailsForm.noReferenceNumberContactDetailsForm

  def createView = () => noReferenceNumberContactDetailsView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[NoReferenceNumberContactDetails]) =>
    noReferenceNumberContactDetailsView(form, Summary("99996010001"))(fakeRequest, messages)

  "No reference number view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "noReferenceNumberContactDetailsFullName",
      "noReferenceNumberContactDetails.phone",
      "noReferenceNumberContactDetails.email"
    )

    "has a link marked with back.link.label leading to the Login Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.requestReferenceNumber.routes.NoReferenceNumberController.show().url
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
