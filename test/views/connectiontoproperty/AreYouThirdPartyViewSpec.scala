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

package views.connectiontoproperty

import actions.SessionRequest
import form.connectiontoproperty.AreYouThirdPartyForm
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import org.scalatest.matchers.must.Matchers.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class AreYouThirdPartyViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "areYouThirdParty"

  override val form = AreYouThirdPartyForm.areYouThirdPartyForm

  val sessionRequest6076 = SessionRequest(baseFilled6076Session, fakeRequest)
  val sessionRequest     = SessionRequest(baseFilled6010Session, fakeRequest)

  val backLink = controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url

  def createView = () =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetailsNoSession.toSummary)(
      sessionRequest,
      messages
    )

  def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetailsNoSession.toSummary)(
      sessionRequest,
      messages
    )

  def createView6076 = () =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetails6076YesSession.toSummary)(
      sessionRequest6076,
      messages
    )

  def createViewUsingForm6076 = (form: Form[AnswersYesNo]) =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetails6076YesSession.toSummary)(
      sessionRequest6076,
      messages
    )

  "Are you a third party view" must {

    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          checkServiceNameInHeaderBanner(createView())
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

//    "contain radio buttons for the value yes with hint 6076" in {
//      val doc = asDocument(createViewUsingForm6076(form))
//      assertContainsRadioButton(doc, "areYouThirdParty", "areYouThirdParty", AnswerYes.name, false)
//      assertContainsText(doc, messages("label.yes"))
//      assertContainsText(doc, messages("hint.areYouThirdParty.yes", "Wombles Inc"))
//    }
//
//    "contain radio buttons for the value no with hint 6076" in {
//      val doc = asDocument(createViewUsingForm6076(form))
//      assertContainsRadioButton(doc, "areYouThirdParty-2", "areYouThirdParty", AnswerNo.name, false)
//      assertContainsText(doc, messages("label.no"))
//      assertContainsText(doc, messages("hint.areYouThirdParty.no", "Wombles Inc"))
//    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
