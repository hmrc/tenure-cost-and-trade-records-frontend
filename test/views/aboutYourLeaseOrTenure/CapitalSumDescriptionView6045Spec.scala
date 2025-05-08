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

package views.aboutYourLeaseOrTenure

import actions.SessionRequest
import form.aboutYourLeaseOrTenure.CapitalSumDescriptionForm.capitalSumDescriptionForm
import models.submissions.aboutYourLeaseOrTenure.CapitalSumDescription
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CapitalSumDescriptionView6045Spec extends QuestionViewBehaviours[CapitalSumDescription] {

  val messageKeyPrefix = "capitalSumDescription6045"

  override val form = capitalSumDescriptionForm

  val sessionRequest = SessionRequest(stillConnectedDetails6045YesSession, fakeRequest)

  def createView = () => capitalSumDescriptionView(form)(using sessionRequest, messages)

  def createViewUsingForm = (form: Form[CapitalSumDescription]) =>
    capitalSumDescriptionView(form)(using sessionRequest, messages)

  "Capital sum description view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to pay capital sum question Page" in {

      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain an input for capital sum details " in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "capitalSumDescription")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
