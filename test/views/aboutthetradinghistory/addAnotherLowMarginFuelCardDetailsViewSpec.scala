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

package views.aboutthetradinghistory

import actions.SessionRequest
import form.aboutthetradinghistory.AddAnotherLowMarginFuelCardsDetailsForm.theForm
import models.pages.Summary
import models.submissions.common.AnswersYesNo
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class addAnotherLowMarginFuelCardDetailsViewSpec extends QuestionViewBehaviours[AnswersYesNo] {
  val messageKeyPrefix                  = "addAnotherLowMarginFuelCardDetails"
  val sessionRequest                    = SessionRequest(baseFilled6010Session, fakeRequest)
  override val form: Form[AnswersYesNo] = theForm

  def createView: () => Html = () =>
    addAnotherLowMarginFuelCardsDetailsView(
      form,
      0
    )(sessionRequest, messages)

  "Catering add another bunker fuel cards details view" must {
    behave like normalPageWithZeroDetails(createView, messageKeyPrefix, "zeroDetails", "0")
  }
}
