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

import form.abouttheproperty.EnforcementActionForm
import models.submissions.abouttheproperty.EnforcementActionsYes
import models.submissions.abouttheproperty.{EnforcementAction, EnforcementActionsNo, EnforcementActionsYes}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class EnforcementActionBeenTakenViewSpec extends QuestionViewBehaviours[EnforcementAction] {

  def enforcementActionsTakenView = app.injector.instanceOf[views.html.abouttheproperty.enforcementActionBeenTaken]

  val messageKeyPrefix = "enforcementActionHasBeenTaken"

  override val form = EnforcementActionForm.enforcementActionForm

  def createView = () => enforcementActionsTakenView(form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[EnforcementAction]) => enforcementActionsTakenView(form)(fakeRequest, messages)

  "Enforcement Action view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the website for property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.abouttheproperty.routes.PremisesLicenseConditionsController.show().url
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "enforcementAction",
        "enforcementAction",
        EnforcementActionsYes.name,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "enforcementAction-2",
        "enforcementAction",
        EnforcementActionsNo.name,
        false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain get help section use of enforcement action taken details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("enforcementAction.helpWithServiceEnforcementActionHeader")))
      assert(doc.toString.contains(messages("enforcementAction.helpWithServiceEnforcementAction")))
    }

    "contain get help section basic details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithServiceHeader")))
      assert(doc.toString.contains(messages("common.helpWithService")))
    }
  }
}
