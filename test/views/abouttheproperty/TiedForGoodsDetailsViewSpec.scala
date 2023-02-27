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

package views.abouttheproperty

import form.abouttheproperty.TiedForGoodsDetailsForm
import models.submissions.abouttheproperty.{TiedForGoodsInformationDetails, TiedForGoodsInformationDetailsBeerOnly, TiedForGoodsInformationDetailsFullTie, TiedForGoodsInformationDetailsPartialTie}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class TiedForGoodsDetailsViewSpec extends QuestionViewBehaviours[TiedForGoodsInformationDetails] {

  def tiedForGoodsDetailsView = app.injector.instanceOf[views.html.abouttheproperty.tiedForGoodsDetails]

  val messageKeyPrefix = "tiedForGoodsDetails"

  override val form = TiedForGoodsDetailsForm.tiedForGoodsDetailsForm

  def createView = () => tiedForGoodsDetailsView(form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[TiedForGoodsInformationDetails]) =>
    tiedForGoodsDetailsView(form)(fakeRequest, messages)

  "Tied for goods details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.abouttheproperty.routes.TiedForGoodsController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain radio buttons for the value full tie" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "tiedForGoodsDetails",
        "tiedForGoodsDetails",
        TiedForGoodsInformationDetailsFullTie.name,
        false
      )
      assertContainsText(doc, messages("tiedForGoodsDetails.fullTie"))
    }

    "contain radio buttons for the value beer only" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "tiedForGoodsDetails-2",
        "tiedForGoodsDetails",
        TiedForGoodsInformationDetailsBeerOnly.name,
        false
      )
      assertContainsText(doc, messages("tiedForGoodsDetails.beerOnly"))
    }

    "contain radio buttons for the value partial tie" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "tiedForGoodsDetails-3",
        "tiedForGoodsDetails",
        TiedForGoodsInformationDetailsPartialTie.name,
        false
      )
      assertContainsText(doc, messages("tiedForGoodsDetails.partialTie"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
