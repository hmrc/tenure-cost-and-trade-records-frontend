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

import form.aboutyouandtheproperty.LicensableActivitiesInformationForm
import models.pages.Summary
import models.submissions.aboutyouandtheproperty.LicensableActivitiesInformationDetails
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours

class LicenceActivitiesDetailsViewSpec extends QuestionViewBehaviours[LicensableActivitiesInformationDetails] {

  val messageKeyPrefix = "licensableActivitiesDetails"

  override val form: Form[LicensableActivitiesInformationDetails] =
    LicensableActivitiesInformationForm.licensableActivitiesDetailsForm

  def createView: () => Html = () =>
    licensableActivitiesDetailsView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm: Form[LicensableActivitiesInformationDetails] => Html =
    (form: Form[LicensableActivitiesInformationDetails]) =>
      licensableActivitiesDetailsView(form, Summary("99996010001"))(fakeRequest, messages)

  "Licence Activities details view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the licence activities Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutTheProperty"))
    }

    "contain an input for licensableActivitiesDetails" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "licensableActivitiesDetails")
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

    "contain get help section" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("help.licensableActivitiesDetails.title")))
      assert(doc.toString.contains(messages("help.licensableActivitiesDetails.heading")))
      assert(doc.toString.contains(messages("help.licensableActivitiesDetails.p1")))
      assert(doc.toString.contains(messages("help.licensableActivitiesDetails.list.p1")))
      assert(doc.toString.contains(messages("help.licensableActivitiesDetails.list.p2")))
      assert(doc.toString.contains(messages("help.licensableActivitiesDetails.list.p3")))
    }
  }
}
