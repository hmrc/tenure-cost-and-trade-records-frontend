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

package views.aboutthetradinghistory

import actions.SessionRequest
import form.aboutthetradinghistory.WhatYouWillNeedForm
import models.pages.Summary
import org.scalatest.matchers.must.Matchers.*
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class aboutWhatYouWillNeedViewSpec extends QuestionViewBehaviours[String] {

  private val sessionRequest     = SessionRequest(baseFilled6076Session, fakeRequest)
  private val sessionRequest6010 = SessionRequest(baseFilled6010Session, fakeRequest)
  private val sessionRequest6015 = SessionRequest(baseFilled6015Session, fakeRequest)
  private val sessionRequest6016 = SessionRequest(baseFilled6016Session, fakeRequest)
  private val sessionRequest6045 = SessionRequest(baseFilled6045Session, fakeRequest)
  private val sessionRequest6048 = SessionRequest(baseFilled6048Session, fakeRequest)
  private val sessionRequest6076 = SessionRequest(baseFilled6076Session, fakeRequest)
  private val sessionRequest6030 = SessionRequest(baseFilled6030Session, fakeRequest)

  val messageKeyPrefix = "whatYouWillNeed"
  override val form    = WhatYouWillNeedForm.whatYouWillNeedForm

  def createView = () => whatYouWillNeedView(form, Summary("99996076001"))(sessionRequest, messages)

  def createView6010 = () => whatYouWillNeedView(form, Summary("99996010001"))(sessionRequest6010, messages)

  def createView6015 = () => whatYouWillNeedView(form, Summary("99996015001"))(sessionRequest6015, messages)

  def createView6016 = () => whatYouWillNeedView(form, Summary("99996016001"))(sessionRequest6016, messages)
  def createView6045 = () => whatYouWillNeedView(form, Summary("99996045001"))(sessionRequest6045, messages)
  def createView6048 = () => whatYouWillNeedView(form, Summary("99996048001"))(sessionRequest6048, messages)
  def createView6076 = () => whatYouWillNeedView(form, Summary("99996076001"))(sessionRequest6076, messages)
  def createView6030 = () => whatYouWillNeedView(form, Summary("99996030001"))(sessionRequest6030, messages)

  def createViewUsingForm = (form: Form[String]) =>
    whatYouWillNeedView(form, Summary("99996076001"))(sessionRequest, messages)

  "What you will need view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the task list Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("back.link.label")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe controllers.routes.TaskListController.show().url
    }

    "contain whatYouWillNeed.header1" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("whatYouWillNeed.header1")))
    }

    "contain whatYouWillNeed.text" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("whatYouWillNeed.text1")))
      assert(doc.toString.contains(messages("whatYouWillNeed.text2")))
      assert(doc.toString.contains(messages("whatYouWillNeed.text3")))
    }

    "contain list" in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("whatYouWillNeed.occupationAccountingInformation")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p1")))
      assert(doc.toString.contains(messages("whatYouWillNeed.powerGenerated")))
      assert(doc.toString.contains(messages("whatYouWillNeed.grossReceipts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p3")))
      assert(doc.toString.contains(messages("whatYouWillNeed.otherIncome")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p4")))
      assert(doc.toString.contains(messages("whatYouWillNeed.costOfSales")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p5")))
      assert(doc.toString.contains(messages("whatYouWillNeed.staffCosts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p6")))
      assert(doc.toString.contains(messages("whatYouWillNeed.premisesCosts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p7")))
      assert(doc.toString.contains(messages("whatYouWillNeed.operationalAdministrativeExpenses")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p8")))
      assert(doc.toString.contains(messages("whatYouWillNeed.headOfficeExpenses")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p9")))
    }

    "contain list 6010" in {
      val doc = asDocument(createView6010())
      assert(doc.toString.contains(messages("whatYouWillNeed.drinks")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6010.p1")))
      assert(doc.toString.contains(messages("whatYouWillNeed.food")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6010.p2")))
      assert(doc.toString.contains(messages("whatYouWillNeed.otherReceipts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6010.p3")))
      assert(doc.toString.contains(messages("whatYouWillNeed.turnover")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6010.p4")))
    }

    "contain list 6030" in {
      val doc = asDocument(createView6010())
      assert(doc.toString.contains(messages("whatYouWillNeed.drinks")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6010.p1")))
      assert(doc.toString.contains(messages("whatYouWillNeed.food")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6010.p2")))
      assert(doc.toString.contains(messages("whatYouWillNeed.otherReceipts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6010.p3")))
      assert(doc.toString.contains(messages("whatYouWillNeed.turnover")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6010.p4")))
    }

    "contain list 6015" in {
      val doc = asDocument(createView6015())
      assert(doc.toString.contains(messages("whatYouWillNeed.turnover")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6015.p1")))
      assert(doc.toString.contains(messages("whatYouWillNeed.costOfSales")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6015.p2")))
      assert(doc.toString.contains(messages("whatYouWillNeed.payrollCosts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6015.p3")))
      assert(doc.toString.contains(messages("whatYouWillNeed.variableOperatingExpenses")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6015.p4")))
      assert(doc.toString.contains(messages("whatYouWillNeed.fixedOperatingExpenses")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6015.p5")))
      assert(doc.toString.contains(messages("whatYouWillNeed.otherCosts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6015.p6")))
    }

    "contain list 6016" in {
      val doc = asDocument(createView6016())
      assert(doc.toString.contains(messages("whatYouWillNeed.accommodation")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6016.p1")))
      assert(doc.toString.contains(messages("whatYouWillNeed.drinks")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6010.p1")))
      assert(doc.toString.contains(messages("whatYouWillNeed.food")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6010.p2")))
      assert(doc.toString.contains(messages("whatYouWillNeed.otherReceipts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6016.p4")))
    }

    "contain list 6045" in {
      val doc = asDocument(createView6045())
      assert(doc.toString.contains(messages("whatYouWillNeed.staticCaravans")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6045.p2")))
      assert(doc.toString.contains(messages("whatYouWillNeed.incomeOtherHolidayAccommodation")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6045.p3")))
      assert(doc.toString.contains(messages("whatYouWillNeed.incomeTouringTenting")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6045.p4")))
      assert(doc.toString.contains(messages("whatYouWillNeed.tradingFiguresOtherActivities")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6045.p5")))
    }

    "contain list 6048" in {
      val doc = asDocument(createView6048())
      assert(doc.toString.contains(messages("whatYouWillNeed.income")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6048.p2")))
      assert(doc.toString.contains(messages("whatYouWillNeed.fixedCosts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6048.p3")))
      assert(doc.toString.contains(messages("whatYouWillNeed.costOfSales")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6048.p4")))
      assert(doc.toString.contains(messages("whatYouWillNeed.accountingCosts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6048.p5")))
      assert(doc.toString.contains(messages("whatYouWillNeed.administrativeCosts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6048.p6")))
      assert(doc.toString.contains(messages("whatYouWillNeed.operationalCosts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.6048.p7")))
    }

    "contain list 6076" in {
      val doc = asDocument(createView6076())
      assert(doc.toString.contains(messages("whatYouWillNeed.powerGenerated")))
      assert(doc.toString.contains(messages("whatYouWillNeed.grossReceipts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p3")))
      assert(doc.toString.contains(messages("whatYouWillNeed.otherIncome")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p4")))
      assert(doc.toString.contains(messages("whatYouWillNeed.costOfSales")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p5")))
      assert(doc.toString.contains(messages("whatYouWillNeed.staffCosts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p6")))
      assert(doc.toString.contains(messages("whatYouWillNeed.premisesCosts")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p7")))
      assert(doc.toString.contains(messages("whatYouWillNeed.operationalAdministrativeExpenses")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p8")))
      assert(doc.toString.contains(messages("whatYouWillNeed.headOfficeExpenses")))
      assert(doc.toString.contains(messages("whatYouWillNeed.p9")))
    }

    "Section heading is visible" in {
      val doc         = asDocument(createViewUsingForm(form))
      val sectionText = doc.getElementsByClass("govuk-caption-m").text()
      assert(sectionText == messages("label.section.aboutYourTradingHistory"))
    }

    "contain continue button with the value Continue" in {
      val doc         = asDocument(createViewUsingForm(form))
      val loginButton = doc.getElementById("continue").text()
      assert(loginButton == messages("button.label.continue"))
    }

  }
}
