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

import form.aboutYourLeaseOrTenure.TradeServiceDescriptionForm
import models.pages.Summary
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class TradeServicesDescriptionViewSpec extends QuestionViewBehaviours[String]:

  private val messageKeyPrefix = "tradeServiceDescription"

  override val form: Form[String] = TradeServiceDescriptionForm.tradeServicesDescriptionForm

  private val backLink = controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url

  private def createView = () =>
    tradeServicesDescriptionView(form, None, backLink, Summary("99996010001"))(using fakeRequest, messages)

  private def createViewUsingForm = (form: Form[String]) =>
    tradeServicesDescriptionView(form, None, backLink, Summary("99996010001"))(using fakeRequest, messages)

  "Trade services description view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to does the rent include any trade services Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
    }

    "Section heading is visible" in {
      val doc  = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val html = doc.getElementsByClass("govuk-caption-m").html()
      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.aboutYourLeaseOrTenure")}"""
    }

    "contain input for the service description" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "description")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()
      assert(loginButton == messages("button.continue.label"))
    }
  }
