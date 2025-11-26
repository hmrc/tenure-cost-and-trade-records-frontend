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
import form.aboutYourLeaseOrTenure.IntervalsOfRentReviewForm
import models.submissions.aboutYourLeaseOrTenure.IntervalsOfRentReview
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class IntervalsOfRentReviewViewSpec extends QuestionViewBehaviours[IntervalsOfRentReview] {

  val messageKeyPrefix = "intervalsOfRentReview"

  override val form = IntervalsOfRentReviewForm.intervalsOfRentReviewForm(using messages)

  val sessionRequest = SessionRequest(stillConnectedDetails6011YesSession, fakeRequest)

  def createView = () => intervalsOfRentReviewView(form)(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[IntervalsOfRentReview]) =>
    intervalsOfRentReviewView(form)(using sessionRequest, messages)

  "Intervals of rent reviews view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to method to fix rent Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.IsRentReviewPlannedController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain an input for text char count hint" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "intervalsOfRentReview-hint")
    }

    "contain an input for text char count box" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "intervalsOfRentReview")
    }

    "contain date format hint for leaseBegin-hint" in {
      val doc             = asDocument(createViewUsingForm(form))
      val firstOccupyHint = doc.getElementById("nextReview-hint").text()
      assert(firstOccupyHint == messages("label.nextReview.help"))
    }

    "contain date field for the value leaseBegin.day" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "nextReview.day", "Day")
      assertContainsText(doc, "nextReview.day")
    }

    "contain date field for the value leaseBegin.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "nextReview.month", "Month")
      assertContainsText(doc, "nextReview.month")
    }

    "contain date field for the value leaseBegin.year" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "nextReview.year", "Year")
      assertContainsText(doc, "nextReview.year")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
