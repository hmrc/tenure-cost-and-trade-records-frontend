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

package views.requestReferenceNumber

import form.requestReferenceNumber.RequestReferenceNumberForm
import models.submissions.requestReferenceNumber.NoReferenceNumber
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class NoReferenceNumberViewSpec extends QuestionViewBehaviours[NoReferenceNumber] {

  val messageKeyPrefix = "noReferenceNumber"

  override val form = RequestReferenceNumberForm.noReferenceNumberForm

  def createView = () => noReferenceAddressView(form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[NoReferenceNumber]) => noReferenceAddressView(form)(fakeRequest, messages)

  "No reference number view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "noReferenceNumberAddress.buildingNameNumber",
      "noReferenceNumberAddress.street1",
      "noReferenceNumberAddress.town",
      "noReferenceNumberAddress.county",
      "noReferenceNumberAddress.postcode"
    )

    "has a link marked with back.link.label leading to the Login Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.routes.LoginController.show().url
    }

    "contain an input for noReferenceNumberBusinessTradingName" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "noReferenceNumberBusinessTradingName")
    }

    "contain an input for noReferenceNumberAddress.buildingNameNumber" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "noReferenceNumberAddress.buildingNameNumber")
    }

    "contain an input for noReferenceNumberAddress.street1" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "noReferenceNumberAddress.street1")
    }

    "contain an input for noReferenceNumberAddress.town" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "noReferenceNumberAddress.town")
    }

    "contain an input for noReferenceNumberAddress.county" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "noReferenceNumberAddress.county")
    }

    "contain an input for noReferenceNumberAddress.postcode" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "noReferenceNumberAddress.postcode")
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
