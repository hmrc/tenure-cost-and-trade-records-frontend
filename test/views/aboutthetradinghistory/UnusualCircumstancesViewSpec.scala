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
import form.aboutthetradinghistory.UnusualCircumstancesForm
import models.ForType.*
import models.pages.Summary
import models.submissions.aboutthetradinghistory.UnusualCircumstances
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class UnusualCircumstancesViewSpec extends QuestionViewBehaviours[UnusualCircumstances] {
  // NOTE: this is a holding view test until the other costs page is implemented
  val messageKeyPrefix       = "unusualCircumstances"
  val messageKeyPrefix6030   = "unusualCircumstancesReceipts"
  val backLink               = controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController.show().url
  private val sessionRequest = SessionRequest(baseFilled6030Session, fakeRequest)

  override val form = UnusualCircumstancesForm.unusualCircumstancesForm

  def createView = () =>
    unusualCircumstancesView(form, FOR6010, backLink, Summary("99996010001"))(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[UnusualCircumstances]) =>
    unusualCircumstancesView(form, FOR6010, backLink, Summary("99996010001"))(using sessionRequest, messages)

  def createView6030 = () =>
    unusualCircumstancesView(form, FOR6030, backLink, Summary("99996010001"))(using sessionRequest, messages)

  def createViewUsingForm6030 = (form: Form[UnusualCircumstances]) =>
    unusualCircumstancesView(form, FOR6030, backLink, Summary("99996010001"))(using sessionRequest, messages)

  "Unusual Circumstances view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController.show().url
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
    "contain an input for unusualCircumstances" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "unusualCircumstances")
    }
  }

  "Unusual Circumstances 6030 view" must {

    behave like normalPage(createView6030, messageKeyPrefix6030)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView6030())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm6030(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm6030(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
    "contain an input for unusualCircumstances" in {
      val doc = asDocument(createViewUsingForm6030(form))
      assertRenderedById(doc, "unusualCircumstances")
    }
  }
}
