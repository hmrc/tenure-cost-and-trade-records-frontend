/*
 * Copyright 2025 HM Revenue & Customs
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

package views.notconnected

import form.notconnected.PastConnectionForm
import models.pages.Summary
import models.submissions.common.AnswersYesNo.*
import models.submissions.notconnected.PastConnectionType
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class PastConnectionViewSpec extends QuestionViewBehaviours[PastConnectionType] {

  val messageKeyPrefix = "pastConnectionType"

  val backLink = controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url

  override val form: Form[PastConnectionType] = PastConnectionForm.pastConnectionForm

  def createView: () => Html = () =>
    pastConnectionView(form, Summary("99996010001", Some(prefilledAddress)), backLink)(using fakeRequest, messages)

  def createViewUsingForm: Form[PastConnectionType] => Html = (form: Form[PastConnectionType]) =>
    pastConnectionView(form, Summary("99996010001", Some(prefilledAddress)), backLink)(using fakeRequest, messages)

  "Past connection view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a reference number and address banner" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Reference:")
      assertContainsText(doc, "99996010/001")
      assertContainsText(doc, "Property:")
      assertContainsText(doc, "001, GORING ROAD, GORING-BY-SEA, WORTHING, WEST SUSSEX, BN12 4AX")
    }

    "has a link marked with back.link.label leading to the are you still connected Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.connectionToTheProperty"))
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "pastConnectionType",
        "pastConnectionType",
        AnswerYes.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "pastConnectionType-2",
        "pastConnectionType",
        AnswerNo.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
