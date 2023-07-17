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

import views.behaviours.ViewBehaviours

class confirmationConnectionToPropertyViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "label.connectionToProperty.confirm"

  def createView = () => confirmationConnectionToProperty()(fakeRequest, messages)

  "confirmation not connected view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "contain confirmation text" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("label.connectionToProperty.emailConfirm")))
      assert(doc.toString.contains(messages("label.connectionToProperty.saveCopyOfAnswers.1")))
      assert(doc.toString.contains(messages("label.connectionToProperty.saveCopyOfAnswers.2")))
      assert(doc.toString.contains(messages("label.connectionToProperty.unableToRetrieveAnswers")))
      assert(doc.toString.contains(messages("label.connectionToProperty.whatNext")))
      assert(doc.toString.contains(messages("connectionToProperty.confirm.copy.list.1")))
      assert(doc.toString.contains(messages("connectionToProperty.confirm.copy.list.2")))
    }
  }
}
