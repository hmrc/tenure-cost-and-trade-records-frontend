/*
 * Copyright 2024 HM Revenue & Customs
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
import form.aboutthetradinghistory.TotalFuelSoldForm
import models.pages.Summary
import models.submissions.aboutthetradinghistory.TotalFuelSold
import play.api.data.{Form, FormError}
import views.behaviours.QuestionViewBehaviours

class TotalFuelSoldViewSpec extends QuestionViewBehaviours[Seq[TotalFuelSold]] {

  val sessionRequest = SessionRequest(aboutYourTradingHistory6020YesSession, fakeRequest)

  val backLink = controllers.aboutthetradinghistory.routes.FinancialYearEndController.show().url

  val messageKeyPrefix = "totalFuelSold"

  override val form = TotalFuelSoldForm.totalFuelSoldForm(Seq(2025, 2024, 2023).map(_.toString))(messages)
  def createView    = () => totalFuelSoldView(form, backLink, Summary("99996010001"))(sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[TotalFuelSold]]) =>
    totalFuelSoldView(form, backLink, Summary("99996020001"))(sessionRequest, messages)

  "Total Fuel Costs view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to your financial year end Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.FinancialYearEndController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "Page heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-heading-l").text()
      assert(sectionText == messages("totalFuelSold.heading"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "TotalFuelSoldForm" should {
      "reject empty values" in {
        val form         = TotalFuelSoldForm.totalFuelSoldForm(Seq("2022", "2021"))(messages)
        val formData     = Map(
          "totalFuelSold-0" -> "",
          "totalFuelSold-1" -> "100.50"
        )
        val formWithData = form.bind(formData)

        formWithData.errors.size shouldBe 1
        formWithData.errors.head shouldBe FormError(
          "totalFuelSold-0",
          messages("error.totalFuelSold.required", 2022.toString)
        )
      }

      "reject non-numeric values" in {
        val form     = TotalFuelSoldForm.totalFuelSoldForm(Seq("2022"))(messages)
        val formData = Map(
          "totalFuelSold-0" -> "abc"
        )

        val formWithData = form.bind(formData)

        formWithData.errors.size shouldBe 1
        formWithData.errors.head shouldBe FormError(
          "totalFuelSold-0",
          messages("error.totalFuelSold.range", 2022.toString)
        )
      }
    }
  }
}
