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

package views.aboutthetradinghistory

import actions.SessionRequest
import form.aboutthetradinghistory.CheckYourAnswersTentingPitchesForm
import models.submissions.common.AnswersYesNo
import play.api.data.Form
import play.api.mvc.AnyContent
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersTentingPitchesViewSpec extends QuestionViewBehaviours[AnswersYesNo]:

  private val messageKeyPrefix = "cYa.touringAndTentingPitches"

  override val form: Form[AnswersYesNo] = CheckYourAnswersTentingPitchesForm.checkYourAnswersTentingPitchesForm

  private val backLink: String = controllers.aboutthetradinghistory.routes.TentingPitchesCertificatedController.show().url

  private val sessionRequest: SessionRequest[AnyContent] = SessionRequest(aboutYourTradingHistory6045YesSession, fakeRequest)

  private def createView: () => Html = () => checkYourAnswersTentingPitches(form, backLink)(using sessionRequest, messages)

  private def createViewUsingForm: Form[AnswersYesNo] => Html =
    form => checkYourAnswersTentingPitches(form, backLink)(using sessionRequest, messages)

  "Check Your Answers Additional Activities view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the Tenting Pitches Certificated Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.TentingPitchesCertificatedController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-heading-l").text()
      assert(sectionText == messages("cYa.touringAndTentingPitches.heading"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()
      assert(loginButton == messages("button.continue.label"))
    }
  }
