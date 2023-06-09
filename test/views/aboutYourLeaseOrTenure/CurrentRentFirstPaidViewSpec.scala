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

import form.aboutYourLeaseOrTenure.CurrentRentFirstPaidForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.CurrentRentFirstPaid
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CurrentRentFirstPaidViewSpec extends QuestionViewBehaviours[CurrentRentFirstPaid] {

  val messageKeyPrefix = "currentRentFirstPaid"

  override val form = CurrentRentFirstPaidForm.currentRentFirstPaidForm

  def createView = () => currentRentFirstPaidView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[CurrentRentFirstPaid]) =>
    currentRentFirstPaidView(form, Summary("99996010001"))(fakeRequest, messages)

  "Current rent first paid view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show.url
    }

    "contain an subhead for page" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(doc, messages("label.currentRentFirstPaid"))
    }

    "contain date format hint for currentRentFirstPaid-hint" in {
      val doc             = asDocument(createViewUsingForm(form))
      val firstOccupyHint = doc.getElementById("currentRentFirstPaid-hint").text()
      assert(firstOccupyHint == messages("label.currentRentFirstPaid.help"))
    }

    "contain date field for the value currentRentFirstPaid.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "currentRentFirstPaid.month", "Month")
      assertContainsText(doc, "currentRentFirstPaid.month")
    }

    "contain date field for the value currentRentFirstPaid.year" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "currentRentFirstPaid.year", "Year")
      assertContainsText(doc, "currentRentFirstPaid.year")
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
