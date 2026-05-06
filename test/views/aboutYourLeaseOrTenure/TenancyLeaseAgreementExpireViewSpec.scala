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

import form.aboutYourLeaseOrTenure.TenancyLeaseAgreementExpireForm
import models.pages.Summary
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

import java.time.LocalDate

class TenancyLeaseAgreementExpireViewSpec extends QuestionViewBehaviours[LocalDate]:

  private def tenancyLeaseAgreementExpireView = inject[views.html.aboutYourLeaseOrTenure.tenancyLeaseAgreementExpire]

  private val messageKeyPrefix = "tenancyLeaseAgreementExpire"

  override val form: Form[LocalDate] = TenancyLeaseAgreementExpireForm.tenancyLeaseAgreementExpireForm(using messages)

  private def createView = () =>
    tenancyLeaseAgreementExpireView(form, Summary("99996010001"))(using fakeRequest, messages)

  private def createViewUsingForm = (form: Form[LocalDate]) =>
    tenancyLeaseAgreementExpireView(form, Summary("99996010001"))(using fakeRequest, messages)

  "Tenancy lease agreement expire view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the current rent paid Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show().url
    }

    "Section heading is visible" in {
      val doc  = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val html = doc.getElementsByClass("govuk-caption-m").html()
      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.aboutYourLeaseOrTenure")}"""
    }

    "contain date format hint for tenancyLeaseAgreementExpire-hint" in {
      val doc             = asDocument(createViewUsingForm(form))
      val firstOccupyHint = doc.getElementById("tenancyLeaseAgreementExpire-hint").text()
      assert(firstOccupyHint == messages("hint.date.example"))
    }

    "contain date field for the value tenancyLeaseAgreementExpire.day" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "tenancyLeaseAgreementExpire.day", "Day")
      assertContainsText(doc, "tenancyLeaseAgreementExpire.day")
    }

    "contain date field for the value tenancyLeaseAgreementExpire.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "tenancyLeaseAgreementExpire.month", "Month")
      assertContainsText(doc, "tenancyLeaseAgreementExpire.month")
    }

    "contain date field for the value tenancyLeaseAgreementExpire.year" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "tenancyLeaseAgreementExpire.year", "Year")
      assertContainsText(doc, "tenancyLeaseAgreementExpire.year")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()
      assert(loginButton == messages("button.continue.label"))
    }
  }
