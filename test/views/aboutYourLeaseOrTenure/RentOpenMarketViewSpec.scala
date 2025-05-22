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
import form.aboutYourLeaseOrTenure.RentOpenMarketValueForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.RentOpenMarketValueDetails
import models.submissions.common.{AnswerNo, AnswerYes}
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import views.behaviours.QuestionViewBehaviours

class RentOpenMarketViewSpec extends QuestionViewBehaviours[RentOpenMarketValueDetails] {

  val messageKeyPrefix = "rentOpenMarketValue"

  override val form = RentOpenMarketValueForm.rentOpenMarketValuesForm

  val sessionRequest6020full: SessionRequest[AnyContentAsEmpty.type] =
    SessionRequest(prefilledFull6020Session, fakeRequest)

  val backLink = controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url

  def createView     = () => rentOpenMarketValueView(form, backLink, Summary("99996010001"))(using fakeRequest, messages)
  def createView6020 = () =>
    rentOpenMarketValueView(form, backLink, Summary("99996020001"))(using sessionRequest6020full, messages)

  def createViewUsingForm = (form: Form[RentOpenMarketValueDetails]) =>
    rentOpenMarketValueView(form, backLink, Summary("99996010001"))(using fakeRequest, messages)

  "Rent open market view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
    }

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page 6020" in {
      val doc          = asDocument(createView6020())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for rent open market with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "rentOpenMarketValue",
        "rentOpenMarketValue",
        AnswerYes.name,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for rent open market with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "rentOpenMarketValue-2",
        "rentOpenMarketValue",
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
