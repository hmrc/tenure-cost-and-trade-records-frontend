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

package views.aboutFranchisesOrLettings

import actions.SessionRequest
import form.aboutfranchisesorlettings.RentReceivedFromForm
import models.ForType.FOR6010
import models.Session
import models.submissions.aboutfranchisesorlettings.RentReceivedFrom
import play.api.data.Form
import play.api.mvc.AnyContent
import views.behaviours.QuestionViewBehaviours

class RentReceivedFromViewSpec extends QuestionViewBehaviours[RentReceivedFrom]:

  override val form: Form[RentReceivedFrom] = RentReceivedFromForm.rentReceivedFromForm(using messages)

  private val fakeSessionRequest = SessionRequest[AnyContent](
    sessionData = Session(
      referenceNumber,
      FOR6010,
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      isWelsh = false
    ),
    request = getRequest
  )

  private def createView = () =>
    rentReceivedFromView(
      form,
      0,
      "separate business",
      Some("/backLinkUrl")
    )(using fakeSessionRequest, messages)

  private def createViewUsingForm = (form: Form[RentReceivedFrom]) =>
    rentReceivedFromView(
      form,
      0,
      "separate business",
      Some("/backLinkUrl")
    )(using fakeSessionRequest, messages)

  "Catering operation rent details view" should {

    behave like normalPageWithMessageExtra(createView, "rent.received.from", "separate business")

    behave like pageWithTextFields(createViewUsingForm, "annualRent")

    behave like pageWithBackLink(createView, "/backLinkUrl", "/backLinkUrl")

    "Section caption is visible" in {
      val doc  = asDocument(createViewUsingForm(form))
      val html = doc.getElementsByClass("govuk-caption-m").html()

      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.aboutConcessionsOrLettings")}"""
    }

    "contain date label for label.annualRent" in {
      val doc      = asDocument(createViewUsingForm(form))
      val forLabel = doc.getElementsByAttributeValue("for", "annualRent").text()

      forLabel shouldBe messages("label.annualRent")
    }

    "contain checkbox" in {
      val doc    = asDocument(createViewUsingForm(form))
      val legend = doc.getElementsByClass("govuk-label govuk-checkboxes__label govuk-!-font-weight-bold").text()

      legend shouldBe messages("rent.received.from.confirm")
    }

    "contain continue button with the value Continue" in {
      val doc            = asDocument(createViewUsingForm(form))
      val continueButton = doc.getElementById("continue-button").text()

      continueButton shouldBe messages("button.continue.label")
    }

    "contain save as draft button with the value Save as draft" in {
      val doc        = asDocument(createViewUsingForm(form))
      val saveButton = doc.getElementById("save-button").text()

      saveButton shouldBe messages("button.save.label")
    }
  }
