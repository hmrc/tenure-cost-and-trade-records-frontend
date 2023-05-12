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

package views

import controllers.{LoginController, LoginDetails}
import play.api.data.Form
import views.behaviours.{QuestionViewBehaviours, ViewBehaviours}

class ConfirmationNotConnectedViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "confirmation"

//  override val form = LoginController.loginForm

  def createView = () => confirmationNotConnectedView(baseFilled6010Session)(fakeRequest, messages)

//  def createViewUsingForm = () => confirmationNotConnectedView(baseFilled6010Session)(fakeRequest, messages)

  "confirmation not connected view" must {

    behave like normalPage(createView, messageKeyPrefix)

//    behave like pageWithTextFields(createViewUsingForm, "referenceNumber", "postcode")

    "has a reference number and address banner" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Reference:")
      assertContainsText(doc, "99996010/004")
      assertContainsText(doc, "Property:")
      assertContainsText(doc, "001, GORING ROAD, GORING-BY-SEA, WORTHING, BN12 4AX")
    }

    "contain confirmation text" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("confirmation.emailConfirm")))
      assert(doc.toString.contains(messages("confirmation.saveCopyOfAnswers")))
      assert(doc.toString.contains(messages("confirmation.unableToRetrieveAnswers")))
      assert(doc.toString.contains(messages("confirmation.whatNext")))
      assert(doc.toString.contains(messages("confirmation.list.1")))
      assert(doc.toString.contains(messages("confirmation.list.2")))
    }
  }
}
