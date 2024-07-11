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

import actions.SessionRequest
import controllers.FeedbackFormMapper
import views.behaviours.ViewBehaviours

class confirmationVacantPropertyViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "label.connectionToProperty.confirm"
  val sessionRequest   = SessionRequest(baseFilled6010Session, fakeRequest)

  val form       = FeedbackFormMapper.feedbackForm
  def createView = () => confirmationRequestReferenceNumber(form)(sessionRequest, messages)

  "confirmation request reference number view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "contain confirmation text" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("label.connectionToProperty.requestRefNum")))
      assert(doc.toString.contains(messages("label.connectionToProperty.whatNext")))
      assert(doc.toString.contains(messages("label.connectionToProperty.reissued")))
      assert(doc.toString.contains(messages("label.connectionToProperty.contact")))
      assert(doc.toString.contains(messages("list.connectionToProperty.p1")))
      assert(doc.toString.contains(messages("list.connectionToProperty.p2")))
      assert(doc.toString.contains(messages("list.connectionToProperty.p3")))
    }
  }
}
