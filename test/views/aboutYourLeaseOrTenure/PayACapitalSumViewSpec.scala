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

import form.Form6010.PayACapitalSumForm
import models.submissions.Form6010.PayACapitalSumDetails
import models.submissions.common.{AnswerNo, AnswerYes}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class PayACapitalSumViewSpec extends QuestionViewBehaviours[PayACapitalSumDetails] {

  val messageKeyPrefix = "capitalSumOrPremium"

  override val form = PayACapitalSumForm.payACapitalSumForm

  val backLink = controllers.Form6010.routes.TenantsAdditionsDisregardedController.show.url

  def createView = () => payACapitalSumView(form, backLink)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[PayACapitalSumDetails]) =>
    payACapitalSumView(form, backLink)(fakeRequest, messages)

  "capital sum or premium view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to tenants additions disregarded Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.Form6010.routes.TenantsAdditionsDisregardedController.show.url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for capital sum with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "capitalSumOrPremium",
        "capitalSumOrPremium",
        AnswerYes.name,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for capital sum with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "capitalSumOrPremium-2",
        "capitalSumOrPremium",
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
