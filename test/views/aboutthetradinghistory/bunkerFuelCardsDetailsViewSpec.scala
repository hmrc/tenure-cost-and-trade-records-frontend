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

import models.submissions.aboutthetradinghistory.BunkerFuelCardDetails
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours
import form.aboutthetradinghistory.BunkerFuelCardDetailsForm.bunkerFuelCardDetailsForm
import models.pages.Summary

class bunkerFuelCardsDetailsViewSpec extends QuestionViewBehaviours[BunkerFuelCardDetails]{
  override val form: Form[BunkerFuelCardDetails] = bunkerFuelCardDetailsForm
  val messageKeyPrefix     = "bunkerFuelCardDetails"
  def createView = () =>
    bunkerFuelCardDetailsView(
      form,None, controllers.aboutthetradinghistory.routes.BunkeredFuelQuestionController.show().url,Summary("99996010001")
    )(fakeRequest, messages)
  "Catering bunker fuel cards details view" must {
    behave like normalPage(createView,messageKeyPrefix)
  }
}
