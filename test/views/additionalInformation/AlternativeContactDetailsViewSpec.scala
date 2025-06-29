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

package views.additionalInformation

import form.aboutyouandtheproperty.AlternativeContactDetailsForm
import models.pages.Summary
import models.submissions.common.Address
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class AlternativeContactDetailsViewSpec extends QuestionViewBehaviours[Address] {

  val messageKeyPrefix = "alternativeContactDetails"

  override val form: Form[Address] = AlternativeContactDetailsForm.alternativeContactDetailsForm

  def createView: () => Html = () =>
    alternativeContactDetailsView(form, Summary("99996010001"))(using fakeRequest, messages)

  def createViewUsingForm: Form[Address] => Html = (form: Form[Address]) =>
    alternativeContactDetailsView(form, Summary("99996010001"))(using fakeRequest, messages)

  "Alternative Contact Details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "buildingNameNumber",
      "town"
    )

    "has a link marked with back.link.label leading to the contact details page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain an input for street1" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "street1")
    }

    "contain an input for county" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "county")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
