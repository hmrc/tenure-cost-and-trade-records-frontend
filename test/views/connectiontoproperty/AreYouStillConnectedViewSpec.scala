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

import form.connectiontoproperty.{AreYouStillConnectedForm, EditAddressForm}
import models.submissions.common.Address
import models.submissions.connectiontoproperty.{AddressConnectionType, AddressConnectionTypeNo, AddressConnectionTypeYes, AddressConnectionTypeYesChangeAddress}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.api.i18n.Lang
import views.behaviours.QuestionViewBehaviours

import java.util.Locale

class AreYouStillConnectedViewSpec extends QuestionViewBehaviours[AddressConnectionType] {

  def areYouStillConnectedView = app.injector.instanceOf[views.html.connectiontoproperty.areYouStillConnected]

  val messageKeyPrefix = "areYouConnected"

  override val form = AreYouStillConnectedForm.areYouStillConnectedForm

  def createView = () => areYouStillConnectedView(form, prefilledAddress)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[AddressConnectionType]) =>
    areYouStillConnectedView(form, prefilledAddress)(fakeRequest, messages)

  "Are you still connected view" must {
    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc  = asDocument(createView())
          val nav  = Option {
            doc.getElementById("proposition-menu")
          }.getOrElse(
            doc
              .getElementsByAttributeValue("class", "hmrc-header__service-name hmrc-header__service-name--linked")
              .first()
              .parent()
          )
          val span = nav.children.first
          span.text mustBe messagesApi("site.service_name")(Lang(Locale.UK))
        }

        "display the correct browser title" in {
          val doc = asDocument(createView())
          assertEqualsValue(
            doc,
            "title",
            messages("service.title", messages(s"$messageKeyPrefix.heading", prefilledAddress.singleLine))
          )
        }

        "display the correct page title" in {
          val doc = asDocument(createView())
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", prefilledAddress.singleLine)
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

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
