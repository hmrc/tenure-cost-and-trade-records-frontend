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

package views.connectiontoproperty

import actions.SessionRequest
import form.CheckYourAnswersAndConfirmForm
import models.pages.Summary
import models.submissions.common.CheckYourAnswersAndConfirm
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersConnectionToPropertyViewSpec extends QuestionViewBehaviours[CheckYourAnswersAndConfirm] {

  val messageKeyPrefix = "checkYourAnswersConnectionToProperty"

  override val form: Form[CheckYourAnswersAndConfirm] =
    CheckYourAnswersAndConfirmForm.theForm

  val backLink: String = controllers.connectiontoproperty.routes.AreYouThirdPartyController.show().url

  val sessionRequest: SessionRequest[AnyContentAsEmpty.type] = SessionRequest(baseFilled6010Session, fakeRequest)

  def createView: () => Html = () =>
    checkYourAnswersConnectionToProperty(form, backLink, Summary("99996010001"))(using sessionRequest, messages)

  def createViewUsingForm: Form[CheckYourAnswersAndConfirm] => Html =
    (form: Form[CheckYourAnswersAndConfirm]) =>
      checkYourAnswersConnectionToProperty(form, backLink, Summary("99996010001"))(using sessionRequest, messages)

  "Check Your Answers Connection To Property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the website for property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.connectiontoproperty.routes.AreYouThirdPartyController.show().url
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
