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

package views.abouttheproperty

import form.abouttheproperty.PremisesLicenseConditionsDetailsForm
import models.submissions.abouttheproperty.PremisesLicenseConditionsDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class PremisesLicenceConditionsDetailsViewSpec extends QuestionViewBehaviours[PremisesLicenseConditionsDetails] {

  def premisesLicenceConditionsView =
    app.injector.instanceOf[views.html.abouttheproperty.premisesLicenseConditionsDetails]

  val messageKeyPrefix = "premisesLicenseConditionsDetails"

  override val form = PremisesLicenseConditionsDetailsForm.premisesLicenceDetailsForm

  def createView = () => premisesLicenceConditionsView(form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[PremisesLicenseConditionsDetails]) =>
    premisesLicenceConditionsView(form)(fakeRequest, messages)

  "Property licence conditions details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the premises licence Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.abouttheproperty.routes.PremisesLicenseConditionsController.show().url
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain get help section use of licence activities details" in {
      val doc = asDocument(createView())
      assert(
        doc.toString.contains(
          messages("premisesLicenseConditionsDetails.helpWithServicePremisesLicenseConditionsDetailsHeader")
        )
      )
      assert(
        doc.toString.contains(
          messages("premisesLicenseConditionsDetails.helpWithServicePremisesLicenseConditionsDetails")
        )
      )
      assert(doc.toString.contains(messages("premisesLicenseConditionsDetails.listBlock1.p1")))
      assert(doc.toString.contains(messages("premisesLicenseConditionsDetails.listBlock1.p2")))
      assert(doc.toString.contains(messages("premisesLicenseConditionsDetails.listBlock1.p3")))
    }

    "contain get help section basic details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithServiceHeader")))
      assert(doc.toString.contains(messages("common.helpWithService")))
    }
  }
}
