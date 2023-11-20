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

import form.connectiontoproperty.AreYouThirdPartyForm
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.api.i18n.Lang
import views.behaviours.QuestionViewBehaviours

import java.util.Locale

class AreYouThirdPartyViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "areYouThirdParty"

  override val form = AreYouThirdPartyForm.areYouThirdPartyForm

  val backLink = controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url

  def createView = () =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetailsNoSession.toSummary)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetailsNoSession.toSummary)(fakeRequest, messages)

  "Are you a thrid party view" must {

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
          assert(sectionText == messages("label.section.connectionToTheProperty"))
        }

        "display the correct browser title" in {
          val doc = asDocument(createView())
          assertEqualsValue(
            doc,
            "title",
            messages("service.title", messages("areYouThirdParty.heading"))
          )
        }

        "display the correct page title" in {
          val doc = asDocument(createView())
          assertEqualsValue(
            doc,
            "h1",
            messages("areYouThirdParty.title", "Wombles Inc")
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

    "contain radio buttons for the value yes with hint" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(doc, "areYouThirdParty", "areYouThirdParty", AnswerYes.name, false)
      assertContainsText(doc, messages("label.yes"))
      assertContainsText(doc, messages("hint.areYouThirdParty.yes", "Wombles Inc"))
    }

    "contain radio buttons for the value no with hint" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(doc, "areYouThirdParty-2", "areYouThirdParty", AnswerNo.name, false)
      assertContainsText(doc, messages("label.no"))
      assertContainsText(doc, messages("hint.areYouThirdParty.no", "Wombles Inc"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
