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

package util

import actions.SessionRequest
import models.ForType
import models.ForType.*
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne, CostOfSales, TurnoverSection}
import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.Html
import controllers.toOpt

import java.time.LocalDate
import uk.gov.hmrc.govukfrontend.views.html.components.*
import util.NumberUtil.*
import util.CyaTradingHistorySupport
import javax.inject.Inject

class CyaTradingHistorySupport @Inject() (
  val govukSummaryList: GovukSummaryList,
  val dateUtil: DateUtilLocalised,
  val accessibilityUtil: AccessibilityUtil
) {

  def tradingPeriodWithWeeks(tradingPeriod: Int)(using messages: Messages): String =
    val weeksSuffix = if tradingPeriod == 1 then "turnover.week" else "turnover.weeks"
    s"$tradingPeriod ${messages(weeksSuffix)}"

  def sectionAnswers(using
    request: SessionRequest[?],
    messages: Messages
  ): SectionAnswersRowBuilder[AboutTheTradingHistory] =
    SectionAnswersRowBuilder(request.sessionData.aboutTheTradingHistory)

  def sectionAnswers1(using
    request: SessionRequest[?],
    messages: Messages
  ): SectionAnswersRowBuilder[AboutTheTradingHistoryPartOne] =
    SectionAnswersRowBuilder(request.sessionData.aboutTheTradingHistoryPartOne)

  def forType(using request: SessionRequest[?]): ForType = request.sessionData.forType

  def table6045(valuesGrid: Seq[Seq[String]]): Html = Html(
    valuesGrid.map { values =>
      s"""<div class="hmrc-turnover-table-column">
       ${values.map(value => s"<p class=\"govuk-body\">$value</p>").mkString}
       </div>"""
    }.mkString
  )

  def table(valuesGrid: Seq[Seq[String]], financialYearEnds: Seq[LocalDate], messageKeys: Option[Seq[String]] = None)(
    using messages: Messages
  ): Html =
    Html(
      valuesGrid
        .zip(financialYearEnds)
        .map { case (values, yearEnd) =>
          s"""<div class="hmrc-turnover-table-column">
           ${messageKeys.fold {
              values.map { value =>
                s"""<p class="govuk-body">$value</p>"""
              }.mkString
            } { keys =>
              values
                .zip(keys)
                .map { case (value, key) =>
                  s"""<p class="govuk-body" aria-label="${accessibilityUtil.ariaBuilder(key, yearEnd)}">$value</p>"""
                }
                .mkString
            }}
           </div>"""
        }
        .mkString
    )

  def tableRow(values: Seq[String]): Html = Html(
    values.map { value =>
      s"""<div class="hmrc-turnover-table-column">$value</div>"""
    }.mkString
  )

  def optionalParagraph(textOpt: Option[String]): Html = Html(
    textOpt.filterNot(_.trim.isEmpty).fold("") { text =>
      s"""<p class="govuk-body">$text</p>"""
    }
  )

  def answerOpt(
    valueLabelKey: String,
    valueOpt: Option[String],
    classes: String = "no-border-bottom"
  )(using messages: Messages): SummaryListRow =
    answer(valueLabelKey, Seq(valueOpt.getOrElse("")), None, classes)

  def answer(
    valueLabelKey: String,
    values: Seq[String],
    additionalParagraph: Option[String] = None,
    classes: String = "no-border-bottom"
  )(using messages: Messages): SummaryListRow =
    SummaryListRow(
      key = Key(Text(messages(valueLabelKey))),
      value = Value(
        HtmlContent(
          s"${tableRow(values).body}${optionalParagraph(additionalParagraph).body}"
        )
      ),
      classes = classes
    )

  def yearEndChanged(using request: SessionRequest[?], messages: Messages): Boolean =
    sectionAnswers.answers
      .flatMap(_.occupationAndAccountingInformation)
      .flatMap(_.financialYearEndHasChanged)
      .contains(true)

  def financialYearEndDatesTable(
    financialYearEndDates: Seq[LocalDate],
    dateUtil: DateUtilLocalised
  )(using messages: Messages): Html =
    table(
      financialYearEndDates.map(financialYearEnd => Seq(dateUtil.formatDayMonthAbbrYear(financialYearEnd))),
      financialYearEndDates
    )

  def financialYearEndDates(
    aboutTheTradingHistory: AboutTheTradingHistory
  )(using request: SessionRequest[?]): Seq[LocalDate] =
    forType match {
      case FOR6020           => aboutTheTradingHistory.turnoverSections6020.getOrElse(Seq.empty).map(_.financialYearEnd)
      case FOR6030           => aboutTheTradingHistory.turnoverSections6030.map(_.financialYearEnd)
      case FOR6045 | FOR6046 => request.sessionData.financialYearEndDates6045.map(_._1)
      case FOR6048           => request.sessionData.financialYearEndDates6048.map(_._1)
      case FOR6076           => request.sessionData.financialYearEndDates6076.map(_._1)
      case _                 => aboutTheTradingHistory.turnoverSections.map(_.financialYearEnd)
    }

  def actions(using messages: Messages): Option[Actions] =
    Option(
      Actions(items =
        Seq(
          ActionItem(
            href = s"${controllers.aboutthetradinghistory.routes.TurnoverController.show().url}?from=CYA",
            content = Text(messages("label.change")),
            visuallyHiddenText = Option(messages("turnover.heading")),
            attributes = Map(
              "aria-label" -> s"${messages("label.change")} ${messages("turnover.heading")}"
            )
          )
        )
      )
    )

  def financialYearEndRow(
    pageHeadingKey: String,
    editPage: Call
  )(using
    request: SessionRequest[?],
    messages: Messages
  ): SummaryListRow =
    SummaryListRow(
      key = Key(Text(messages("turnover.financialYearEnd"))),
      value = Value(
        HtmlContent(
          tableRow(
            sectionAnswers.answers.map(financialYearEndDates).fold(Seq(""))(_.map(dateUtil.formatDayMonthAbbrYear))
          ).body
        )
      ),
      actions = Option(
        Actions(items =
          Seq(
            ActionItem(
              href = s"$editPage?from=CYA",
              content = Text(messages("label.change")),
              visuallyHiddenText = Option(messages(pageHeadingKey)),
              attributes = Map(
                "aria-label" -> s"${messages("label.change")} ${messages(pageHeadingKey)}"
              )
            )
          )
        )
      ),
      classes = "no-border-bottom"
    )

  def pageAnswers(
    pageHeadingKey: String,
    editPage: Call,
    answerRows: SummaryListRow*
  )(using
    request: SessionRequest[?],
    messages: Messages
  ): Html = {
    val rows = financialYearEndRow(pageHeadingKey, editPage) +: answerRows.init :+ answerRows.last.copy(classes = "")

    Html(
      s"""<h2 class="govuk-heading-m">${messages(pageHeadingKey)}</h2>
         ${govukSummaryList(SummaryList(rows = rows)).body}""".stripMargin
    )
  }

  def costOfSalesValuesTable(costOfSales: Seq[CostOfSales])(using messages: Messages): String =
    table(
      valuesGrid = costOfSales.map(s =>
        Seq(
          dateUtil.formatDayMonthAbbrYear(s.financialYearEnd),
          s.accommodation.getOrElse(zeroBigDecimal).asMoney,
          s.food.getOrElse(zeroBigDecimal).asMoney,
          s.drinks.getOrElse(zeroBigDecimal).asMoney,
          s.other.getOrElse(zeroBigDecimal).asMoney,
          s.total.asMoney
        )
      ),
      financialYearEnds = costOfSales.map(_.financialYearEnd),
      messageKeys = Option(costOfSalesMessageKeys)
    ).body

  private def costOfSalesMessageKeys(using messages: Messages): Seq[String] =
    Seq(
      messages("checkYourAnswersAboutTheTradingHistory.financialYearEnd"),
      messages("checkYourAnswersAboutTheTradingHistory.accommodation"),
      messages("checkYourAnswersAboutTheTradingHistory.food"),
      messages("checkYourAnswersAboutTheTradingHistory.drinks"),
      messages("checkYourAnswersAboutTheTradingHistory.other"),
      messages("costOfSales.total")
    )

  def costOfSalesKeys(using messages: Messages): String =
    costOfSalesMessageKeys.mkString(
      """<p class="govuk-body govuk-!-font-weight-bold">""",
      """</p> <p class="govuk-body govuk-!-font-weight-bold">""",
      "</p>"
    )

  private def turnoverMessageKeys60156016(using messages: Messages): Seq[String] =
    Seq(
      messages("checkYourAnswersAboutTheTradingHistory.financialYearEnd"),
      messages("checkYourAnswersAboutTheTradingHistory.tradingPeriod"),
      messages("checkYourAnswersAboutTheTradingHistory.accommodation"),
      messages("checkYourAnswersAboutTheTradingHistory.averageOccupancyRate"),
      messages("checkYourAnswersAboutTheTradingHistory.food"),
      messages("checkYourAnswersAboutTheTradingHistory.drinks"),
      messages("checkYourAnswersAboutTheTradingHistory.otherReceipts"),
      messages("checkYourAnswersAboutTheTradingHistory.totalSalesRevenue")
    )

  def turnoverKeys60156016(using messages: Messages): String =
    turnoverMessageKeys60156016.mkString(
      """<p class="govuk-body govuk-!-font-weight-bold">""",
      """</p> <p class="govuk-body govuk-!-font-weight-bold">""",
      "</p>"
    )

  def turnoverValuesTable60156016(turnoverSections: Seq[TurnoverSection])(using messages: Messages): String =
    table(
      valuesGrid = turnoverSections.map(t =>
        Seq(
          dateUtil.formatDayMonthAbbrYear(t.financialYearEnd),
          s"${t.tradingPeriod} ${messages("turnover.weeks")}",
          t.accommodation.getOrElse(zeroBigDecimal).asMoney,
          t.averageOccupancyRate.getOrElse(zeroBigDecimal).withScale(2) + "%",
          t.food.getOrElse(zeroBigDecimal).asMoney,
          t.alcoholicDrinks.getOrElse(zeroBigDecimal).asMoney,
          t.otherReceipts.getOrElse(zeroBigDecimal).asMoney,
          t.total.asMoney
        )
      ),
      financialYearEnds = turnoverSections.map(_.financialYearEnd),
      messageKeys = Some(turnoverMessageKeys60156016)
    ).body

  def accountingInformation(using
    request: SessionRequest[?],
    messages: Messages
  ): Html =
    Html(
      govukSummaryList(
        SummaryList(rows =
          sectionAnswers1
            .conditionRow(
              _.areYouVATRegistered.isDefined,
              "checkYourAnswersAboutTheTradingHistory.areYouVATRegistered",
              _.areYouVATRegistered.map(yesNo => messages(s"label.$yesNo")),
              controllers.aboutthetradinghistory.routes.AreYouVATRegisteredController.show,
              "areYouVatRegistered"
            )
            .map(_.copy(classes = "no-border-bottom"))
            ++
              (if (forType != FOR6048) {
                 sectionAnswers
                   .row(
                     messageKey = "checkYourAnswersAboutTheTradingHistory.occupationDate",
                     getAnswerValue = _.occupationAndAccountingInformation
                       .map(_.firstOccupy.toYearMonth)
                       .map(dateUtil.formatYearMonth),
                     editPage = controllers.aboutthetradinghistory.routes.ChangeOccupationAndAccountingController.show,
                     editField = ""
                   )
                   .map(_.copy(classes = "no-border-bottom"))
               } else {
                 None
               }) ++
              answerOpt(
                valueLabelKey = "checkYourAnswersAboutTheTradingHistory.financialYearEnd",
                valueOpt = sectionAnswers.answers
                  .flatMap(_.occupationAndAccountingInformation)
                  .flatMap(_.currentFinancialYearEnd.map(_.toMonthDay))
                  .map(dateUtil.formatMonthDay),
                classes = if (!yearEndChanged) "" else "no-border-bottom"
              ) ++
              (if (yearEndChanged)
                 Seq(
                   SummaryListRow(
                     key = Key(Text(messages("checkYourAnswersAboutTheTradingHistory.financialYearEndUpdates"))),
                     value = Value(
                       HtmlContent(
                         financialYearEndDatesTable(
                           financialYearEndDates = financialYearEndDates(sectionAnswers.answers.get),
                           dateUtil = dateUtil
                         ).body
                       )
                     )
                   )
                 )
               else Seq.empty)
        )
      ).body
    )

  def occupationDetails(using
    request: SessionRequest[?],
    messages: Messages
  ): Html =
    govukSummaryList(
      SummaryList(rows =
        sectionAnswers.row(
          "checkYourAnswersAboutTheTradingHistory.occupationDate",
          _.occupationAndAccountingInformation.map(_.firstOccupy.toYearMonth).map(dateUtil.formatYearMonth),
          controllers.aboutthetradinghistory.routes.WhenDidYouFirstOccupyController.show(),
          "firstOccupy.month"
        ) ++
          sectionAnswers.row(
            "checkYourAnswersAboutTheTradingHistory.financialYearEnd",
            _.occupationAndAccountingInformation
              .flatMap(_.currentFinancialYearEnd.map(_.toMonthDay))
              .map(dateUtil.formatMonthDay),
            controllers.aboutthetradinghistory.routes.FinancialYearEndController.show(),
            "financialYear.day"
          ) ++
          sectionAnswers.row(
            "checkYourAnswersAboutTheTradingHistory.financialYearEndUpdates",
            _.fold("")(t => financialYearEndDatesTable(financialYearEndDates(t), dateUtil).body),
            forType match {
              case FOR6020 | FOR6030 | FOR6076 =>
                controllers.aboutthetradinghistory.routes.FinancialYearEndDatesSummaryController.show()
              case _                           => controllers.aboutthetradinghistory.routes.FinancialYearEndDatesController.show()
            },
            "",
            ("valueAsHtml", _ => None)
          )
      )
    )
}
