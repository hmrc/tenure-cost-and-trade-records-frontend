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

package views.requestReferenceNumber

import actions.SessionRequest
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class RequestReferenceNumberCheckYourAnswersViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "requestReferenceNumber.checkYourAnswers"

  override val form: Form[String] = defaultForm

  val sessionRequest = SessionRequest(baseFilled6010Session, fakeRequest)

  def createView = () =>
    requestReferenceNumberCheckYourAnswersView(notConnected6010NoSession)(using sessionRequest, messages)

  "Check Your Answers Additional Information view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the request reference number contact details page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.requestReferenceNumber.routes.RequestReferenceNumberContactDetailsController
        .show()
        .url
    }

    "contain submit and send button with the value Accept and send" in {
      val doc         = asDocument(createView())
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.confirm"))
    }
  }

}
