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

import form.Form6010.WebsiteForPropertyForm
import models.submissions.Form6010._
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class WebsiteForPropertyViewSpec extends QuestionViewBehaviours[WebsiteForPropertyDetails] {

  def websiteForPropertyView = app.injector.instanceOf[views.html.form.websiteForProperty]

  val messageKeyPrefix = "aboutProperty"

  override val form = WebsiteForPropertyForm.websiteForPropertyForm

  def createView = () => websiteForPropertyView(form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[WebsiteForPropertyDetails]) =>
    websiteForPropertyView(form)(fakeRequest, messages)

  "Property website view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.routes.TaskListController.show().url
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "buildingOperatingHaveAWebsite",
        "buildingOperatingHaveAWebsite",
        BuildingOperationHaveAWebsiteYes.name,
        false
      )
      assertContainsText(doc, messages("buildingOperatingHaveAWebsite.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "buildingOperatingHaveAWebsite-2",
        "buildingOperatingHaveAWebsite",
        BuildingOperationHaveAWebsiteNo.name,
        false
      )
      assertContainsText(doc, messages("buildingOperatingHaveAWebsite.no"))
    }

    "contain an input for websiteAddressForProperty" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "websiteAddressForProperty")
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain get help section use of website details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("website.helpWithServiceWebsiteHeader")))
      assert(doc.toString.contains(messages("website.helpWithServiceWebsite")))
    }

    "contain get help section basic details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithServiceHeader")))
      assert(doc.toString.contains(messages("common.helpWithService")))
    }
  }
}
