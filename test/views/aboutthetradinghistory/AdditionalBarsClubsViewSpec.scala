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
import form.aboutthetradinghistory.AdditionalBarsClubsForm
import models.submissions.aboutthetradinghistory.AdditionalBarsClubs
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class AdditionalBarsClubsViewSpec extends QuestionViewBehaviours[Seq[AdditionalBarsClubs]] {

  val years = Seq("2023", "2022", "2021")

  val messageKeyPrefix = "additionalBarsClubs"

  override val form = AdditionalBarsClubsForm.additionalBarsClubsForm(years)(using messages)

  val sessionRequest = SessionRequest(aboutYourTradingHistory6045YesSession, fakeRequest)

  val backLink = controllers.aboutthetradinghistory.routes.AdditionalCateringController.show().url

  def createView = () => additionalBarsClubsView(form, backLink)(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[AdditionalBarsClubs]]) =>
    additionalBarsClubsView(form, backLink)(using sessionRequest, messages)

  "Additional bars and clubs 6045 view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the additional catering Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.AdditionalCateringController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").first().text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contains paragraph details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("additionalBarsClubs.p")))
    }

    "contain an input for gross bar receipts" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalBarsClubs[0].grossBar")
      assertRenderedById(doc, "additionalBarsClubs[1].grossBar")
      assertRenderedById(doc, "additionalBarsClubs[2].grossBar")
    }

    "contain an input for cost of bar purchases" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalBarsClubs[0].costBar")
      assertRenderedById(doc, "additionalBarsClubs[1].costBar")
      assertRenderedById(doc, "additionalBarsClubs[2].costBar")
    }
    "contain an input for gross receipts from club membership" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalBarsClubs[0].grossClubMembership")
      assertRenderedById(doc, "additionalBarsClubs[1].grossClubMembership")
      assertRenderedById(doc, "additionalBarsClubs[2].grossClubMembership")
    }
    "contain an input for gross receipts from separate" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalBarsClubs[0].grossFromSeparate")
      assertRenderedById(doc, "additionalBarsClubs[1].grossFromSeparate")
      assertRenderedById(doc, "additionalBarsClubs[2].grossFromSeparate")
    }
    "contain an input for cost of entertainment" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalBarsClubs[0].costOfEntertainment")
      assertRenderedById(doc, "additionalBarsClubs[1].costOfEntertainment")
      assertRenderedById(doc, "additionalBarsClubs[2].costOfEntertainment")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
