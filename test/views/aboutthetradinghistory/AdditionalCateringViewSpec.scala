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
import form.aboutthetradinghistory.AdditionalCateringForm
import models.submissions.aboutthetradinghistory.AdditionalCatering
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class AdditionalCateringViewSpec extends QuestionViewBehaviours[Seq[AdditionalCatering]] {

  val years = Seq("2023", "2022", "2021")

  val messageKeyPrefix = "catering.additionalActivitiesOnSite"

  override val form = AdditionalCateringForm.additionalCateringForm(years)(messages)

  val sessionRequest = SessionRequest(aboutYourTradingHistory6045YesSession, fakeRequest)

  val backLink = controllers.aboutthetradinghistory.routes.AdditionalShopsController.show().url

  def createView = () => additionalCateringView(form, backLink)(sessionRequest, messages)

  def createViewUsingForm = (form: Form[Seq[AdditionalCatering]]) =>
    additionalCateringView(form, backLink)(sessionRequest, messages)

  "Additional bars and clubs 6045 view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the additional shops Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.AdditionalShopsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").first().text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contains paragraphs details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("catering.additionalActivitiesOnSite.p1")))
      assert(doc.toString.contains(messages("catering.additionalActivitiesOnSite.p2")))
    }

    s"contain an input for gross receipts" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalCatering[0].grossReceipts")
      assertRenderedById(doc, "additionalCatering[1].grossReceipts")
      assertRenderedById(doc, "additionalCatering[2].grossReceipts")
    }

    s"contain an input for cost of purchase" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalCatering[0].costOfPurchase")
      assertRenderedById(doc, "additionalCatering[1].costOfPurchase")
      assertRenderedById(doc, "additionalCatering[2].costOfPurchase")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
