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

package views.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.RentIncludeTradeServicesForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure._
import models.submissions.common.{AnswerNo, AnswerYes}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class RentIncludeTradeServiceViewSpec extends QuestionViewBehaviours[RentIncludeTradeServicesDetails] {

  val messageKeyPrefix = "rentIncludeTradeServices"

  override val form = RentIncludeTradeServicesForm.rentIncludeTradeServicesForm

  val backLink = controllers.aboutYourLeaseOrTenure.routes.AboutYourLandlordController.show().url

  def createView = () => rentIncludeTradeServicesView(form, "FOR6010", Summary("99996010001"))(fakeRequest, messages)

  def create6030View = () =>
    rentIncludeTradeServicesView(form, "FOR6030", Summary("99996030001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[RentIncludeTradeServicesDetails]) =>
    rentIncludeTradeServicesView(form, "FOR6010", Summary("99996010001"))(fakeRequest, messages)

  "Rent include trade services view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleBuildingInsuranceController
        .show()
        .url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for rent include trade services with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "rentIncludeTradeServices",
        "rentIncludeTradeServices",
        AnswerYes.name,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for rent include trade services with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "rentIncludeTradeServices-2",
        "rentIncludeTradeServices",
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

    "contain correct hint for 6030 type of form" in {
      val doc  = asDocument(create6030View())
      val hint = doc.getElementById("rentIncludeTradeServices-hint").text()
      assert(hint == messages("hint.rentIncludeTradeServices.for6030"))
    }

    "contain correct hint for other types of form" in {
      val doc  = asDocument(createViewUsingForm(form))
      val hint = doc.getElementById("rentIncludeTradeServices-hint").text()
      assert(hint == messages("hint.rentIncludeTradeServices"))
    }
  }
}
