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

package views.aboutYourLeaseOrTenure

import actions.SessionRequest
import form.aboutYourLeaseOrTenure.CheckYourAnswersAboutYourLeaseOrTenureForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.CheckYourAnswersAboutYourLeaseOrTenure
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersAboutYourLeaseOrTenureViewSpec
    extends QuestionViewBehaviours[CheckYourAnswersAboutYourLeaseOrTenure] {

  val messageKeyPrefix = "checkYourAnswersAboutYourLeaseOrTenure"

  override val form = CheckYourAnswersAboutYourLeaseOrTenureForm.checkYourAnswersAboutYourLeaseOrTenureForm

  val backLink = controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url

  val sessionRequest     = SessionRequest(baseFilled6010Session, fakeRequest)
  val sessionRequest6011 = SessionRequest(baseFilled6011Session, fakeRequest)

  val sessionRequest6030 = SessionRequest(prefilledFull6030Session, fakeRequest)

  val sessionRequest6020full: SessionRequest[AnyContentAsEmpty.type] =
    SessionRequest(prefilledFull6020Session, fakeRequest)

  def createView = () =>
    checkYourAnswersAboutLeaseAndTenureView(form, backLink, Summary("99996010001"))(sessionRequest, messages)

  def createView6011 = () =>
    checkYourAnswersAboutLeaseAndTenureView(form, backLink, Summary("99996020001"))(sessionRequest6011, messages)

  def createView6020 = () =>
    checkYourAnswersAboutLeaseAndTenureView(form, backLink, Summary("99996020001"))(sessionRequest6020full, messages)

  def createView6030      = () =>
    checkYourAnswersAboutLeaseAndTenureView(form, backLink, Summary("99996030001"))(sessionRequest6030, messages)
  def createViewUsingForm = (form: Form[CheckYourAnswersAboutYourLeaseOrTenure]) =>
    checkYourAnswersAboutLeaseAndTenureView(form, backLink, Summary("99996010001"))(sessionRequest, messages)

  "Check Your Answers About The Property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the website for property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url
    }

    "has a link marked with back.link.label leading to the website for property Page for 6011" in {
      val doc          = asDocument(createView6011())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url
    }

    "has a link marked with back.link.label leading to the website for property Page for 6020" in {
      val doc          = asDocument(createView6020())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url
    }

    "has a link marked with back.link.label leading to the website for property Page for 6030" in {
      val doc          = asDocument(createView6030())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
