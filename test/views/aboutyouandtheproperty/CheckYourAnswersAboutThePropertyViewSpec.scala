/*
 * Copyright 2026 HM Revenue & Customs
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
import form.CheckYourAnswersAndConfirmForm
import models.pages.Summary
import models.submissions.common.CheckYourAnswersAndConfirm
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class CheckYourAnswersAboutThePropertyViewSpec extends QuestionViewBehaviours[CheckYourAnswersAndConfirm]:

  private val messageKeyPrefix = "checkYourAnswersAboutTheProperty"

  override val form: Form[CheckYourAnswersAndConfirm] = CheckYourAnswersAndConfirmForm.theForm

  private val backLink = controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url

  private val sessionRequest = SessionRequest(baseFilled6010Session, fakeRequest)

  private def createView = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996010001"))(using sessionRequest, messages)

  private def createView6020 = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996020001"))(
      using SessionRequest(baseFilled6020Session, fakeRequest),
      messages
    )

  private def createView6030 = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996030001"))(
      using SessionRequest(baseFilled6030Session, fakeRequest),
      messages
    )

  private def createView6048 = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996048002"))(
      using SessionRequest(baseFilled6048Session, fakeRequest),
      messages
    )

  private def createView6048Welsh = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996048001"))(
      using SessionRequest(baseFilled6048Session, fakeRequest),
      messages
    )

  private def createView6076 = () =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996076001"))(
      using SessionRequest(baseFilled6076Session, fakeRequest),
      messages
    )

  private def createViewUsingForm = (form: Form[CheckYourAnswersAndConfirm]) =>
    checkYourAnswersAboutThePropertyView(form, backLink, Summary("99996010001"))(using sessionRequest, messages)

  "Check Your Answers About The Property view" should {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the website for property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue-button").text()
      assert(loginButton == messages("button.continue.label"))
    }
  }

  "Answers About The Property component for 6020 type" should {
    "render the view correctly" in {
      val doc = asDocument(createView6020())
      doc.text()    should include(messages("checkYourAnswersAboutTheProperty.propertyUsage"))
      doc.text() shouldNot include(messages("checkYourAnswersAboutTheProperty.hasWebsite"))
    }
  }

  "Answers About The Property component for 6030 type" should {
    "render the view correctly" in {
      val doc = asDocument(createView6030())
      doc.text() should include(messages("checkYourAnswersAboutTheProperty.charity"))
    }
  }

  "Answers About The Property component for 6048 type" should {
    "render the view correctly" in {
      val doc = asDocument(createView6048())
      doc.text() should include(messages("checkYourAnswersAboutTheProperty.firstAvailable"))
      doc.text() should include(messages("checkYourAnswersAboutTheProperty.availabilityCommercial"))
      doc.text() should include(messages("checkYourAnswersAboutTheProperty.completedCommercial"))
    }
  }

  "Answers About The Property component for 6048 type Welsh" should {
    "render the view correctly" in {
      val doc = asDocument(createView6048Welsh())
      doc.text() should include(messages("checkYourAnswersAboutTheProperty.firstAvailable"))
      doc.text() should include(messages("checkYourAnswersAboutTheProperty.availabilityCommercial"))
      doc.text() should include(messages("checkYourAnswersAboutTheProperty.completedCommercial"))
    }
  }

  "Answers About The Property component for 6076 type" should {
    "render the h2 headers in component correctly" in {
      val doc = asDocument(createView6076())
      doc.text() should include(messages("checkYourAnswersAboutTheProperty.aboutYou.heading"))
      doc.text() should include(messages("checkYourAnswersAboutTheProperty.aboutProperty.technologyType"))
      doc.text() should include(messages("checkYourAnswersAboutTheProperty.aboutProperty.siteConstructionDtls"))
    }
  }
