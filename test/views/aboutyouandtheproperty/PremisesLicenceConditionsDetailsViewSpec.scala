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

package views.aboutyouandtheproperty

import form.aboutyouandtheproperty.PremisesLicenseConditionsDetailsForm
import models.submissions.aboutyouandtheproperty.PremisesLicenseConditionsDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class PremisesLicenceConditionsDetailsViewSpec extends QuestionViewBehaviours[PremisesLicenseConditionsDetails] {

  val messageKeyPrefix = "premisesLicenseConditionsDetails"

  override val form: Form[PremisesLicenseConditionsDetails] =
    PremisesLicenseConditionsDetailsForm.premisesLicenceDetailsForm

  def createView: () => Html = () => premisesLicenceConditionsDetailsView(form)(fakeRequest, messages)

  def createViewUsingForm: Form[PremisesLicenseConditionsDetails] => Html =
    (form: Form[PremisesLicenseConditionsDetails]) => premisesLicenceConditionsDetailsView(form)(fakeRequest, messages)

  "Property licence conditions details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the premises licence Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain an input for premisesLicenseConditionsDetails" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "premisesLicenseConditionsDetails")
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain save as draft button with the value Save as draft" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("save").text()
      assert(loginButton == messages("button.label.save"))
    }

    "contain get help section" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("help.premisesLicenseConditionsDetails.title")))
      assert(doc.toString.contains(messages("help.premisesLicenseConditionsDetails.heading")))
      assert(doc.toString.contains(messages("help.premisesLicenseConditionsDetails.p1")))
      assert(doc.toString.contains(messages("help.premisesLicenseConditionsDetails.list.p1")))
      assert(doc.toString.contains(messages("help.premisesLicenseConditionsDetails.list.p2")))
      assert(doc.toString.contains(messages("help.premisesLicenseConditionsDetails.list.p3")))
    }
  }
}
