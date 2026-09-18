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
import form.aboutthetradinghistory.UnusualCircumstancesForm
import models.ForType.*
import models.pages.Summary
import models.submissions.aboutthetradinghistory.UnusualCircumstances
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class UnusualCircumstancesViewSpec extends QuestionViewBehaviours[UnusualCircumstances]:

  private val messageKeyPrefix     = "unusualCircumstances"
  private val messageKeyPrefix6030 = "unusualCircumstancesReceipts"
  private val backLink             = controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController.show().url
  private val sessionRequest       = SessionRequest(baseFilled6030Session, getRequest)

  override val form: Form[UnusualCircumstances] = UnusualCircumstancesForm.unusualCircumstancesForm

  private def createView = () =>
    unusualCircumstancesView(form, FOR6010, backLink, Summary("99996010001"))(using sessionRequest, messages)

  private def createViewUsingForm = (form: Form[UnusualCircumstances]) =>
    unusualCircumstancesView(form, FOR6010, backLink, Summary("99996010001"))(using sessionRequest, messages)

  private def createView6030 = () =>
    unusualCircumstancesView(form, FOR6030, backLink, Summary("99996010001"))(using sessionRequest, messages)

  private def createViewUsingForm6030 = (form: Form[UnusualCircumstances]) =>
    unusualCircumstancesView(form, FOR6030, backLink, Summary("99996010001"))(using sessionRequest, messages)

  "Unusual Circumstances view" should {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView, "Income Expenditure Summary", backLink)

    "Section heading is visible" in {
      val doc  = asDocument(createViewUsingForm(form))
      val html = doc.getElementsByClass("govuk-caption-m").html()

      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.aboutYourTradingHistory")}"""
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()

      loginButton shouldBe messages("button.continue.label")
    }

    "contain an input for unusualCircumstances" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "unusualCircumstances")
    }
  }

  "Unusual Circumstances 6030 view" should {

    behave like normalPage(createView6030, messageKeyPrefix6030)

    behave like pageWithBackLink(createView, "Income Expenditure Summary", backLink)

    "Section heading is visible" in {
      val doc  = asDocument(createViewUsingForm6030(form))
      val html = doc.getElementsByClass("govuk-caption-m").html()

      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.aboutYourTradingHistory")}"""
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm6030(form))
      val loginButton = doc.getElementById("continue-button").text()

      loginButton shouldBe messages("button.continue.label")
    }

    "contain an input for unusualCircumstances" in {
      val doc = asDocument(createViewUsingForm6030(form))
      assertRenderedById(doc, "unusualCircumstances")
    }
  }
