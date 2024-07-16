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

import form.aboutYourLeaseOrTenure.MethodToFixCurrentRentForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.{MethodToFixCurrentRentDetails, MethodToFixCurrentRentIndependentExpert, MethodToFixCurrentRentsACourt, MethodToFixCurrentRentsAgreement, MethodToFixCurrentRentsArbitration}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class MethodOfFixCurrentRentViewSpec extends QuestionViewBehaviours[MethodToFixCurrentRentDetails] {

  val messageKeyPrefix = "methodUsedToFixCurrentRent"

  override val form = MethodToFixCurrentRentForm.methodToFixCurrentRentForm

  def createView = () => methodToFixCurrentRentView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[MethodToFixCurrentRentDetails]) =>
    methodToFixCurrentRentView(form, Summary("99996010001"))(fakeRequest, messages)

  "Method of fix current rent view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to how is current rent fixed Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for the value agreement" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "methodUsedToFixCurrentRent",
        "methodUsedToFixCurrentRent",
        MethodToFixCurrentRentsAgreement.name,
        false
      )
      assertContainsText(doc, messages("label.methodUsedToFixCurrentRent.agreement"))
    }

    "contain radio buttons for the value arbitration" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "methodUsedToFixCurrentRent-2",
        "methodUsedToFixCurrentRent",
        MethodToFixCurrentRentsArbitration.name,
        false
      )
      assertContainsText(doc, messages("label.methodUsedToFixCurrentRent.arbitration"))
    }

    "contain radio buttons for the value independent expert" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "methodUsedToFixCurrentRent-3",
        "methodUsedToFixCurrentRent",
        MethodToFixCurrentRentIndependentExpert.name,
        false
      )
      assertContainsText(doc, messages("label.methodUsedToFixCurrentRent.independentExpert"))
    }

    "contain radio buttons for the value court" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "methodUsedToFixCurrentRent-4",
        "methodUsedToFixCurrentRent",
        MethodToFixCurrentRentsACourt.name,
        false
      )
      assertContainsText(doc, messages("label.methodUsedToFixCurrentRent.aCourt"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
