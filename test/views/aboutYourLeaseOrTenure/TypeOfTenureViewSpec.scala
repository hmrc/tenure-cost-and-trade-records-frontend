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

import form.aboutYourLeaseOrTenure.TypeOfTenureForm.typeOfTenureForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.TypeOfTenure
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class TypeOfTenureViewSpec extends QuestionViewBehaviours[TypeOfTenure] {

  val messageKeyPrefix = "typeOfTenure"

  override val form: Form[TypeOfTenure] = typeOfTenureForm

  def createView: () => Html = () => typeOfTenureView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm: Form[TypeOfTenure] => Html = (form: Form[TypeOfTenure]) =>
    typeOfTenureView(form, Summary("99996010001"))(fakeRequest, messages)

  "Type of Tenure view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, "typeOfTenureDetails")

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.routes.TaskListController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain checkbox for the leasehold" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "leasehold", "typeOfTenure[]", "leasehold", isChecked = false)
      assertContainsText(doc, messages("label.typeOfTenure.leasehold"))
    }

    "contain checkbox for the license" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "license", "typeOfTenure[]", "license", isChecked = false)
      assertContainsText(doc, messages("label.typeOfTenure.license"))
    }

    "contain checkbox for the tenancy" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "tenancy", "typeOfTenure[]", "tenancy", isChecked = false)
      assertContainsText(doc, messages("label.typeOfTenure.tenancy"))
    }

    "contain checkbox for the longLeasehold" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsCheckBox(doc, "longLeasehold", "typeOfTenure[]", "longLeasehold", isChecked = false)
      assertContainsText(doc, messages("label.typeOfTenure.long.leasehold"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }

}
