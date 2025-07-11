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

package views.additionalInformation

import actions.SessionRequest
import models.submissions.common.AnswersYesNo
import form.additionalinformation.CheckYourAnswersAdditionalInformationForm
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersAdditionalInformationViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "checkYourAnswersAdditionalInformation"

  override val form = CheckYourAnswersAdditionalInformationForm.checkYourAnswersAdditionalInformationForm

  val backLink = controllers.additionalinformation.routes.FurtherInformationOrRemarksController.show().url

  val sessionRequest = SessionRequest(baseFilled6010Session, fakeRequest)

  def createView = () =>
    checkYourAnswersAdditionalInformationView(form, notConnected6010NoSession)(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    checkYourAnswersAdditionalInformationView(form, notConnected6010NoSession)(using sessionRequest, messages)

  "Check Your Answers Additional Information view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the further information Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.additionalinformation.routes.FurtherInformationOrRemarksController.show().url
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
