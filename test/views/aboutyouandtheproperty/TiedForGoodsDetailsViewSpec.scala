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

package views.aboutyouandtheproperty

import form.aboutyouandtheproperty.TiedForGoodsDetailsForm
import models.pages.Summary
import models.submissions.aboutyouandtheproperty._
import models.submissions.aboutyouandtheproperty.TiedForGoodsInformation.*
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class TiedForGoodsDetailsViewSpec extends QuestionViewBehaviours[TiedForGoodsInformationDetails] {

  val messageKeyPrefix = "tiedForGoodsDetails"

  override val form: Form[TiedForGoodsInformationDetails] = TiedForGoodsDetailsForm.tiedForGoodsDetailsForm

  def createView: () => Html = () => tiedForGoodsDetailsView(form, Summary("99996010001"))(using fakeRequest, messages)

  def createViewUsingForm: Form[TiedForGoodsInformationDetails] => Html =
    (form: Form[TiedForGoodsInformationDetails]) =>
      tiedForGoodsDetailsView(form, Summary("99996010001"))(using fakeRequest, messages)

  "Tied for goods details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain radio buttons for the value full tie" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "tiedForGoodsDetails",
        "tiedForGoodsDetails",
        TiedForGoodsInformationDetailsFullTie.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("tiedForGoodsDetails.fullTie"))
    }

    "contain radio buttons for the value beer only" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "tiedForGoodsDetails-2",
        "tiedForGoodsDetails",
        TiedForGoodsInformationDetailsBeerOnly.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("tiedForGoodsDetails.beerOnly"))
    }

    "contain radio buttons for the value partial tie" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "tiedForGoodsDetails-3",
        "tiedForGoodsDetails",
        TiedForGoodsInformationDetailsPartialTie.toString,
        isChecked = false
      )
      assertContainsText(doc, messages("tiedForGoodsDetails.partialTie"))
    }

    "contain continue button with the value Continue" in {
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
