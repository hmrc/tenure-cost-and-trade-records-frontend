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

import form.Form6010.RentOpenMarketValueForm
import models.submissions.Form6010.RentOpenMarketValueDetails
import models.submissions.common.{AnswerNo, AnswerYes}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class rentOpenMarketViewSpec extends QuestionViewBehaviours[RentOpenMarketValueDetails] {

  val messageKeyPrefix = "rentOpenMarketValue"

  override val form = RentOpenMarketValueForm.rentOpenMarketValuesForm

  val backLink = controllers.aboutYourLeaseOrTenure.routes.AboutYourLandlordController.show().url

  def createView = () => rentOpenMarketValueView(form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[RentOpenMarketValueDetails]) =>
    rentOpenMarketValueView(form)(fakeRequest, messages)

  "Rent open market view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the franchise or letting tied to property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.Form6010.routes.RentIncludeFixtureAndFittingsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for rent open market with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "rentOpenMarketValue",
        "rentOpenMarketValue",
        AnswerYes.name,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for rent open market with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "rentOpenMarketValue-2",
        "rentOpenMarketValue",
        AnswerNo.name,
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
