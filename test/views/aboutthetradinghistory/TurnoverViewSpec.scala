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

class TurnoverViewSpec extends QuestionViewBehaviours[AboutYourTradingHistory] {
  // NOTE: this is a holding view test until the turnover page is implemented
  def turnoverView = app.injector.instanceOf[views.html.aboutthetradinghistory.turnover]

  val messageKeyPrefix = "turnover"

  override val form = AboutYourTradingHistoryForm.aboutYourTradingHistoryForm

  def createView = () => turnoverView()(fakeRequest, messages)

  def createViewUsingForm = (form: Form[AboutYourTradingHistory]) => turnoverView()(fakeRequest, messages)

  "Turnover view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.aboutthetradinghistory.routes.AboutYourTradingHistoryController.show.url
    }

    "contain save and continue button with the value Save and Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

    "contain get help section general guidance" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverGeneralHeader")))
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverGeneral")))
    }

    "contain get help section financial year end" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverFinancialHeader")))
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverFinancial")))
    }

    "contain get help section trading period" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverTradingHeader")))
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverTrading")))
    }

    "contain get help section alcoholic drinks" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverAlcoholHeader")))
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverAlcohol")))
    }

    "contain get help section food" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverFoodHeader")))
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverFood")))
    }

    "contain get help section other receipts" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverOtherReceiptsHeader")))
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverOtherReceipts")))
    }

    "contain get help section accommodation" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverAccommodationHeader")))
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverAccommodation")))
    }

    "contain get help section average occupancy rate" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverAverageOccupancyHeader")))
      assert(doc.toString.contains(messages("turnover.helpWithServiceTurnoverAverageOccupancy")))
    }
//    turnover.helpWithServiceTurnoverGeneralHeader = General guidance
//      turnover.helpWithServiceTurnoverGeneral = Do not include VAT in any of the figures you declare on this page. If you have not broken down your turnover into separate categories, or if you have recorded certain categories together – such as all food and drinks – you can declare them together in a single field.
//      turnover.helpWithServiceTurnoverFinancialHeader = Financial year end
//    turnover.helpWithServiceTurnoverFinancial = Your financial year end dates are pre-populated by the system. If your financial year end has changed during the period shown, you can edit these dates accordingly.
//    turnover.helpWithServiceTurnoverTradingHeader = Trading period
//      turnover.helpWithServiceTurnoverTrading = If your accounts do not relate to a whole year, or if you were not trading continuously – if you were closed for refurbishment, for example – state the number of weeks you were trading.
//    turnover.helpWithServiceTurnoverAlcoholHeader = Alcoholic drinks
//      turnover.helpWithServiceTurnoverAlcohol = Use this row to declare your annual turnover from the sales of all alcoholic drinks, soft drinks, and snacks like crisps or nuts.
//    turnover.helpWithServiceTurnoverFoodHeader = Food
//    turnover.helpWithServiceTurnoverFood = If you first occupied the property within the last 3 years, you’ll be asked for details of any full years’ completed accounts you have. You’ll also need to provide details from the current financial year.
//      turnover.helpWithServiceTurnoverOtherReceiptsHeader = Other receipts
//      turnover.helpWithServiceTurnoverOtherReceipts = Use this row to declare your annual turnover from all other sources not listed here. This could include, but is not limited to, admission charges, camping pitch fees or function room hire.
//      turnover.helpWithServiceTurnoverAccommodationHeader = Accommodation
//    turnover.helpWithServiceTurnoverAccommodation = Use this row to declare your annual turnover if you provide accommodation for guests.
//      turnover.helpWithServiceTurnoverAverageOccupancyHeader = Average occupancy rate
//    turnover.helpWithServiceTurnoverAverageOccupancy

    "contain get help section basic details" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("common.helpWithServiceHeader")))
      assert(doc.toString.contains(messages("common.helpWithService")))
    }
  }
}
