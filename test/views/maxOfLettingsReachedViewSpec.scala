/*
 * Copyright 2024 HM Revenue & Customs
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

import actions.SessionRequest
import form.MaxOfLettingsForm
import models.pages.Summary
import models.submissions.MaxOfLettings
import play.api.data.Form
import views.behaviours.ViewBehaviours

class maxOfLettingsReachedViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "maxOf5Lettings"
  val sessionRequest   = SessionRequest(baseFilled6010Session, fakeRequest)

  val form       = MaxOfLettingsForm.maxOfLettingsForm(messages)
  def createView = () =>
    maxOfLettingsReachedView(form, "backLink", "connection", Summary("10000001"))(sessionRequest, messages)

  def createViewUsingForm = (form: Form[MaxOfLettings]) =>
    maxOfLettingsReachedView(form, "backLink", "connection", Summary("99996010001"))(sessionRequest, messages)

  "max of Lettings reached view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "contain text" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("maxOf5Lettings.heading")))
      assert(doc.toString.contains(messages("maxOf5Lettings.contact")))
      assert(doc.toString.contains(messages("maxOf5Lettings.confirm")))
      assert(doc.toString.contains(messages("maxOf5Lettings.link")))
    }
    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain save as draft button with the value Save as draft" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("save").text()
      assert(loginButton == messages("button.label.save"))
    }
  }
}
