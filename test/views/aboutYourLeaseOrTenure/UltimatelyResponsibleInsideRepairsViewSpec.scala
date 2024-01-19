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

import form.aboutYourLeaseOrTenure.UltimatelyResponsibleInsideRepairsForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.UltimatelyResponsibleInsideRepairs
import models.submissions.common._
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class UltimatelyResponsibleInsideRepairsViewSpec extends QuestionViewBehaviours[UltimatelyResponsibleInsideRepairs] {

  val messageKeyPrefix = "ultimatelyResponsibleIR"

  override val form = UltimatelyResponsibleInsideRepairsForm.ultimatelyResponsibleInsideRepairsForm

  def createView = () => ultimatelyResponsibleInsideRepairsView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[UltimatelyResponsibleInsideRepairs]) =>
    ultimatelyResponsibleInsideRepairsView(form, Summary("99996010001"))(fakeRequest, messages)

  "Ultimately responsible view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, "sharedResponsibilitiesIR")

    "has a link marked with back.link.label leading to the does the rent payable Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.DoesTheRentPayableController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for inside repair with the value landlord" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "insideRepairs",
        "insideRepairs",
        InsideRepairsLandlord.name,
        false
      )
      assertContainsText(doc, messages("label.landlord"))
    }

    "contain radio buttons for inside repair with the value tenant" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "insideRepairs-2",
        "insideRepairs",
        InsideRepairsTenant.name,
        false
      )
      assertContainsText(doc, messages("label.tenant"))
    }

    "contain radio buttons for inside repair with the value both" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "insideRepairs-3",
        "insideRepairs",
        InsideRepairsBoth.name,
        false
      )
      assertContainsText(doc, messages("label.both"))
    }

    "contain radio buttons for building insurance with the value landlord" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "insideRepairs",
        "insideRepairs",
        BuildingInsuranceLandlord.name,
        false
      )
      assertContainsText(doc, messages("label.landlord"))
    }

    "contain radio buttons for building insurance with the value tenant" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "insideRepairs-2",
        "insideRepairs",
        BuildingInsuranceTenant.name,
        false
      )
      assertContainsText(doc, messages("label.tenant"))
    }

    "contain radio buttons for building insurance with the value both" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "insideRepairs-3",
        "insideRepairs",
        BuildingInsuranceBoth.name,
        false
      )
      assertContainsText(doc, messages("label.both"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
