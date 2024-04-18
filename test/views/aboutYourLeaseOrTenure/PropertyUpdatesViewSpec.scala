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

import form.aboutYourLeaseOrTenure.PropertyUpdatesForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.PropertyUpdates
import models.submissions.common.{AnswerNo, AnswerYes}
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class PropertyUpdatesViewSpec extends QuestionViewBehaviours[PropertyUpdates] {

  val messageKeyPrefix = "propertyUpdates"

  override val form = PropertyUpdatesForm.propertyUpdatesForm

  def createView = () => propertyUpdatesView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[PropertyUpdates]) =>
    propertyUpdatesView(form, Summary("99996010001"))(fakeRequest, messages)

  "Property update view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to interval of review Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for property updates with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyUpdates",
        "propertyUpdates",
        AnswerYes.name,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for property updates with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyUpdates-2",
        "propertyUpdates",
        AnswerNo.name,
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
