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

import form.connectiontoproperty.AreYouStillConnectedForm
import models.pages.Summary
import models.submissions.connectiontoproperty.{AddressConnectionType, AddressConnectionTypeNo, AddressConnectionTypeYes, AddressConnectionTypeYesChangeAddress}
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class AreYouStillConnectedViewSpec extends QuestionViewBehaviours[AddressConnectionType] {

  val messageKeyPrefix = "areYouConnected"

  override val form = AreYouStillConnectedForm.theForm

  def createView = () => areYouStillConnectedView(form, Summary("99996010001"), "", false)(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[AddressConnectionType]) =>
    areYouStillConnectedView(form, Summary("99996010001"), "", false)(using fakeRequest, messages)

  "Are you still connected view" must {

    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in
          checkServiceNameInHeaderBanner(createView())

        "Section heading is visible" in {
          val doc         = asDocument(createViewUsingForm(form))
          val sectionText = doc.getElementsByClass("govuk-caption-m").text()
          assert(sectionText == messages("label.section.connectionToTheProperty"))
        }

        "display the correct browser title" in {
          val doc = asDocument(createView())
          assertEqualsValue(
            doc,
            "title",
            messages("service.title", messages("areYouConnected.heading", ""))
          )
        }

        "display the correct page title" in {
          val doc = asDocument(createView())
          assertEqualsValue(
            doc,
            "h1",
            messages("areYouConnected.title", "")
          )
        }

        "display language toggles" in {
          val doc = asDocument(createView())
          doc.getElementById("cymraeg-switch") != null || !doc
            .getElementsByAttributeValue("href", "/valuation-office-agency-contact-frontend/language/cymraeg")
            .isEmpty
        }
      }
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(doc, "isRelated", "isRelated", AddressConnectionTypeYes.name, false)
      assertContainsText(doc, messages("label.yes.oes"))
    }

    "contain radio buttons for the value yes edit address" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(doc, "isRelated-2", "isRelated", AddressConnectionTypeYesChangeAddress.name, false)
      assertContainsText(doc, messages("label.areYouConnected.addressUpdate"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(doc, "isRelated-3", "isRelated", AddressConnectionTypeNo.name, false)
      assertContainsText(doc, messages("label.no.nac.oes"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
