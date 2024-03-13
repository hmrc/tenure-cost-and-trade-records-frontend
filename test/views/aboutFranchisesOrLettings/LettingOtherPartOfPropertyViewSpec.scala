/*
 * Copyright 2024 HM Revenue & Customs
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

import form.aboutfranchisesorlettings.{ConcessionOrFranchiseFeeForm, LettingOtherPartOfPropertiesForm}
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours
import models.pages.Summary

class LettingOtherPartOfPropertyViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "LettingOtherPartOfProperties"

  override val form = LettingOtherPartOfPropertiesForm.lettingOtherPartOfPropertiesForm
  val form6030      = ConcessionOrFranchiseFeeForm.concessionOrFranchiseFeeForm

  def createView = () =>
    lettingOtherPartOfPropertyView(
      form,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url,
      Summary("99996010001"),
      "FOR6010"
    )(fakeRequest, messages)

  def createView6030 = () =>
    lettingOtherPartOfPropertyView(
      form,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url,
      Summary("99996030001"),
      "FOR6030"
    )(fakeRequest, messages)

  def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    lettingOtherPartOfPropertyView(
      form,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url,
      Summary("99996010001"),
      "FOR6010"
    )(fakeRequest, messages)

  def createViewUsingForm6030 = (form: Form[AnswersYesNo]) =>
    lettingOtherPartOfPropertyView(
      form,
      messageKeyPrefix,
      controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url,
      Summary("99996030001"),
      "FOR6030"
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
        AnswerYes.name,
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
        AnswerNo.name,
        false
      )
      assertContainsText(doc, messages("label.no"))
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
  }

  "Letting other parts of property view 6030" must {

    behave like normalPage(createView6030, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView6030())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm6030(form6030)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheFranchiseLettings"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm6030(form6030))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain save as draft button with the value Save as draft" in {
      val doc         = asDocument(createViewUsingForm6030(form6030))
      val loginButton = doc.getElementById("save").text()
      assert(loginButton == messages("button.label.save"))
    }
  }
}
