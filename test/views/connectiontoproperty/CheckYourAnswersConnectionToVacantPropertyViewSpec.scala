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

package views.connectiontoproperty

import actions.SessionRequest
import form.connectiontoproperty.CheckYourAnswersConnectionToVacantPropertyForm
import models.submissions.connectiontoproperty.CheckYourAnswersConnectionToVacantProperty
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersConnectionToVacantPropertyViewSpec
    extends QuestionViewBehaviours[CheckYourAnswersConnectionToVacantProperty] {

  val messageKeyPrefix = "checkYourAnswersConnectionToVacantProperty"

  override val form: Form[CheckYourAnswersConnectionToVacantProperty] =
    CheckYourAnswersConnectionToVacantPropertyForm.checkYourAnswersConnectionToVacantPropertyForm

  val backLink: String = controllers.connectiontoproperty.routes.ProvideContactDetailsController.show().url

  val sessionRequest: SessionRequest[AnyContentAsEmpty.type] =
    SessionRequest(stillConnectedDetailsYesToAllSession, fakeRequest)

  def createView: () => Html = () => checkYourAnswersConnectionToVacantProperty(backLink)(sessionRequest, messages)

  def createViewUsingForm: Form[CheckYourAnswersConnectionToVacantProperty] => Html =
    (form: Form[CheckYourAnswersConnectionToVacantProperty]) =>
      checkYourAnswersConnectionToVacantProperty(backLink)(sessionRequest, messages)

  "Check Your Answers Connection To Vacant Property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the website for property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.connectiontoproperty.routes.ProvideContactDetailsController.show().url
    }

    "heading text" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("declaration.heading")))
    }

    "paragraph text" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("declaration.information")))
    }

    "contain save and continue button with the value Send" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.send"))
    }
  }
}
