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

import form.Form6010.HowIsCurrentRentFixedForm
import models.submissions.Form6010._
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class HowIsCurrentRentFixedViewSpec extends QuestionViewBehaviours[HowIsCurrentRentFixed] {

  val messageKeyPrefix = "howIsCurrentRentFixed"

  override val form = HowIsCurrentRentFixedForm.howIsCurrentRentFixedForm

  val backLink = controllers.Form6010.routes.RentPayableVaryAccordingToGrossOrNetController.show().url

  def createView = () => howIsCurrentRentFixedView(form, backLink)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[HowIsCurrentRentFixed]) =>
    howIsCurrentRentFixedView(form, backLink)(fakeRequest, messages)

  "How is current rennt fixed view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to rent payable by gross or net turnover Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.Form6010.routes.RentPayableVaryAccordingToGrossOrNetController.show.url
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
        CurrentRentFixedNewLeaseAgreement.name,
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
        CurrentRentFixedInterimRent.name,
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
        CurrentRentFixedRentReview.name,
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
        CurrentRentFixedRenewalLeaseTenancy.name,
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
        CurrentRentFixedSaleLeaseback.name,
        false
      )
      assertContainsText(doc, messages("label.saleLeaseback"))
    }

    "contain date format hint for rentActuallyAgreed-hint" in {
      val doc             = asDocument(createViewUsingForm(form))
      val firstOccupyHint = doc.getElementById("rentActuallyAgreed-hint").text()
      assert(firstOccupyHint == messages("help.date.day.month.year"))
    }

    "contain date field for the value rentActuallyAgreed.day" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "rentActuallyAgreed.day", "Day")
      assertContainsText(doc, "rentActuallyAgreed.day")
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

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
