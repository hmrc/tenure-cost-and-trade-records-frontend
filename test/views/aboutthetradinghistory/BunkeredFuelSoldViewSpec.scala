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
import form.aboutthetradinghistory.BunkeredFuelSoldForm
import models.pages.Summary
import models.submissions.aboutthetradinghistory.BunkeredFuelSold
import play.api.data.{Form, FormError}
import views.behaviours.QuestionViewBehaviours

class BunkeredFuelSoldViewSpec extends QuestionViewBehaviours[Seq[BunkeredFuelSold]] {

  val sessionRequest = SessionRequest(aboutYourTradingHistory6020YesSession, fakeRequest)

  val backLink = controllers.aboutthetradinghistory.routes.BunkeredFuelQuestionController.show().url

  val messageKeyPrefix = "bunkeredFuelSold"

  override val form = BunkeredFuelSoldForm.bunkeredFuelSoldForm(Seq(2023, 2022, 2021).map(_.toString))(using messages)
  def createView    = () => bunkeredFuelSoldView(form, backLink)(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[BunkeredFuelSold]]) =>
    bunkeredFuelSoldView(form, backLink)(using sessionRequest, messages)

  "Bunkered Fuel Costs view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to your bunkered fuel question Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.BunkeredFuelQuestionController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").first().text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "Page heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-heading-l").text()
      assert(sectionText == messages("bunkeredFuelSold.heading"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "BunkeredFuelSoldForm" should {

      "reject empty values" in {
        val form         = BunkeredFuelSoldForm.bunkeredFuelSoldForm(Seq("2022", "2021"))(using messages)
        val formData     = Map(
          "bunkeredFuelSold-0" -> "",
          "bunkeredFuelSold-1" -> "100.50"
        )
        val formWithData = form.bind(formData)

        formWithData.errors.size shouldBe 1
        formWithData.errors.head shouldBe FormError(
          "bunkeredFuelSold-0",
          messages("error.bunkeredFuelSold.required", 2022.toString)
        )
      }

      "reject non-numeric values" in {
        val form     = BunkeredFuelSoldForm.bunkeredFuelSoldForm(Seq("2022"))(using messages)
        val formData = Map(
          "bunkeredFuelSold-0" -> "abc"
        )

        val formWithData = form.bind(formData)

        formWithData.errors.size shouldBe 1
        formWithData.errors.head shouldBe FormError(
          "bunkeredFuelSold-0",
          messages("error.bunkeredFuelSold.range", 2022.toString)
        )
      }
    }
  }
}
