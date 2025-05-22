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
import form.aboutthetradinghistory.PercentageFromFuelCardsForm
import models.pages.Summary
import models.submissions.aboutthetradinghistory.PercentageFromFuelCards
import play.api.data.{Form, FormError}
import views.behaviours.QuestionViewBehaviours

class PercentageFromFuelCardsViewSpec extends QuestionViewBehaviours[Seq[PercentageFromFuelCards]] {

  val sessionRequest = SessionRequest(aboutYourTradingHistory6020YesSession, fakeRequest)

  val messageKeyPrefix = "percentageFromFuelCards"

  val backLink = controllers.aboutthetradinghistory.routes.CustomerCreditAccountsController.show().url

  override val form =
    PercentageFromFuelCardsForm.percentageFromFuelCardsForm(Seq(2025, 2024, 2023).map(_.toString))(using messages)
  def createView    = () =>
    percentageFromFuelCardsView(form, backLink, Summary("99996010001"))(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[PercentageFromFuelCards]]) =>
    percentageFromFuelCardsView(form, backLink, Summary("99996020001"))(using sessionRequest, messages)

  "Percentage from fuel cards view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to customer credit accounts Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.CustomerCreditAccountsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "Page heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-heading-l").text()
      assert(sectionText == messages("percentageFromFuelCards.heading"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "Percentage from fuel form" should {
      "reject empty values" in {
        val form         = PercentageFromFuelCardsForm.percentageFromFuelCardsForm(Seq("2022", "2021"))(using messages)
        val formData     = Map(
          "percentageFromFuelCards-0" -> "",
          "percentageFromFuelCards-1" -> "100"
        )
        val formWithData = form.bind(formData)

        formWithData.errors.size shouldBe 1
        formWithData.errors.head shouldBe FormError(
          "percentageFromFuelCards-0",
          messages("error.percentageFromFuelCards.required", 2022.toString)
        )
      }

      "reject non-numeric values" in {
        val form     = PercentageFromFuelCardsForm.percentageFromFuelCardsForm(Seq("2022"))(using messages)
        val formData = Map(
          "percentageFromFuelCards-0" -> "abc"
        )

        val formWithData = form.bind(formData)

        formWithData.errors.size shouldBe 1
        formWithData.errors.head shouldBe FormError(
          "percentageFromFuelCards-0",
          messages("error.percentageFromFuelCards.range", 2022.toString)
        )
      }

      "reject  values greater than 100 " in {
        val form     = PercentageFromFuelCardsForm.percentageFromFuelCardsForm(Seq("2022"))(using messages)
        val formData = Map(
          "percentageFromFuelCards-0" -> "111"
        )

        val formWithData = form.bind(formData)

        formWithData.errors.size shouldBe 1
        formWithData.errors.head shouldBe FormError(
          "percentageFromFuelCards-0",
          messages("error.percentage", 2022.toString)
        )
      }
    }
  }
}
