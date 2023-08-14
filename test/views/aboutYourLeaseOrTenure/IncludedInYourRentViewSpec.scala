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

import form.aboutYourLeaseOrTenure.IncludedInYourRentForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure._
import models.submissions.common.{AnswerNo, AnswerYes}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class IncludedInYourRentViewSpec extends QuestionViewBehaviours[IncludedInYourRentDetails] {

  val messageKeyPrefix = "includedInYourRent"

  override val form = IncludedInYourRentForm.includedInYourRentForm

  def createView = () => includedInYourRentView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[IncludedInYourRentDetails]) =>
    includedInYourRentView(form, Summary("99996010001"))(fakeRequest, messages)

  "Included in rent view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to current lease begin Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain checkbox for the vat" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "vat", "includedInYourRent", "vat", false)
      assertContainsText(doc, messages("label.includedInYourRent.vat"))
    }

    "contain checkbox for the nondomesticRates" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "nondomesticRates", "includedInYourRent", "nondomesticRates", false)
      assertContainsText(doc, messages("label.includedInYourRent.nondomesticRates"))
    }

    "contain checkbox for the waterCharges" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "waterCharges", "includedInYourRent", "waterCharges", false)
      assertContainsText(doc, messages("label.includedInYourRent.waterCharges"))
    }

    "contain checkbox for none" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "none", "includedInYourRent", "none", false)
      assertContainsText(doc, messages("label.includedInYourRent.none"))
    }

  }
}
