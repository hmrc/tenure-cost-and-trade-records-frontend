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

import form.aboutYourLeaseOrTenure.ServicePaidSeparatelyChargeForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.ServicePaidSeparatelyCharge
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class ServicePaidSeparatelyChargeViewSpec extends QuestionViewBehaviours[ServicePaidSeparatelyCharge] {

  val messageKeyPrefix = "servicePaidSeparatelyCharge"

  override val form = ServicePaidSeparatelyChargeForm.servicePaidSeparatelyChargeForm

  def createView = () => servicePaidSeparatelyChargeView(form, 1, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[ServicePaidSeparatelyCharge]) =>
    servicePaidSeparatelyChargeView(form, 1, Summary("99996010001"))(fakeRequest, messages)

  "Service paid separately charge view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to Service paid separately description page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show(Some(1)).url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain input for the separately paid service charge" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "annualCharge")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
