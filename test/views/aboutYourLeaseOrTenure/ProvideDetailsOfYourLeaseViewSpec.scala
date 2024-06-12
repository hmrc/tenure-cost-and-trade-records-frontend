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

import form.aboutYourLeaseOrTenure.ProvideDetailsOfYourLeaseForm
import models.pages.Summary
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class ProvideDetailsOfYourLeaseViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "provideDetailsOfYourLease"

  override val form = ProvideDetailsOfYourLeaseForm.provideDetailsOfYourLeaseForm

  def createView = () => provideDetailsOfYourLeaseView(form, Summary("99996010001"))(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) =>
    provideDetailsOfYourLeaseView(form, Summary("99996010001"))(fakeRequest, messages)

  "Provide details of your lease view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show().url
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form)) // govuk-caption-m
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourLeaseOrTenure"))
    }

    "contain a heading for page" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(doc, messages("provideDetailsOfYourLease.heading"))
    }

    "contain a subheading for page" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(doc, messages("provideDetailsOfYourLease.p1"))
    }

    "contain a list for page" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(doc, messages("provideDetailsOfYourLease.list1"))
      assertContainsText(doc, messages("provideDetailsOfYourLease.list2"))
      assertContainsText(doc, messages("provideDetailsOfYourLease.list3"))
      assertContainsText(doc, messages("provideDetailsOfYourLease.list4"))
    }

    "contain an input for provideDetailsOfYourLease" in {
      val doc = asDocument(createViewUsingForm(form))
      assertRenderedById(doc, "provideDetailsOfYourLease")
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }
  }
}
