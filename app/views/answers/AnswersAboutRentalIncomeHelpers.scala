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

package views.answers

import actions.SessionRequest
import controllers.aboutfranchisesorlettings.routes
import models.submissions.aboutfranchisesorlettings.{CalculatingTheRent, Concession6015IncomeRecord, ConcessionIncomeRecord, FranchiseIncomeRecord, IncomeRecord, LettingIncomeRecord, PropertyRentDetails, RentReceivedFrom}
import models.ForType.FOR6030
import play.api.i18n.Messages
import util.DateUtilLocalised
import util.NumberUtil.*
import views.CheckYourAnswersHelpers.*
import views.includes.cards.{CardData, CardEntry}

object AnswersAboutRentalIncomeHelpers:

  private def annualRentCardEntry(
    maybePropertyRentDetails: Option[PropertyRentDetails],
    index: Int,
    dateUtil: DateUtilLocalised
  )(using messages: Messages) =
    CardEntry(
      label = messages("checkYourAnswersAboutFranchiseOrLettings.annualRent"),
      value = maybePropertyRentDetails.fold("")(propertyRentDetails =>
        s"""
             |<p class='govuk-body'>
             |${propertyRentDetails.annualRent.asMoney}<br>
             |${messages(
            s"checkYourAnswersAboutFranchiseOrLettings.dateSumFixed",
            dateUtil.formatDayMonthAbbrYear(propertyRentDetails.dateInput)
          )}
             |</p>
             |""".stripMargin
      ),
      changeAction = Some(routes.RentalIncomeRentController.show(index).asChangeLink("rentReceived"))
    )

  private def rentReceivedCardEntry(
    maybeRentReceivedFrom: Option[RentReceivedFrom],
    index: Int
  )(using messages: Messages) =
    CardEntry(
      label = messages("checkYourAnswersAboutFranchiseOrLettings.annualRent"),
      value = maybeRentReceivedFrom.fold("")(propertyRentDetails => s"""
           |<p class='govuk-body'>
           |${propertyRentDetails.annualRent.asMoney}<br>
           |${messages("checkYourAnswersAboutFranchiseOrLettings.rentIncludedInTurnover")}
           |</p>
           |""".stripMargin),
      changeAction = Some(routes.RentReceivedFromController.show(index).asChangeLink("annualRent"))
    )

  private def howRentWasCalculatedCardEntry(
    calculatingTheRent: Option[CalculatingTheRent],
    index: Int,
    dateUtil: DateUtilLocalised
  )(using messages: Messages) =
    CardEntry(
      label = messages("checkYourAnswersAboutFranchiseOrLettings.howRentWasCalculated"),
      value = calculatingTheRent.fold("")(calculatingTheRent =>
        s"""
           |<p class='govuk-body'>
           |${calculatingTheRent.description.escapedHtml}<br>
           |${messages(
            s"checkYourAnswersAboutFranchiseOrLettings.dateSumFixed",
            dateUtil.formatDayMonthAbbrYear(calculatingTheRent.dateInput)
          )}
           |</p>
           |""".stripMargin
      ),
      // TODO changeAction = None,
      changeAction = Some(routes.CalculatingTheRentForController.show(index).asChangeLink("annualRent"))
    )

  private def itemsIncludedCardEntry(maybeItemsIncluded: Option[List[String]], index: Int)(using
    messages: Messages
  ) =
    CardEntry(
      label = messages("checkYourAnswersAboutFranchiseOrLettings.itemsInRent"),
      value = maybeItemsIncluded
        .fold(List.empty)(_.filterNot(_ == "noneOfThese"))
        .filter(_.nonEmpty)
        .map(item => messages(s"checkbox.cateringOperationOrLettingAccommodationCheckboxesDetails.$item"))
        .mkString("""<p class="govuk-body">""", "<br>", "</p>"),
      changeAction = Some(routes.RentalIncomeIncludedController.show(index).asChangeLink("rentReceived"))
    )

  private def franchiseCardsData(
    record: FranchiseIncomeRecord,
    businessCounter: Int,
    index: Int,
    dateUtil: DateUtilLocalised
  )(using
    messages: Messages
  ) =
    CardData(
      index = businessCounter,
      label = messages("checkYourAnswersAboutFranchiseOrLettings.business"),
      removeAction = routes.RentalIncomeListController.remove(index).withFromCheckYourAnswer("rentReceived"),
      entries = Seq(
        CardEntry(
          label = messages("checkYourAnswersAboutFranchiseOrLettings.businessOperatorName"),
          value = record.businessDetails.fold("") { businessDetails =>
            s"""
                 |<p class="govuk-body">
                 |${businessDetails.operatorName}<br>
                 |${businessDetails.typeOfBusiness}<br>
                 |${businessDetails.cateringAddress.map(_.escapedHtml).getOrElse("")}
                 |</p>
                 |""".stripMargin
          },
          changeAction = Some(routes.FranchiseTypeDetailsController.show(index).asChangeLink("rentReceived"))
        ),
        annualRentCardEntry(record.rent, index, dateUtil),
        itemsIncludedCardEntry(record.itemsIncluded, index)
      )
    )

  private def lettingCardsData(
    record: LettingIncomeRecord,
    lettingCounter: Int,
    index: Int,
    dateUtil: DateUtilLocalised
  )(using
    messages: Messages
  ) =
    CardData(
      index = lettingCounter,
      label = messages("checkYourAnswersAboutFranchiseOrLettings.letting"),
      removeAction = routes.RentalIncomeListController.remove(index).withFromCheckYourAnswer("rentReceived"),
      entries = Seq(
        CardEntry(
          label = messages("checkYourAnswersAboutFranchiseOrLettings.lettingOperatorName"),
          value = record.operatorDetails.fold("") { operatorDetails =>
            s"""
                 |<p class="govuk-body">
                 |${operatorDetails.operatorName}<br>
                 |${operatorDetails.typeOfBusiness}<br>
                 |${operatorDetails.lettingAddress.map(_.escapedHtml).getOrElse("")}
                 |</p>
                 |""".stripMargin
          },
          changeAction = Some(routes.LettingTypeDetailsController.show(index).asChangeLink("lettingOperatorName"))
        ),
        annualRentCardEntry(record.rent, index, dateUtil),
        itemsIncludedCardEntry(record.itemsIncluded, index)
      )
    )

  private def tableRow(values: Seq[String]) =
    values.map { value =>
      s""" <div class="hmrc-turnover-table-column"> $value </div> """
    }.mkString

  private def concessionCardsData(
    record: ConcessionIncomeRecord,
    businessCounter: Int,
    index: Int,
    dateUtil: DateUtilLocalised
  )(using
    request: SessionRequest[?],
    messages: Messages
  ) =
    CardData(
      index = businessCounter,
      label = messages("checkYourAnswersAboutFranchiseOrLettings.business"),
      removeAction = routes.RentalIncomeListController.remove(index).withFromCheckYourAnswer("rentReceived"),
      entries = Seq(
        CardEntry(
          label = messages("cya.concessionOrFranchise.operatorDetails"),
          value = record.businessDetails.fold("") { businessDetails =>
            s"""
               |<p class="govuk-body">${businessDetails.operatorName}<br>
               |${businessDetails.typeOfBusiness}<br>
               |${businessDetails.howBusinessPropertyIsUsed}
               |</p>
               |""".stripMargin
          },
          changeAction = Some({
            if (request.sessionData.forType == FOR6030)
              routes.CateringOperationBusinessDetailsController.show(Some(index))
            else
              routes.ConcessionTypeDetailsController.show(index)
          }.asChangeLink("operatorName"))
        ),
        CardEntry(
          label = messages("feeReceived.concessionOrFranchiseFee"),
          value = "",
          changeAction = Some({
            if (request.sessionData.forType == FOR6030)
              routes.FeeReceivedController.show(index)
            else
              routes.ConcessionTypeFeesController.show(index)
          }.asChangeLink("rentReceived")),
          classes = "no-border-bottom"
        ),
        CardEntry(
          label = messages("turnover.financialYearEnd"),
          value = tableRow(
            record.feeReceived.fold(Seq.empty[String]) {
              _.feeReceivedPerYear.map(fee => dateUtil.formatDayMonthAbbrYear(fee.financialYearEnd))
            }
          ),
          changeAction = None,
          classes = "no-border-bottom"
        ),
        CardEntry(
          label = messages("turnover.tradingPeriod"),
          value = tableRow(
            record.feeReceived.fold(Seq.empty[String]) {
              _.feeReceivedPerYear.map(fee => s"${fee.tradingPeriod} ${messages("turnover.weeks")}")
            }
          ),
          changeAction = None,
          classes = "no-border-bottom"
        ),
        CardEntry(
          label = messages("feeReceived.concessionOrFranchiseFee"),
          value = tableRow(
            record.feeReceived.fold(Seq.empty[String]) {
              _.feeReceivedPerYear.map(_.concessionOrFranchiseFee.getOrElse(zeroBigDecimal).asMoney)
            }
          ),
          changeAction = None,
          classes = "no-border-bottom"
        ),
        CardEntry(
          label = "",
          value = record.feeReceived.fold("")(_.feeCalculationDetails.getOrElse("")),
          changeAction = None,
          classes = "no-border-bottom"
        )
      )
    )

  private def concession6015CardsData(
    record: Concession6015IncomeRecord,
    businessCounter: Int,
    index: Int,
    dateUtil: DateUtilLocalised
  )(using messages: Messages) = {
    val fragment = "franchiseOperatorName"
    CardData(
      index = businessCounter,
      label = messages("checkYourAnswersAboutFranchiseOrLettings.business"),
      removeAction = routes.RentalIncomeListController.remove(index).withFromCheckYourAnswer(fragment),
      entries = Seq(
        CardEntry(
          label = messages("checkYourAnswersAboutFranchiseOrLettings.businessOperatorName"),
          value = record.businessDetails.fold("") { businessDetails =>
            s"""
               |<p class="govuk-body">${businessDetails.operatorName}<br>
               |${businessDetails.typeOfBusiness}<br>
               |${businessDetails.cateringAddress.map(_.escapedHtml).getOrElse("")}
               |</p>
               |""".stripMargin
          },
          changeAction = Some(routes.FranchiseTypeDetailsController.show(index).asChangeLink(fragment))
        ),
        rentReceivedCardEntry(record.rent, index),
        howRentWasCalculatedCardEntry(record.calculatingTheRent, index, dateUtil),
        itemsIncludedCardEntry(record.itemsIncluded, index)
      )
    )
  }

  def cardsData(rentalIncome: Seq[IncomeRecord], dateUtil: DateUtilLocalised)(using
    request: SessionRequest[?],
    messages: Messages
  ) = {
    var businessCounter = -1
    var lettingCounter  = -1
    for ((r, index) <- rentalIncome.zipWithIndex)
      yield r match {
        case record: FranchiseIncomeRecord      =>
          businessCounter = businessCounter + 1
          franchiseCardsData(record, businessCounter, index, dateUtil)
        case record: Concession6015IncomeRecord =>
          businessCounter = businessCounter + 1
          concession6015CardsData(record, businessCounter, index, dateUtil)
        case record: LettingIncomeRecord        =>
          lettingCounter = lettingCounter + 1
          lettingCardsData(record, lettingCounter, index, dateUtil)
        case record: ConcessionIncomeRecord     =>
          businessCounter = businessCounter + 1
          concessionCardsData(record, businessCounter, index, dateUtil)
      }
  }
