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

import form.aboutYourLeaseOrTenure.{CurrentLeaseOrAgreementBeginForm, CurrentRentFirstPaidForm}
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.{CurrentLeaseOrAgreementBegin, CurrentRentFirstPaid}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CurrentLeaseOrAgreementBeginViewSpec extends QuestionViewBehaviours[CurrentLeaseOrAgreementBegin] {

  val messageKeyPrefix = "currentLeaseOrAgreementBegin"

  override val form = CurrentLeaseOrAgreementBeginForm.currentLeaseOrAgreementBeginForm(messages)

  def createView = () => currentLeaseOrAgreementBeginView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[CurrentLeaseOrAgreementBegin]) =>
    currentLeaseOrAgreementBeginView(form, Summary("99996010001"))(fakeRequest, messages)

  "Current rent first paid view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show.url
    }

    "contain an subhead for page" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(doc, messages("label.currentLeaseOrAgreementBegin"))
    }

    "contain date field for the value leaseBegin.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "leaseBegin.month", "Month")
      assertContainsText(doc, "leaseBegin.month")
    }

    "contain date field for the value leaseBegin.year" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "leaseBegin.year", "Year")
      assertContainsText(doc, "leaseBegin.year")
    }

    "contain text for the value grantedFor" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "grantedFor", "How long was it granted for?")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
