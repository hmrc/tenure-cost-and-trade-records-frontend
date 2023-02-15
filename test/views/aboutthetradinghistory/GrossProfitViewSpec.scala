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

package views.aboutthetradinghistory

import form.aboutthetradinghistory.AboutYourTradingHistoryForm
import models.submissions.aboutthetradinghistory.AboutYourTradingHistory
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class GrossProfitsViewSpec extends QuestionViewBehaviours[AboutYourTradingHistory] {
  // NOTE: this is a holding view test until the gross profit page is implemented
  def grossProfitView = app.injector.instanceOf[views.html.aboutthetradinghistory.grossProfits]

  val messageKeyPrefix = "grossProfit"

  override val form = AboutYourTradingHistoryForm.aboutYourTradingHistoryForm

  def createView = () => grossProfitView()(fakeRequest, messages)

  def createViewUsingForm = (form: Form[AboutYourTradingHistory]) => grossProfitView()(fakeRequest, messages)

  "grossProfits view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutthetradinghistory.routes.CostOfSalesOrGrossProfitDetailsController.show.url
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain get help section" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("helpWithService.title")))
    }

    "contain get help section basic details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithServiceHeader")))
      assert(doc.toString.contains(messages("common.helpWithService")))
    }
  }
}
