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

import form.aboutyouandtheproperty.PremisesLicenseGrantedDetailsForm
import models.pages.Summary
import models.submissions.aboutyouandtheproperty.PremisesLicenseGrantedInformationDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class PremisesLicenceGrantedDetailsViewSpec extends QuestionViewBehaviours[PremisesLicenseGrantedInformationDetails] {

  val messageKeyPrefix = "premisesLicenseGrantedInformation"

  override val form = PremisesLicenseGrantedDetailsForm.premisesLicenseGrantedInformationDetailsForm

  def createView = () => premisesLicenceGrantedDetailsView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[PremisesLicenseGrantedInformationDetails]) =>
    premisesLicenceGrantedDetailsView(form, Summary("99996010001"))(fakeRequest, messages)

  "Property licence granted view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the licencable activities Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain an input for premisesLicenseGrantedInformation" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "premisesLicenseGrantedInformation")
    }

    "contain continue button with the value Continue" in {
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
      assert(doc.toString.contains(messages("help.premisesLicenseGrantedInformation.title")))
      assert(doc.toString.contains(messages("help.premisesLicenseGrantedInformation.heading1")))
      assert(doc.toString.contains(messages("help.premisesLicenseGrantedInformation.p1")))
      assert(doc.toString.contains(messages("help.premisesLicenseGrantedInformation.heading1")))
      assert(doc.toString.contains(messages("help.premisesLicenseGrantedInformation.p2")))
      assert(doc.toString.contains(messages("help.premisesLicenseGrantedInformation.list.p1")))
      assert(doc.toString.contains(messages("help.premisesLicenseGrantedInformation.list.p2")))
      assert(doc.toString.contains(messages("help.premisesLicenseGrantedInformation.list.p3")))
    }
  }
}
