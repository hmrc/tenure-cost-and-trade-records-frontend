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

import form.notconnected.NotConnectedForm
import models.submissions.notconnected.NotConnectedContactDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersNotConnectedViewSpec extends QuestionViewBehaviours[NotConnectedContactDetails] {

  val messageKeyPrefix = "checkYourAnswersNotConnected"

  override val form: Form[NotConnectedContactDetails] = NotConnectedForm.notConnectedForm

  def createView: () => Html = () => checkYourAnswersNotConnectedView(notConnected6010NoSession)(fakeRequest, messages)

  def createViewUsingForm: Form[NotConnectedContactDetails] => Html = (form: Form[NotConnectedContactDetails]) =>
    checkYourAnswersNotConnectedView(notConnected6010NoSession)(fakeRequest, messages)

  "Check Your Answers Additional Information view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a reference number and address banner" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Reference:")
      assertContainsText(doc, "99996010/004")
      assertContainsText(doc, "Property:")
      assertContainsText(doc, "001, GORING ROAD, GORING-BY-SEA, WORTHING, WEST SUSSEX, BN12 4AX")
    }

    "has a link marked with back.link.label leading to the further information Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.notconnected.routes.RemoveConnectionController.show().url
    }

    "contain are you still connected field" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Connected to the property?")
    }

    "contain still connected string boolean" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "No")
    }

    "contain have you ever been connected field" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Ever been connected to the property?")
    }

    "contain ever been connected string boolean" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Yes")
    }

    "contain contact details field" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Contacts and additional information")
    }

    "contain contact details phone string" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "12345678901")
    }

    "contain contact details email string" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "test@email.com")
    }

    "contain legal declaration" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages("declaration.heading"))
      assertContainsText(doc, messages("declaration.information"))
      assertContainsText(doc, messages("hint.declaration"))
    }

    "contain submit button with the value Accept and Send" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.send"))
    }
  }
}
