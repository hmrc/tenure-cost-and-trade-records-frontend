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

import form.Form6010.LettingOtherPartOfPropertiesForm
import models.submissions.Form6010.{LettingOtherPartOfPropertiesNo, LettingOtherPartOfPropertiesYes, LettingOtherPartOfProperty}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class LettingOtherPartOfPropertyViewSpec extends QuestionViewBehaviours[LettingOtherPartOfProperty] {

  val messageKeyPrefix = "LettingOtherPartOfProperties"

  override val form = LettingOtherPartOfPropertiesForm.lettingOtherPartOfPropertiesForm

  def createView = () =>
    lettingOtherPartOfPropertyView(
      form,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
    )(fakeRequest, messages)

  def createViewUsingForm = (form: Form[LettingOtherPartOfProperty]) =>
    lettingOtherPartOfPropertyView(
      form,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
    )(fakeRequest, messages)

  "Letting other parts of property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
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
        "LettingOtherPartOfProperties",
        "LettingOtherPartOfProperties",
        LettingOtherPartOfPropertiesYes.name,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "LettingOtherPartOfProperties-2",
        "LettingOtherPartOfProperties",
        LettingOtherPartOfPropertiesNo.name,
        false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain get help section" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("helpWithService.title")))
    }

    "contain get help section basic details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithServiceHeader")))
      assert(doc.toString.contains(messages("common.helpWithService")))
    }
  }
}
