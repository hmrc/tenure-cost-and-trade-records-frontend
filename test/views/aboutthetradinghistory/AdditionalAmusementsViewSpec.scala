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
import form.aboutthetradinghistory.AdditionalAmusementsForm
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class AdditionalAmusementsViewSpec extends QuestionViewBehaviours[Seq[Option[BigDecimal]]] {

  val years = Seq("2023", "2022", "2021")

  val messageKeyPrefix = "additionalAmusements"

  override val form = AdditionalAmusementsForm.additionalAmusementsForm(years)(using messages)

  val sessionRequest = SessionRequest(aboutYourTradingHistory6045YesSession, fakeRequest)

  val backLink = controllers.aboutthetradinghistory.routes.AdditionalBarsClubsController.show().url

  def createView = () => additionalAmusementsView(form, backLink)(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[Option[BigDecimal]]]) =>
    additionalAmusementsView(form, backLink)(using sessionRequest, messages)

  "Additional amusements 6045 view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the additional bars and clubs Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.AdditionalBarsClubsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").first().text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contains paragraph" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("additionalAmusements.p")))
    }

    s"contain an input for gross receipts" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalAmusements[0].receipts")
      assertRenderedById(doc, "additionalAmusements[1].receipts")
      assertRenderedById(doc, "additionalAmusements[2].receipts")
    }
  }
}
