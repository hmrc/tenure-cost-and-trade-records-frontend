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

package views.aboutYourLeaseOrTenure

import actions.SessionRequest
import form.aboutYourLeaseOrTenure.HowIsCurrentRentFixedForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.HowIsCurrentRentFixed
import models.submissions.aboutYourLeaseOrTenure.CurrentRentFixed.*
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import views.behaviours.QuestionViewBehaviours

class HowIsCurrentRentFixedViewSpec extends QuestionViewBehaviours[HowIsCurrentRentFixed] {

  val messageKeyPrefix = "howIsCurrentRentFixed"

  val sessionRequest6020full: SessionRequest[AnyContentAsEmpty.type] =
    SessionRequest(prefilledFull6020Session, fakeRequest)

  override val form = HowIsCurrentRentFixedForm.howIsCurrentRentFixedForm(using messages)

  val backLink = controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController.show().url

  def createView     = () => howIsCurrentRentFixedView(form, backLink, Summary("99996010001"))(using fakeRequest, messages)
  def createView6020 = () =>
    howIsCurrentRentFixedView(form, backLink, Summary("99996020001"))(using sessionRequest6020full, messages)

  def createViewUsingForm = (form: Form[HowIsCurrentRentFixed]) =>
    howIsCurrentRentFixedView(form, backLink, Summary("99996010001"))(using fakeRequest, messages)

  "How is current rennt fixed view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to rent payable by gross or net turnover Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController
        .show()
        .url
    }

    "has a link marked with back.link.label leading to rent payable by gross or net turnover Page123" in {
      val doc          = asDocument(createView6020())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController
        .show()
        .url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for how is rent fixed with the value agreement" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "howIsCurrentRentFixed",
        "howIsCurrentRentFixed",
        CurrentRentFixedNewLeaseAgreement.toString,
        false
      )
      assertContainsText(doc, messages("label.newLeaseAgreement"))
    }

    "contain radio buttons for how is rent fixed with the value interim" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "howIsCurrentRentFixed-2",
        "howIsCurrentRentFixed",
        CurrentRentFixedInterimRent.toString,
        false
      )
      assertContainsText(doc, messages("label.interimRent"))
    }

    "contain radio buttons for how is rent fixed with the value rent review" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "howIsCurrentRentFixed-3",
        "howIsCurrentRentFixed",
        CurrentRentFixedRentReview.toString,
        false
      )
      assertContainsText(doc, messages("label.rentReview"))
    }

    "contain radio buttons for how is rent fixed with the value renewal" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "howIsCurrentRentFixed-4",
        "howIsCurrentRentFixed",
        CurrentRentFixedRenewalLeaseTenancy.toString,
        false
      )
      assertContainsText(doc, messages("label.renewalLeaseTenancy"))
    }

    "contain radio buttons for how is rent fixed with the value sale lease back" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "howIsCurrentRentFixed-5",
        "howIsCurrentRentFixed",
        CurrentRentFixedSaleLeaseback.toString,
        false
      )
      assertContainsText(doc, messages("label.saleLeaseback"))
    }

    "contain date field for the value rentActuallyAgreed.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "rentActuallyAgreed.month", "Month")
      assertContainsText(doc, "rentActuallyAgreed.month")
    }

    "contain date field for the value rentActuallyAgreed.year" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "rentActuallyAgreed.year", "Year")
      assertContainsText(doc, "rentActuallyAgreed.year")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain get help section" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("help.rentActuallyAgreed.title")))
      assert(doc.toString.contains(messages("help.rentActuallyAgreed.p1")))
      assert(doc.toString.contains(messages("help.rentActuallyAgreed.p2")))
    }
  }
}
