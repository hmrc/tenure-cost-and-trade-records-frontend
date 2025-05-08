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

package views

import controllers.{LoginController, LoginDetails}
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class LoginViewSpec extends QuestionViewBehaviours[LoginDetails] {

  def login = inject[views.html.login]

  val messageKeyPrefix = "login"

  override val form = LoginController.loginForm

  def createView = () => login(form)(using fakeRequest, messages)

  def createViewUsingForm = (form: Form[LoginDetails]) => login(form)(using fakeRequest, messages)

  "Login view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, "referenceNumber", "postcode")

    "contain Login button with the value Login" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
