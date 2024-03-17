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

package views.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.TradeServiceDescriptionForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.TradeServicesDetails
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class TradeServicesDescriptionViewSpec extends QuestionViewBehaviours[TradeServicesDetails] {

  val messageKeyPrefix = "tradeServiceDescription"

  override val form = TradeServiceDescriptionForm.tradeServicesDescriptionForm

  val backLink = controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url

  def createView = () =>
    tradeServicesDescriptionView(form, None, backLink, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[TradeServicesDetails]) =>
    tradeServicesDescriptionView(form, None, backLink, Summary("99996010001"))(fakeRequest, messages)

  "Trade services description view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to does the rent include any trade services Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain input for the service description" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "description")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
