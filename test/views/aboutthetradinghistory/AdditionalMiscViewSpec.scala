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
import form.aboutthetradinghistory.AdditionalMiscForm
import models.submissions.aboutthetradinghistory.{AdditionalMisc, AdditionalMiscDetails}
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class AdditionalMiscViewSpec extends QuestionViewBehaviours[(Seq[AdditionalMisc], AdditionalMiscDetails)] {

  val years = Seq("2023", "2022", "2021")

  val messageKeyPrefix = "additionalMisc"

  override val form = AdditionalMiscForm.additionalMiscForm(years)(using messages)

  val sessionRequest = SessionRequest(aboutYourTradingHistory6045YesSession, fakeRequest)

  val backLink = controllers.aboutthetradinghistory.routes.AdditionalAmusementsController.show().url

  def createView = () => additionalMiscView(form, backLink)(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[(Seq[AdditionalMisc], AdditionalMiscDetails)]) =>
    additionalMiscView(form, backLink)(using sessionRequest, messages)

  "Additional misc view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the additional amusements Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutthetradinghistory.routes.AdditionalAmusementsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").first().text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contains paragraph details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("additionalMisc.p")))
    }

    s"contain an input for gross leisure Receipts" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalMisc.[0].leisureReceipts")
      assertRenderedById(doc, "additionalMisc.[1].leisureReceipts")
      assertRenderedById(doc, "additionalMisc.[2].leisureReceipts")
    }

    s"contain an input for gross leisure winter storage receipts" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalMisc.[0].winterStorageReceipts")
      assertRenderedById(doc, "additionalMisc.[1].winterStorageReceipts")
      assertRenderedById(doc, "additionalMisc.[2].winterStorageReceipts")
    }

    s"contain an input for gross other activities receipts" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalMisc.[0].otherActivitiesReceipts")
      assertRenderedById(doc, "additionalMisc.[1].otherActivitiesReceipts")
      assertRenderedById(doc, "additionalMisc.[2].otherActivitiesReceipts")
    }

    s"contain an input for number of vans" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalMisc.[0].numberOfVans")
      assertRenderedById(doc, "additionalMisc.[1].numberOfVans")
      assertRenderedById(doc, "additionalMisc.[2].numberOfVans")
    }

    s"contain an input for other services receipts" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalMisc.[0].otherServicesReceipts")
      assertRenderedById(doc, "additionalMisc.[1].otherServicesReceipts")
      assertRenderedById(doc, "additionalMisc.[2].otherServicesReceipts")
    }

    s"contain an input for bottled gas receipts" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "additionalMisc.[0].bottledGasReceipts")
      assertRenderedById(doc, "additionalMisc.[1].bottledGasReceipts")
      assertRenderedById(doc, "additionalMisc.[2].bottledGasReceipts")
    }

    s"contain an other activities receipts details" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "details.otherActivitiesReceiptsDetails")
    }

    s"contain an input for leisure receipts details" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "details.leisureReceiptsDetails")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
