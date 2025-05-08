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
import form.aboutYourLeaseOrTenure.UltimatelyResponsibleOutsideRepairsForm
import models.submissions.aboutYourLeaseOrTenure.UltimatelyResponsibleOutsideRepairs
import models.submissions.common._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class UltimatelyResponsibleOutsideRepairsViewSpec extends QuestionViewBehaviours[UltimatelyResponsibleOutsideRepairs] {

  val messageKeyPrefix = "ultimatelyResponsibleOR"

  override val form = UltimatelyResponsibleOutsideRepairsForm.ultimatelyResponsibleOutsideRepairsForm

  private val backLink       =
    controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController.show().url
  private val sessionRequest = SessionRequest(baseFilled6010Session, fakeRequest)

  def createView = () => ultimatelyResponsibleOutsideRepairsView(form, backLink)(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[UltimatelyResponsibleOutsideRepairs]) =>
    ultimatelyResponsibleOutsideRepairsView(form, backLink)(using sessionRequest, messages)

  "Ultimately responsible view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, "sharedResponsibilitiesOR")

    "has a link marked with back.link.label leading to the does the rent payable Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController
        .show()
        .url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for outside repair with the value landlord" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "outsideRepairs",
        "outsideRepairs",
        OutsideRepairsLandlord.name,
        false
      )
      assertContainsText(doc, messages("label.landlord"))
    }

    "contain radio buttons for outside repair with the value tenant" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "outsideRepairs-2",
        "outsideRepairs",
        OutsideRepairsTenant.name,
        false
      )
      assertContainsText(doc, messages("label.tenant"))
    }

    "contain radio buttons for outside repair with the value both" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "outsideRepairs-3",
        "outsideRepairs",
        OutsideRepairsBoth.name,
        false
      )
      assertContainsText(doc, messages("label.both"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
