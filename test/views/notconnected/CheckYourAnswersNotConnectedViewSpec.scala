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
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersNotConnectedViewSpec extends QuestionViewBehaviours[NotConnectedContactDetails] {

  def checkYourAnswersNotConnectedView =
    app.injector.instanceOf[views.html.notconnected.checkYourAnswersNotConnected]

  val messageKeyPrefix = "checkYourAnswersNotConnected"

  override val form = NotConnectedForm.notConnectedForm

  def createView = () =>
    checkYourAnswersNotConnectedView(
      Some("no"),
      Some("no"),
      "John Smith",
      "0123456789123",
      "test@test.com",
      Some("Some additional info")
    )(fakeRequest, messages)

  def createViewUsingForm = (form: Form[NotConnectedContactDetails]) =>
    checkYourAnswersNotConnectedView(
      Some("no"),
      Some("no"),
      "John Smith",
      "0123456789123",
      "test@test.com",
      Some("Some additional info")
    )(fakeRequest, messages)

  "Check Your Answers Additional Information view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the further information Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.notconnected.routes.RemoveConnectionController.show().url
    }
  }
}
