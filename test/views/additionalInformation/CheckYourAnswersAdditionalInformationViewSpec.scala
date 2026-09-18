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

package views.additionalInformation

import actions.SessionRequest
import form.additionalinformation.CheckYourAnswersAdditionalInformationForm
import models.submissions.common.AnswersYesNo
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersAdditionalInformationViewSpec extends QuestionViewBehaviours[AnswersYesNo]:

  private val messageKeyPrefix = "checkYourAnswersAdditionalInformation"

  override val form: Form[AnswersYesNo] = CheckYourAnswersAdditionalInformationForm.checkYourAnswersAdditionalInformationForm

  private val sessionRequest = SessionRequest(baseFilled6010Session, getRequest)

  private def createView = () =>
    checkYourAnswersAdditionalInformationView(form, notConnected6010NoSession)(using sessionRequest, messages)

  private def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    checkYourAnswersAdditionalInformationView(form, notConnected6010NoSession)(using sessionRequest, messages)

  "Check Your Answers Additional Information view" should {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(
      createView,
      "Further information",
      controllers.additionalinformation.routes.FurtherInformationOrRemarksController.show().url
    )

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()

      loginButton shouldBe messages("button.continue.label")
    }
  }
