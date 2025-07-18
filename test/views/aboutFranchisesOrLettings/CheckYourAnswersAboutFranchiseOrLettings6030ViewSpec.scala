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

package views.aboutFranchisesOrLettings

import actions.SessionRequest
import form.aboutfranchisesorlettings.CheckYourAnswersAboutFranchiseOrLettingsForm
import models.submissions.common.AnswersYesNo
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersAboutFranchiseOrLettings6030ViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  def cyaFranchiseOrLettingsView = inject[views.html.aboutfranchisesorlettings.checkYourAnswersAboutFranchiseOrLettings]

  val messageKeyPrefix = "checkYourAnswersAboutFranchiseOrLettings"

  override val form = CheckYourAnswersAboutFranchiseOrLettingsForm.theForm

  val backLink = controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url

  val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6030YesSession, fakeRequest)

  def createView = () => cyaFranchiseOrLettingsView(form, backLink)(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    cyaFranchiseOrLettingsView(form, backLink)(using sessionRequest, messages)

  "Check Your Answers About Franchise Or Lettings view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
