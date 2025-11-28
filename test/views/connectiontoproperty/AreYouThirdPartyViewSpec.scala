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

package views.connectiontoproperty

import actions.SessionRequest
import form.connectiontoproperty.AreYouThirdPartyForm
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

import scala.jdk.CollectionConverters.*

class AreYouThirdPartyViewSpec extends QuestionViewBehaviours[AnswersYesNo] {

  val messageKeyPrefix = "areYouThirdParty"

  override val form = AreYouThirdPartyForm.theForm

  val sessionRequest6048 = SessionRequest(baseFilled6048Session, fakeRequest)
  val sessionRequest6076 = SessionRequest(baseFilled6076Session, fakeRequest)
  val sessionRequest     = SessionRequest(baseFilled6010Session, fakeRequest)

  val backLink = controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url

  def createView = () =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetailsNoSession.toSummary, false)(using
      sessionRequest,
      messages
    )

  def createViewUsingForm = (form: Form[AnswersYesNo]) =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetailsNoSession.toSummary, false)(using
      sessionRequest,
      messages
    )

  def createView6076 = () =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetails6076YesSession.toSummary, false)(using
      sessionRequest6076,
      messages
    )

  def createViewUsingForm6076 = (form: Form[AnswersYesNo]) =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetails6076YesSession.toSummary, false)(using
      sessionRequest6076,
      messages
    )

  def createView6048 = () =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetails6048YesSession.toSummary, false)(using
      sessionRequest6048,
      messages
    )

  def createViewUsingForm6048 = (form: Form[AnswersYesNo]) =>
    areYouThirdPartyView(form, backLink, "Wombles Inc", stillConnectedDetails6048YesSession.toSummary, false)(using
      sessionRequest6048,
      messages
    )

  "Are you a third party view" must {

    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in
          checkServiceNameInHeaderBanner(createView())

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
            messages("service.title", messages("areYouThirdParty6076.heading"))
          )
        }

        "display language toggles" in {
          val doc        = asDocument(createView())
          val langToggle = doc.select(".hmrc-service-navigation-language-select")
          langToggle.toString should include("""href="/send-trade-and-cost-information/hmrc-frontend/language/cy"""")

          val langItems = langToggle.select(".hmrc-service-navigation-language-select__list-item").eachText().asScala
          langItems.size shouldBe 2
          langItems.head   should include("ENG")
          langItems(1)     should include("CYM")
        }
      }
    }

    "contain radio buttons for the value yes with hint 6076" in {
      val doc = asDocument(createViewUsingForm6076(form))
      assertContainsRadioButton(doc, "areYouThirdParty", "areYouThirdParty", AnswerYes.toString, false)
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no with hint 6076" in {
      val doc = asDocument(createViewUsingForm6076(form))
      assertContainsRadioButton(doc, "areYouThirdParty-2", "areYouThirdParty", AnswerNo.toString, false)
      assertContainsText(doc, messages("label.no"))
    }

    "contain list" in {
      val doc = asDocument(createViewUsingForm6076(form))
      assert(doc.toString.contains(messages("areYouThirdParty6076.l1")))
      assert(doc.toString.contains(messages("areYouThirdParty6076.l2")))
      assert(doc.toString.contains(messages("areYouThirdParty6076.l3")))
      assert(doc.toString.contains(messages("areYouThirdParty6076.l4")))

    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
