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

import form.aboutyouandtheproperty.EnforcementActionDetailsForm
import models.submissions.aboutyouandtheproperty.EnforcementActionHasBeenTakenInformationDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class EnforcementActionBeenTakenDetailsViewSpec
    extends QuestionViewBehaviours[EnforcementActionHasBeenTakenInformationDetails] {

  def enforcemenntActionBeenTakenDetails =
    app.injector.instanceOf[views.html.aboutyouandtheproperty.enforcementActionBeenTakenDetails]

  val messageKeyPrefix = "enforcementActionHasBeenTakenDetails"

  override val form = EnforcementActionDetailsForm.enforcementActionDetailsForm

  def createView = () => enforcemenntActionBeenTakenDetails(form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[EnforcementActionHasBeenTakenInformationDetails]) =>
    enforcemenntActionBeenTakenDetails(form)(fakeRequest, messages)

  "Enforcement action taken details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the enforcement action Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
