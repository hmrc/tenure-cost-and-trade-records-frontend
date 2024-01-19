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

package views.connectiontoproperty

import form.connectiontoproperty.VacantPropertyStartDateForm
import models.pages.Summary
import models.submissions.connectiontoproperty.StartDateOfVacantProperty
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class VacantPropertyStartDateViewSpec extends QuestionViewBehaviours[StartDateOfVacantProperty] {

  val messageKeyPrefix = "vacantPropertyStartDate"

  override val form: Form[StartDateOfVacantProperty] = VacantPropertyStartDateForm.vacantPropertyStartDateForm(messages)

  def createView: () => Html = () => vacantPropertiesStartDateView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm: Form[StartDateOfVacantProperty] => Html = (form: Form[StartDateOfVacantProperty]) =>
    vacantPropertiesStartDateView(form, Summary("99996010001"))(fakeRequest, messages)

  "Vacant property start date view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the vacant properties page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.connectiontoproperty.routes.VacantPropertiesController.show.url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.connectionToTheProperty"))
    }

    "contain date field for the value startDateOfVacantProperty.day" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "startDateOfVacantProperty.day", "Day")
      assertContainsText(doc, "startDateOfVacantProperty.day")
    }

    "contain date field for the value startDateOfVacantProperty.month" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "startDateOfVacantProperty.month", "Month")
      assertContainsText(doc, "startDateOfVacantProperty.month")
    }

    "contain date field for the value startDateOfVacantProperty.year" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsLabel(doc, "startDateOfVacantProperty.year", "Year")
      assertContainsText(doc, "startDateOfVacantProperty.year")
    }

    "contain continue button with the value continue" in {
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
}
