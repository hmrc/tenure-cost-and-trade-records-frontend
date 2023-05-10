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

package views.notconnected

import form.notconnected.RemoveConnectionForm
import models.pages.Summary
import models.submissions.notconnected.RemoveConnectionsDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class RemoveConnectionViewSpec extends QuestionViewBehaviours[RemoveConnectionsDetails] {

  val messageKeyPrefix = "removeConnection"

  override val form: Form[RemoveConnectionsDetails] = RemoveConnectionForm.removeConnectionForm

  def createView: () => Html = () =>
    removeConnectionView(form, Summary("99996010001", Some(prefilledAddress)))(fakeRequest, messages)

  def createViewUsingForm: Form[RemoveConnectionsDetails] => Html = (form: Form[RemoveConnectionsDetails]) =>
    removeConnectionView(form, Summary("99996010001", Some(prefilledAddress)))(fakeRequest, messages)

  "Past connection view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "removeConnectionFullName",
      "removeConnectionDetails.email",
      "removeConnectionDetails.phone"
    )

    "has a reference number and address banner" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Reference:")
      assertContainsText(doc, "99996010/001")
      assertContainsText(doc, "Property:")
      assertContainsText(doc, "001, GORING ROAD, GORING-BY-SEA, WORTHING, BN12 4AX")
    }

    "has a link marked with back.link.label leading to have you ever had a connection Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.notconnected.routes.PastConnectionController.show().url
    }

    "contain an input for removeConnectionAdditionalInfo" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "removeConnectionAdditionalInfo")
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
