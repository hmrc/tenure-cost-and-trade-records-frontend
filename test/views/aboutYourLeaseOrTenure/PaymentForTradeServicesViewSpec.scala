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

import form.aboutYourLeaseOrTenure.PaymentForTradeServicesForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.PaymentForTradeServices
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class PaymentForTradeServicesViewSpec extends QuestionViewBehaviours[PaymentForTradeServices] {

  val messageKeyPrefix = "paymentForTradeServices"

  override val form = PaymentForTradeServicesForm.paymentForTradeServicesForm

  val backLink   = controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(0).url
  def createView = () => paymentForTradeServicesView(form, backLink, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[PaymentForTradeServices]) =>
    paymentForTradeServicesView(form, backLink, Summary("99996010001"))(fakeRequest, messages)

  "Payment for trade services view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the list of services Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe backLink
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "paymentForTradeServices",
        "paymentForTradeServices",
        "yes",
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons  with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "paymentForTradeServices-2",
        "paymentForTradeServices",
        "no",
        false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
