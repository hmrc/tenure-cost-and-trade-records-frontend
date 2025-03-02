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

package views.downloadFORTypeForm

import controllers.downloadFORTypeForm.routes
import form.ReferenceNumberForm
import models.submissions.ReferenceNumber
import org.scalatest.matchers.must.Matchers.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class DownloadPDFReferenceNumberViewSpec extends QuestionViewBehaviours[ReferenceNumber] {

  val messageKeyPrefix = "referenceNumber"

  override val form = ReferenceNumberForm.theForm

  def createView = () =>
    referenceNumberView(form, call = routes.DownloadPDFReferenceNumberController.submit())(fakeRequest, messages)

  def createViewUsingForm = (form: Form[ReferenceNumber]) =>
    referenceNumberView(form, call = routes.DownloadPDFReferenceNumberController.submit())(fakeRequest, messages)

  "Download PDF reference number view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "referenceNumber"
    )

    "has a link marked with back.link.label leading to the Login Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.routes.Application.index.url
    }

    "paragraph text" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("referenceNumber.paragraph")))
    }

    "hint text" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("hint.referenceNumber")))
    }

    "contain link for I do not have a reference number" in {
      val doc = asDocument(createView())
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("label.requestReference")))
      assert(
        doc.toString.contains(
          controllers.requestReferenceNumber.routes.RequestReferenceNumberController.startWithSession().url
        )
      )
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
