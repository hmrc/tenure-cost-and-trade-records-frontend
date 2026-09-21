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

package views.aboutYourLeaseOrTenure

import actions.SessionRequest
import form.aboutYourLeaseOrTenure.RentOpenMarketValueForm
import models.pages.Summary
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import play.api.data.Form
import play.api.mvc.AnyContent
import views.behaviours.QuestionViewBehaviours

class RentOpenMarketViewSpec extends QuestionViewBehaviours[AnswersYesNo]:

  private val messageKeyPrefix = "rentOpenMarketValue"

  override val form: Form[AnswersYesNo] = RentOpenMarketValueForm.rentOpenMarketValuesForm

  private val sessionRequest6020full: SessionRequest[AnyContent] = SessionRequest(prefilledFull6020Session, getRequest)

  private val backLink = controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url

  private def createView = () =>
    rentOpenMarketValueView(form, backLink, Summary("99996010001"))(using getRequest, messages)

  private def createView6020 = () =>
    rentOpenMarketValueView(form, backLink, Summary("99996020001"))(using sessionRequest6020full, messages)

  private def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    rentOpenMarketValueView(form, backLink, Summary("99996010001"))(using getRequest, messages)

  "Rent open market view" should {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView, "Rent Include Fixture And Fittings", backLink)

    behave like pageWithBackLink(createView6020, "Rent Include Fixture And Fittings 6020", backLink)

    "Section heading is visible" in {
      val doc  = asDocument(createViewUsingForm(form))
      val html = doc.getElementsByClass("govuk-caption-m").html()

      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.aboutYourLeaseOrTenure")}"""
    }

    "contain radio buttons for rent open market with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "rentOpenMarketValue",
        "rentOpenMarketValue",
        AnswerYes.toString,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for rent open market with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "rentOpenMarketValue-2",
        "rentOpenMarketValue",
        AnswerNo.toString,
        false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()

      loginButton shouldBe messages("button.continue.label")
    }
  }
