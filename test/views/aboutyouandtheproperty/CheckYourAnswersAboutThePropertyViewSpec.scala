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

package views.aboutyouandtheproperty

import actions.SessionRequest
import form.aboutyouandtheproperty.CheckYourAnswersAboutThePropertyForm
import models.pages.Summary
import models.submissions.aboutyouandtheproperty.CheckYourAnswersAboutYourProperty
import org.scalatest.matchers.must.Matchers.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersAboutThePropertyViewSpec extends QuestionViewBehaviours[CheckYourAnswersAboutYourProperty] {

  val messageKeyPrefix = "checkYourAnswersAboutTheProperty"

  override val form = CheckYourAnswersAboutThePropertyForm.checkYourAnswersAboutThePropertyForm

  val backLink = controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url

  val sessionRequest = SessionRequest(baseFilled6010Session, fakeRequest)

  def createView = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996010001"))(sessionRequest, messages)

  def createView6020 = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996020001"))(
      SessionRequest(baseFilled6020Session, fakeRequest),
      messages
    )

  def createView6030 = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996030001"))(
      SessionRequest(baseFilled6030Session, fakeRequest),
      messages
    )

  def createView6048 = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996048002"))(
      SessionRequest(baseFilled6048Session, fakeRequest),
      messages
    )

  def createView6048Welsh = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996048001"))(
      SessionRequest(baseFilled6048Session, fakeRequest),
      messages
    )

  def createView6076 = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996076001"))(
      SessionRequest(baseFilled6076Session, fakeRequest),
      messages
    )

  def createViewUsingForm = (form: Form[CheckYourAnswersAboutYourProperty]) =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996010001"))(sessionRequest, messages)

  "Check Your Answers About The Property view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the website for property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
    "Answers About The Property component for 6020 type" must {

      "render the view correctly" in {
        val doc = asDocument(createView6020())
        doc.text()    should include(messages("checkYourAnswersAboutTheProperty.propertyUsage"))
        doc.text() shouldNot include(messages("checkYourAnswersAboutTheProperty.hasWebsite"))
      }
    }
    "Answers About The Property component for 6030 type" must {

      "render the view correctly" in {
        val doc = asDocument(createView6030())
        doc.text() should include(messages("checkYourAnswersAboutTheProperty.charity"))
      }
    }

    "Answers About The Property component for 6048 type" must {

      "render the view correctly" in {
        val doc = asDocument(createView6048())
        doc.text() should include(messages("checkYourAnswersAboutTheProperty.firstAvailable"))
        doc.text() should include(messages("checkYourAnswersAboutTheProperty.availabilityCommercial"))
        doc.text() should include(messages("checkYourAnswersAboutTheProperty.completedCommercial"))
      }
    }

    "Answers About The Property component for 6048 type Welsh" must {

      "render the view correctly" in {
        val doc = asDocument(createView6048Welsh())
        doc.text() should include(messages("checkYourAnswersAboutTheProperty.firstAvailable"))
        doc.text() should include(messages("checkYourAnswersAboutTheProperty.availabilityCommercial"))
        doc.text() should include(messages("checkYourAnswersAboutTheProperty.completedCommercial"))
      }
    }

    "Answers About The Property component for 6076 type" must {

      "render the h2 headers in component correctly" in {
        val doc = asDocument(createView6076())
        assert(
          doc
            .select("h2:nth-child(3)")
            .text()
            .contains(messages("checkYourAnswersAboutTheProperty.aboutYou.heading"))
        )
        assert(
          doc
            .select("h2:nth-child(5)")
            .text()
            .contains(messages("checkYourAnswersAboutTheProperty.aboutProperty.technologyType"))
        )
        assert(
          doc
            .select("h2:nth-child(7)")
            .text()
            .contains(messages("checkYourAnswersAboutTheProperty.aboutProperty.siteConstructionDtls"))
        )
        assert(
          doc
            .select("h2:nth-child(9)")
            .text()
            .contains(messages("checkYourAnswersAboutTheProperty.aboutProperty.heading"))
        )

      }

    }
  }
}
