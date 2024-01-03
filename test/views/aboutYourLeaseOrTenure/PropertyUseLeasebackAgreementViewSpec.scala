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

import form.aboutYourLeaseOrTenure.PropertyUseLeasebackArrangementForm.propertyUseLeasebackArrangementForm
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.PropertyUseLeasebackArrangementDetails
import models.submissions.common.{AnswerNo, AnswerYes}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.api.i18n.Lang
import views.behaviours.QuestionViewBehaviours

import java.util.Locale
class PropertyUseLeasebackAgreementViewSpec extends QuestionViewBehaviours[PropertyUseLeasebackArrangementDetails] {

  val messageKeyPrefix = "propertyUseLeasebackArrangement"

  override val form = propertyUseLeasebackArrangementForm

  val backLink   = controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show().url
  def createView = () =>
    propertyUseLeasebackAgreementView(form, backLink, "Wombles Inc", Summary("99996010001"))(
      fakeRequest,
      messages
    )

  def createViewUsingForm = (form: Form[PropertyUseLeasebackArrangementDetails]) =>
    propertyUseLeasebackAgreementView(form, backLink, "Wombles Inc", Summary("99996010001"))(
      fakeRequest,
      messages
    )

  "Property Use Leaseback Agreement view" must {

    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc  = asDocument(createView())
          val nav  = Option {
            doc.getElementById("proposition-menu")
          }.getOrElse(
            doc
              .getElementsByAttributeValue("class", "hmrc-header__service-name hmrc-header__service-name--linked")
              .first()
              .parent()
          )
          val span = nav.children.first
          span.text mustBe messagesApi("site.service_name")(Lang(Locale.UK))
        }

        "Section heading is visible" in {
          val doc         = asDocument(createViewUsingForm(form))
          val sectionText = doc.getElementsByClass("govuk-caption-m").text()
          assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
        }

        "display the correct browser title" in {
          val doc = asDocument(createView())
          assertEqualsValue(
            doc,
            "title",
            messages("service.title", messages("propertyUseLeasebackArrangement.title"))
          )
        }

        "display the correct page title" in {
          val doc = asDocument(createView())
          assertEqualsValue(
            doc,
            "h1",
            messages("propertyUseLeasebackArrangement.heading", "Wombles Inc")
          )
        }

        "display language toggles" in {
          val doc = asDocument(createView())

          val LangToggle = doc.getElementsByClass("hmrc-language-select")
          assert(LangToggle.text().contains("English"))
          assert(LangToggle.text().contains("Cymraeg"))
          assert(LangToggle.toString.contains("/send-trade-and-cost-information/hmrc-frontend/language/cy"))
        }
      }
    }

    "has a link marked with back.link.label leading to lease or agreement years Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show.url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain radio buttons for payment when lease granted with the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyUseLeasebackArrangement",
        "propertyUseLeasebackArrangement",
        AnswerYes.name,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for payment when lease granted with the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "propertyUseLeasebackArrangement-2",
        "propertyUseLeasebackArrangement",
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
