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

package views.requestReferenceNumber

import actions.SessionRequest
import controllers.FeedbackFormMapper
import form.Feedback
import play.api.data.Form
import play.api.mvc.AnyContent
import play.twirl.api.Html
import views.behaviours.ViewBehaviours

class RequestReferenceNumberConfirmationViewSpec extends ViewBehaviours:

  val messageKeyPrefix                           = "label.connectionToProperty.confirm"
  val sessionRequest: SessionRequest[AnyContent] = SessionRequest(baseFilled6010Session, getRequest)

  val form: Form[Feedback]   = FeedbackFormMapper.feedbackForm
  def createView: () => Html = () => requestReferenceNumberConfirmationView(form)(using sessionRequest, messages)

  "confirmation request reference number view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "contain confirmation text" in {
      val page = asDocument(createView()).toString

      assert(page.contains(messages("label.connectionToProperty.requestRefNum")))
      assert(page.contains(messages("label.vacantProperty.whatNext")))
      assert(page.contains(messages("label.connectionToProperty.reissued")))
      assert(page.contains(messages("label.connectionToProperty.contact")))
      assert(page.contains(messages("list.connectionToProperty.p1")))
      assert(page.contains(messages("list.connectionToProperty.p2")))
      assert(page.contains(messages("list.connectionToProperty.p3")))
    }
  }
