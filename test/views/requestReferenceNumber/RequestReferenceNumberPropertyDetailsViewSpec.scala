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

import form.requestReferenceNumber.RequestReferenceNumberPropertyDetailsForm
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class RequestReferenceNumberPropertyDetailsViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "requestReferenceNumber"

  override val form: Form[String] = RequestReferenceNumberPropertyDetailsForm.theForm

  def createView = () => requestReferenceNumberPropertyDetailsView(form)(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) =>
    requestReferenceNumberPropertyDetailsView(form)(using fakeRequest, messages)

  "No reference number view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "businessTradingName"
    )

    "has a link marked with back.link.label leading to the Login Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.routes.LoginController.show.url
    }

    "contain subheading" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("requestReferenceNumber.subheading")))
    }

    "contain request reference number para one" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("requestReferenceNumber.p1")))
    }

    "contain subheading 2" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("requestReferenceNumber.subheading2")))
    }

    "contain request reference number para two" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("requestReferenceNumber.p2")))
    }

    "contain request reference number para three" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("requestReferenceNumber.p3")))
    }

    "contain subheading 3" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("requestReferenceNumber.subheading3")))
    }

    "contain an input for businessTradingName" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "businessTradingName")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
