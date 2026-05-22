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

package views.aboutFranchisesOrLettings

import actions.SessionRequest
import form.aboutfranchisesorlettings.CheckYourAnswersAboutFranchiseOrLettingsForm
import models.submissions.common.AnswersYesNo
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersAboutFranchiseOrLettingsViewSpec extends QuestionViewBehaviours[AnswersYesNo]:

  private def cyaFranchiseOrLettingsView = inject[views.html.aboutfranchisesorlettings.checkYourAnswersAboutFranchiseOrLettings]

  private val messageKeyPrefix = "checkYourAnswersAboutFranchiseOrLettings"

  override val form: Form[AnswersYesNo] = CheckYourAnswersAboutFranchiseOrLettingsForm.theForm

  private val backLink = controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url

  private val sessionRequest = SessionRequest(baseFilled6010Session, fakeRequest)

  private def createView = () => cyaFranchiseOrLettingsView(form, backLink)(using sessionRequest, messages)

  private def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    cyaFranchiseOrLettingsView(form, backLink)(using sessionRequest, messages)

  private def createView6045 = () => cyaFranchiseOrLettingsView(form, backLink)(using sessionRequest, messages)

  "Check Your Answers About Franchise Or Lettings view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the check your answers franchise or lettings Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()
      assert(loginButton == messages("button.continue.label"))
    }
  }

  "Check Your Answers About The Property view for 6045" should {
    behave like normalPage(createView6045, messageKeyPrefix)
  }
