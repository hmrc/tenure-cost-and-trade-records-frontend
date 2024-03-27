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

package views.aboutthetradinghistory

import actions.SessionRequest
import form.aboutthetradinghistory.CheckYourAnswersAboutTheTradingHistoryForm
import models.pages.Summary
import models.submissions.aboutthetradinghistory.CheckYourAnswersAboutTheTradingHistory
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersAboutTheTradingHistorySpec
    extends QuestionViewBehaviours[CheckYourAnswersAboutTheTradingHistory] {

  val messageKeyPrefix = "checkYourAnswersAboutTheTradingHistory"

  override val form: Form[CheckYourAnswersAboutTheTradingHistory] =
    CheckYourAnswersAboutTheTradingHistoryForm.checkYourAnswersAboutTheTradingHistoryForm

  val backLink: String = controllers.aboutthetradinghistory.routes.TurnoverController.show().url

  val sessionRequest: SessionRequest[AnyContentAsEmpty.type] =
    SessionRequest(aboutYourTradingHistory6015YesSession, fakeRequest)

  val sessionRequestFor6020: SessionRequest[AnyContentAsEmpty.type] =
    SessionRequest(aboutYourTradingHistory6020YesSession, fakeRequest)
  def createView: () => Html                                        = () =>
    checkYourAnswersAboutTheTradingHistoryView(form, backLink, Summary("99996015001"))(sessionRequest, messages)

  def createView6020: () => Html = () =>
    checkYourAnswersAboutTheTradingHistoryView(form, backLink, Summary("99996020001"))(sessionRequestFor6020, messages)

  def createViewUsingForm: Form[CheckYourAnswersAboutTheTradingHistory] => Html =
    (form: Form[CheckYourAnswersAboutTheTradingHistory]) =>
      checkYourAnswersAboutTheTradingHistoryView(form, backLink, Summary("99996015001"))(sessionRequest, messages)

  "Check Your Answers About The Property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the website for property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutthetradinghistory.routes.TurnoverController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
  "Check Your Answers About The Property view for 6020" must {
    behave like normalPage(createView6020, messageKeyPrefix)
  }
}
