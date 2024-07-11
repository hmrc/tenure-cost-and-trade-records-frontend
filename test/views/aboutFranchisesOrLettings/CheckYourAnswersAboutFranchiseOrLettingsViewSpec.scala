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

package views.aboutFranchisesOrLettings

import actions.SessionRequest
import form.aboutfranchisesorlettings.CheckYourAnswersAboutFranchiseOrLettingsForm
import models.submissions.aboutfranchisesorlettings.CheckYourAnswersAboutFranchiseOrLettings
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours
import models.pages.Summary

class CheckYourAnswersAboutFranchiseOrLettingsViewSpec
    extends QuestionViewBehaviours[CheckYourAnswersAboutFranchiseOrLettings] {

  def cyaFranchiseOrLettingsView = inject[views.html.aboutfranchisesorlettings.checkYourAnswersAboutFranchiseOrLettings]

  val messageKeyPrefix = "checkYourAnswersAboutFranchiseOrLettings"

  override val form = CheckYourAnswersAboutFranchiseOrLettingsForm.checkYourAnswersAboutFranchiseOrLettingsForm

  val backLink = controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url

  val sessionRequest = SessionRequest(baseFilled6010Session, fakeRequest)

  def createView = () => cyaFranchiseOrLettingsView(form, backLink, Summary("99996010001"))(sessionRequest, messages)

  def createViewUsingForm = (form: Form[CheckYourAnswersAboutFranchiseOrLettings]) =>
    cyaFranchiseOrLettingsView(form, backLink, Summary("99996010001"))(sessionRequest, messages)

  "Check Your Answers About Franchise Or Lettings view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the check your answers franchise or lettings Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseLettings"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
