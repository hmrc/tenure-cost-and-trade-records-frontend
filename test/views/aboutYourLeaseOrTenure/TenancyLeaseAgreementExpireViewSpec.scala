/*
 * Copyright 2023 HM Revenue & Customs
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
import models.submissions.aboutYourLeaseOrTenure.TenancyLeaseAgreementExpire
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class TenancyLeaseAgreementExpireViewSpec extends QuestionViewBehaviours[TenancyLeaseAgreementExpire] {

  def tenancyLeaseAgreementExpireView =
    app.injector.instanceOf[views.html.aboutYourLeaseOrTenure.tenancyLeaseAgreementExpire]

  val messageKeyPrefix = "tenancyLeaseAgreementExpire"

  override val form = TenancyLeaseAgreementExpireForm.tenancyLeaseAgreementExpireForm(messages)

  def createView = () => tenancyLeaseAgreementExpireView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[TenancyLeaseAgreementExpire]) =>
    tenancyLeaseAgreementExpireView(form, Summary("99996010001"))(fakeRequest, messages)

  "Tenancy lease agreement expire view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the current rent paid Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show.url
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
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
