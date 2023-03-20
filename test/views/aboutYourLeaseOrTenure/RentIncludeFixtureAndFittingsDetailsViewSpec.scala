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

import form.Form6010.RentIncludeFixtureAndFittingDetailsForm
import models.AnnualRent
import models.submissions.Form6010.RentIncludeFixturesOrFittingsInformationDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class RentIncludeFixtureAndFittingsDetailsViewSpec
    extends QuestionViewBehaviours[RentIncludeFixturesOrFittingsInformationDetails] {

  val messageKeyPrefix = "rentIncludeFixturesAndFittingsDetails"

  override val form = RentIncludeFixtureAndFittingDetailsForm.rentIncludeFixtureAndFittingsDetailsForm

  def createView = () => rentIncludeFixtureAndFittingsDetailsView(form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[RentIncludeFixturesOrFittingsInformationDetails]) =>
    rentIncludeFixtureAndFittingsDetailsView(form)(fakeRequest, messages)

  "Rent include fixture and fittings details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.Form6010.routes.RentIncludeFixtureAndFittingsController.show.url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain currency field for the value rentIncludeFixturesAndFittingsDetails" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "rentIncludeFixturesAndFittingsDetails")
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
