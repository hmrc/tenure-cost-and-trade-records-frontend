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

package views.aboutYourLeaseOrTenure

import actions.SessionRequest
import form.aboutYourLeaseOrTenure.ServicePaidSeparatelyListForm
import models.pages.Summary
import models.submissions.common.AnswersYesNo
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class ServicePaidSeparatelyListViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "servicePaidSeparatelyList"

  override val form = ServicePaidSeparatelyListForm.addServicePaidSeparatelyForm

  val sessionRequest = SessionRequest(stillConnectedDetails6030YesSession, fakeRequest)

  val backLink = controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show().url

  def createView = () =>
    servicePaidSeparatelyListView(form, 0, backLink, Summary("99996010001"))(sessionRequest, messages)

  def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    servicePaidSeparatelyListView(form, 0, backLink, Summary("99996010001"))(sessionRequest, messages)

  "Services paid separately list view" should {

    "has a link marked with back.link.label leading to the list of Describe the trade service which is paid for separately page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe backLink
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "servicePaidSeparatelyList",
        "servicePaidSeparatelyList",
        "yes",
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons  with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "servicePaidSeparatelyList-2",
        "servicePaidSeparatelyList",
        "no",
        false
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
