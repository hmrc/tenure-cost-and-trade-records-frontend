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

import form.connectiontoproperty.ConnectionToThePropertyForm
import models.submissions.connectiontoproperty.{ConnectionToProperty, ConnectionToThePropertyOccupierAgent, ConnectionToThePropertyOccupierTrustee, ConnectionToThePropertyOwnerAgent, ConnectionToThePropertyOwnerTrustee}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class connectionToThePropertyViewSpec extends QuestionViewBehaviours[ConnectionToProperty] {

  def connectionToPropertyView = app.injector.instanceOf[views.html.connectiontoproperty.connectionToTheProperty]

  val messageKeyPrefix = "connectionToTheProperty"

  override val form = ConnectionToThePropertyForm.connectionToThePropertyForm

  val backLink = controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url

  def createView = () => connectionToPropertyView(form, backLink)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[ConnectionToProperty]) =>
    connectionToPropertyView(form, backLink)(fakeRequest, messages)

  "Edit Address view" must {

    behave like normalPage(createView, messageKeyPrefix, "hint")

    "contain radio buttons for the value occupier/trustee" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "connectionToTheProperty",
        "connectionToTheProperty",
        ConnectionToThePropertyOccupierTrustee.name,
        false
      )
      assertContainsText(doc, messages("label.occupierTrustee"))
      assertContainsText(doc, messages("hint.occupierTrustee"))
    }

    "contain radio buttons for the value owner/trustee" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "connectionToTheProperty-2",
        "connectionToTheProperty",
        ConnectionToThePropertyOwnerTrustee.name,
        false
      )
      assertContainsText(doc, messages("label.ownerTrustee"))
      assertContainsText(doc, messages("hint.ownerTrustee"))
    }

    "contain radio buttons for the value occupier agent" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "connectionToTheProperty-3",
        "connectionToTheProperty",
        ConnectionToThePropertyOccupierAgent.name,
        false
      )
      assertContainsText(doc, messages("label.occupierAgent"))
    }

    "contain radio buttons for the value owner agent" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "connectionToTheProperty-4",
        "connectionToTheProperty",
        ConnectionToThePropertyOwnerAgent.name,
        false
      )
      assertContainsText(doc, messages("label.ownerAgent"))
    }

    "has a link marked with back.link.label leading to the Are still connected Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain get help section" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("helpWithService.title")))
    }

    "contain get help section for people completing form" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("connection.helpWithServicePeopleCompletingFormHeader")))
      assert(doc.toString.contains(messages("connection.helpWithServicePeopleCompletingForm")))
    }

    "contain get help section for trustee completing form" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("connection.helpWithServicePeopleTrusteeHeader")))
      assert(doc.toString.contains(messages("connection.helpWithServicePeopleTrustee")))
    }

    "contain get help section for agents completing form" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("connection.helpWithServicePeopleAgentHeader")))
      assert(doc.toString.contains(messages("connection.helpWithServicePeopleAgent")))
    }

    "contain get help section basic details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithServiceHeader")))
      assert(doc.toString.contains(messages("common.helpWithService")))
    }
  }
}
