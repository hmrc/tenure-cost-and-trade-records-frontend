/*
 * Copyright 2026 HM Revenue & Customs
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
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class RemoveConnectionViewSpec extends QuestionViewBehaviours[RemoveConnectionsDetails]:

  private val messageKeyPrefix = "removeConnection"

  private val backLink = controllers.notconnected.routes.PastConnectionController.show().url

  override val form: Form[RemoveConnectionsDetails] = RemoveConnectionForm.removeConnectionForm

  private def createView: () => Html =
    () => removeConnectionView(form, Summary("99996010001", Some(prefilledAddress)), backLink)(using fakeRequest, messages)

  private def createViewUsingForm: Form[RemoveConnectionsDetails] => Html =
    form => removeConnectionView(form, Summary("99996010001", Some(prefilledAddress)), backLink)(using fakeRequest, messages)

  "Past connection view" should {

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
      assertContainsText(doc, "001, GORING ROAD, GORING-BY-SEA, WORTHING, WEST SUSSEX, BN12 4AX")
    }

    "has a link marked with back.link.label leading to have you ever had a connection Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.notconnected.routes.PastConnectionController.show().url
    }

    "Section heading is visible" in {
      val doc  = asDocument(createViewUsingForm(form))
      val html = doc.getElementsByClass("govuk-caption-m").html()
      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.connectionToTheProperty")}"""
    }

    "contain an input for removeConnectionAdditionalInfo" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "removeConnectionAdditionalInfo")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()
      assert(loginButton == messages("button.continue.label"))
    }
  }
