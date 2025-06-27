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

package views.aboutthetradinghistory

import actions.SessionRequest
import form.aboutthetradinghistory.CheckYourAnswersAboutTheTradingHistoryForm
import models.pages.Summary
import models.submissions.common.AnswersYesNo
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersAboutTheTradingHistorySpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "checkYourAnswersAboutTheTradingHistory"

  override val form: Form[AnswersYesNo] =
    CheckYourAnswersAboutTheTradingHistoryForm.checkYourAnswersAboutTheTradingHistoryForm

  val backLink: String = controllers.aboutthetradinghistory.routes.TurnoverController.show().url

  val backLink6020: String =
    controllers.aboutthetradinghistory.routes.ElectricVehicleChargingPointsController.show().url

  val sessionRequest: SessionRequest[AnyContentAsEmpty.type] =
    SessionRequest(aboutYourTradingHistory6015YesSession, fakeRequest)

  val sessionRequestFor6016: SessionRequest[AnyContentAsEmpty.type] =
    SessionRequest(aboutYourTradingHistory6016YesSession, fakeRequest)

  val sessionRequestFor6020: SessionRequest[AnyContentAsEmpty.type] =
    SessionRequest(aboutYourTradingHistory6020YesSession, fakeRequest)

  val sessionRequestFor6030: SessionRequest[AnyContentAsEmpty.type] =
    SessionRequest(aboutYourTradingHistory6030YesSession, fakeRequest)

  def createView: () => Html = () =>
    checkYourAnswersAboutTheTradingHistoryView(form, backLink, Summary("99996015001"))(using sessionRequest, messages)

  def createView6016: () => Html = () =>
    checkYourAnswersAboutTheTradingHistoryView(form, backLink, Summary("99996016001"))(using
      sessionRequestFor6016,
      messages
    )

  def createView6020: () => Html = () =>
    checkYourAnswersAboutTheTradingHistoryView(form, backLink6020, Summary("99996020001"))(using
      sessionRequestFor6020,
      messages
    )
  def createView6030: () => Html = () =>
    checkYourAnswersAboutTheTradingHistoryView(form, backLink, Summary("99996030001"))(using
      sessionRequestFor6030,
      messages
    )

  def createView6045: () => Html = () =>
    checkYourAnswersAboutTheTradingHistoryView(form, backLink, Summary("99996045001"))(using
      SessionRequest(aboutYourTradingHistory6045YesSession, fakeRequest),
      messages
    )

  def createView6076: () => Html = () =>
    checkYourAnswersAboutTheTradingHistoryView(form, backLink, Summary("99996076001"))(using
      SessionRequest(aboutYourTradingHistory6076YesSession, fakeRequest),
      messages
    )

  def createViewUsingForm: Form[AnswersYesNo] => Html =
    (form: Form[AnswersYesNo]) =>
      checkYourAnswersAboutTheTradingHistoryView(form, backLink, Summary("99996015001"))(using sessionRequest, messages)

  "Check Your Answers About The Property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the website for property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.TurnoverController.show().url
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }

  "Check Your Answers About The Property view for 6016" must {
    behave like normalPage(createView6016, messageKeyPrefix)
  }

  "Check Your Answers About The Property view for 6020" must {
    behave like normalPage(createView6020, messageKeyPrefix)
  }

  "Check Your Answers About The Property view for 6030" must {
    behave like normalPage(createView6030, messageKeyPrefix)
  }

  "Check Your Answers About The Property view for 6045" must {
    behave like normalPage(createView6045, "checkYourAnswersAboutTheTradingHistory.6045.caravans")
  }

  "Check Your Answers About The Property view for 6076" must {
    behave like normalPage(createView6076, messageKeyPrefix)
  }

}
