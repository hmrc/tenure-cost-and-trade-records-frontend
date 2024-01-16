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

package views.additionalInformation

import form.aboutyouandtheproperty.AlternativeContactDetailsForm
import models.pages.Summary
import models.submissions.aboutyouandtheproperty.AlternativeContactDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class AlternativeContactDetailsViewSpec extends QuestionViewBehaviours[AlternativeContactDetails] {

  val messageKeyPrefix = "alternativeContactDetails"

  override val form: Form[AlternativeContactDetails] = AlternativeContactDetailsForm.alternativeContactDetailsForm

  def createView: () => Html = () => alternativeContactDetailsView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm: Form[AlternativeContactDetails] => Html = (form: Form[AlternativeContactDetails]) =>
    alternativeContactDetailsView(form, Summary("99996010001"))(fakeRequest, messages)

  "Alternative Contact Details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      "alternativeContactAddress.buildingNameNumber",
      "alternativeContactAddress.town",
      "alternativeContactAddress.postcode"
    )

    "has a link marked with back.link.label leading to the task Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.additionalinformation.routes.ContactDetailsQuestionController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.additionalInformation"))
    }

    "contain an input for alternativeContactAddress.street1" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "alternativeContactAddress.street1")
    }

    "contain an input for alternativeContactAddress.county" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "alternativeContactAddress.county")
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
