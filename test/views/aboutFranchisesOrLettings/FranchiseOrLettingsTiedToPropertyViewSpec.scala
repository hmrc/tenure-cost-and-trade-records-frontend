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

package views.aboutFranchisesOrLettings

import form.aboutfranchisesorlettings.FranchiseOrLettingsTiedToPropertyForm
import models.submissions.Form6010._
import models.submissions.aboutfranchisesorlettings.{FranchiseOrLettingsTiedToPropertiesNo, FranchiseOrLettingsTiedToPropertiesYes, FranchiseOrLettingsTiedToProperty}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class FranchiseOrLettingsTiedToPropertyViewSpec extends QuestionViewBehaviours[FranchiseOrLettingsTiedToProperty] {

  val messageKeyPrefix = "franchiseLettings"

  override val form = FranchiseOrLettingsTiedToPropertyForm.franchiseOrLettingsTiedToPropertyForm

  def createView = () => franchiseOrLettingsTiedToPropertyView(form, "FOR6010")(fakeRequest, messages)

  def createViewUsingForm = (form: Form[FranchiseOrLettingsTiedToProperty]) =>
    franchiseOrLettingsTiedToPropertyView(form, "FOR6010")(fakeRequest, messages)

  "Franchise or lettings tied to property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.routes.TaskListController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseLettings"))
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "franchiseOrLettingsTiedToProperty",
        "franchiseOrLettingsTiedToProperty",
        FranchiseOrLettingsTiedToPropertiesYes.name,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "franchiseOrLettingsTiedToProperty-2",
        "franchiseOrLettingsTiedToProperty",
        FranchiseOrLettingsTiedToPropertiesNo.name,
        false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
