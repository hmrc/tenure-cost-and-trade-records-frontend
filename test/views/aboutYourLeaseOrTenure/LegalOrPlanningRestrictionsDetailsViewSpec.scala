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

import form.aboutYourLeaseOrTenure.LegalOrPlanningRestrictionsDetailsForm.legalOrPlanningRestrictionsDetailsForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.LegalOrPlanningRestrictionsDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class LegalOrPlanningRestrictionsDetailsViewSpec extends QuestionViewBehaviours[LegalOrPlanningRestrictionsDetails] {

  val messageKeyPrefix = "legalOrPlanningRestrictionsDetails"

  override val form = legalOrPlanningRestrictionsDetailsForm

  def createView = () => legalOrPlanningRestrictionsDetailsView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[LegalOrPlanningRestrictionsDetails]) =>
    legalOrPlanningRestrictionsDetailsView(form, Summary("99996010001"))(fakeRequest, messages)

  "Legal or planning restrictions view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the tenants additions disregarded Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show.url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain an input for legal or planning restriction details" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "legalOrPlanningRestrictionsDetails")
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
