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

import form.aboutYourLeaseOrTenure.RentIncludeFixtureAndFittingDetailsTextAreaForm
import models.pages.Summary
import org.scalatest.matchers.must.Matchers.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class RentIncludeFixtureAndFittingsDetailsTextAreaViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "describeFittingsTextArea"

  override val form =
    RentIncludeFixtureAndFittingDetailsTextAreaForm.rentIncludeFixtureAndFittingsDetailsTextAreaForm

  def createView = () =>
    rentIncludeFixtureAndFittingsDetailsTextAreaView(form, Summary("99996076001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) =>
    rentIncludeFixtureAndFittingsDetailsTextAreaView(form, Summary("99996076001"))(fakeRequest, messages)

  "Fixture and fittings details" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
