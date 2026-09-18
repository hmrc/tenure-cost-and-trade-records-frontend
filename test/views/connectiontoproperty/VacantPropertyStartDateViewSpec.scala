/*
 * Copyright 2026 HM Revenue & Customs
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
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

import java.time.LocalDate

class VacantPropertyStartDateViewSpec extends QuestionViewBehaviours[LocalDate]:

  private val backLink = controllers.connectiontoproperty.routes.VacantPropertiesController.show().url

  private val messageKeyPrefix = "vacantPropertyStartDate"

  override val form: Form[LocalDate] = VacantPropertyStartDateForm.vacantPropertyStartDateForm(using messages)

  private def createView: () => Html =
    () => vacantPropertiesStartDateView(form, Summary("99996010001"), backLink)(using getRequest, messages)

  private def createViewUsingForm: Form[LocalDate] => Html =
    form => vacantPropertiesStartDateView(form, Summary("99996010001"), backLink)(using getRequest, messages)

  "Vacant property start date view" should {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView, "Vacant properties", backLink)

    "Section heading is visible" in {
      val doc  = asDocument(createViewUsingForm(form))
      val html = doc.getElementsByClass("govuk-caption-m").html()

      html shouldBe s"""<span class="govuk-visually-hidden">This section is </span>${messages("label.section.connectionToTheProperty")}"""
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
      val loginButton = doc.getElementById("continue-button").text()

      loginButton shouldBe messages("button.continue.label")
    }

    "contain save as draft button with the value Save as draft" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("save-button").text()

      loginButton shouldBe messages("button.save.label")
    }
  }
