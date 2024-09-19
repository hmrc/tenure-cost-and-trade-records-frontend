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
import form.aboutthetradinghistory.CheckYourAnswersAdditionalActivitiesForm
import models.submissions.common.AnswersYesNo

import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersAdditionalActivitiesViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "cYa.additionalActivities"

  override val form: Form[AnswersYesNo] =
    CheckYourAnswersAdditionalActivitiesForm.checkYourAnswersAdditionalActivitiesForm

  val backLink: String = controllers.aboutthetradinghistory.routes.AdditionalMiscController.show().url

  val sessionRequest: SessionRequest[AnyContentAsEmpty.type] =
    SessionRequest(aboutYourTradingHistory6045YesSession, fakeRequest)

  def createView: () => Html = () => checkYourAnswersAdditionalActivities(form, backLink)(sessionRequest, messages)

  def createViewUsingForm: Form[AnswersYesNo] => Html =
    (form: Form[AnswersYesNo]) => checkYourAnswersAdditionalActivities(form, backLink)(sessionRequest, messages)

  "Check Your Answers Additional Activities view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the additional misc Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.AdditionalMiscController.show().url
    }
    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-heading-l").text()
      assert(sectionText == messages("cYa.additionalActivities.heading"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
