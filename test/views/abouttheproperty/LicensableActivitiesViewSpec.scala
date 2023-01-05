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

import form.Form6010.LicensableActivitiesForm
import models.submissions.Form6010.{BuildingOperationHaveAWebsiteNo, BuildingOperationHaveAWebsiteYes, LicensableActivities}
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class LicensableActivitiesViewSpec extends QuestionViewBehaviours[LicensableActivities] {

  def licencableActivitiesView = app.injector.instanceOf[views.html.form.licensableActivities]

  val messageKeyPrefix = "licensableActivities"

  override val form = LicensableActivitiesForm.licensableActivitiesForm

  def createView = () => licencableActivitiesView(form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[LicensableActivities]) => licencableActivitiesView(form)(fakeRequest, messages)

  "Property licence activities view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the website for property Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.Form6010.routes.WebsiteForPropertyController.show().url
    }

    "contain radio buttons for the value yes" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "licensableActivities",
        "licensableActivities",
        BuildingOperationHaveAWebsiteYes.name,
        false
      )
      assertContainsText(doc, messages("label.yes"))
    }

    "contain radio buttons for the value no" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsRadioButton(
        doc,
        "licensableActivities-2",
        "licensableActivities",
        BuildingOperationHaveAWebsiteNo.name,
        false
      )
      assertContainsText(doc, messages("label.no"))
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain get help section use of licence activities details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("licensableActivities.helpWithServiceLicensableActivitiesHeader")))
      assert(doc.toString.contains(messages("licensableActivities.helpWithServiceLicensableActivities")))
      assert(doc.toString.contains(messages("licensableActivities.listBlock1.p1")))
      assert(doc.toString.contains(messages("licensableActivities.listBlock1.p2")))
      assert(doc.toString.contains(messages("licensableActivities.listBlock1.p3")))
      assert(doc.toString.contains(messages("licensableActivities.inThisInstance")))
      assert(doc.toString.contains(messages("licensableActivities.lateNight")))
      assert(doc.toString.contains(messages("licensableActivities.regulated")))
      assert(doc.toString.contains(messages("licensableActivities.listBlock2.p1")))
      assert(doc.toString.contains(messages("licensableActivities.listBlock2.p2")))
      assert(doc.toString.contains(messages("licensableActivities.listBlock2.p3")))
      assert(doc.toString.contains(messages("licensableActivities.listBlock2.p4")))
      assert(doc.toString.contains(messages("licensableActivities.listBlock2.p5")))
      assert(doc.toString.contains(messages("licensableActivities.listBlock2.p6")))
      assert(doc.toString.contains(messages("licensableActivities.listBlock2.p7")))
    }

    "contain get help section basic details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithServiceHeader")))
      assert(doc.toString.contains(messages("common.helpWithService")))
    }
  }
}
