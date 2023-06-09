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

package form

object Errors {

  //annual rent
  val annualRentExcludingVAT         = "error.required.annualRentExcludingVat"
  val annualRentExcludingVATCurrency = "error.invalid_currency.annualRent"

  //address
  val addressRequired                   = "address.required"
  val addressBuildingNameNumberRequired = "error.address.buildingName.required"
  val addressTownCityRequired           = "error.address.townCity.required"
  val addressPostcodeRequired           = "error.address.postcode.required"

  //Login
  val invalidRefNum             = "error.invalid_refnum"
  val maxCurrencyAmountExceeded = "error.maxCurrencyAmountExceeded"
  val toDateIsAfterFromDate     = "error.writtenAgreement.steppedDetails.stepTo.day"
  val fromDateIsAfterToDate     = "error.writtenAgreement.steppedDetails.stepFrom.day"
  val overlappingDates          = "error.steppedDetails.overlappingDates"
  val invalidPostcode           = "error.invalid_postcode"
  val invalidPostcodeOnLetter   = "error.invalid_postcode_as_on_letter"

  //Are you still connected Error
  val isConnectedError          = "error.isRelated"
  val connectionToPropertyError = "error.no.connection.selected"
  // Not connected
  val isPastConnected           = "error.pastConnectionType"
  //About You Errors
  val userTypeRequired          = "error.userType.required"
  val contactDetailsMissing     = "error.contact.details.missing"
  val contactPhoneRequired      = "error.contact.phone.required"
  val contactEmailRequired      = "error.contact.email.required"
  val alternativeAddressMissing = "error.alternative.address.missing"
  val alternativeContactMissing = "error.alternative.contact.missing"

  //About The Property Errors
  val currentOccupierName   = "error.currentOccupierName.required"
  val propertyCurrentlyUsed = "error.currentPropertyUse.required"

  //Incentives, Payments and Conditions
  val formerLeaseAgreementSurrendered = "error.formerLeaseAgreementSurrendered.required"
  val rentReducedOnReview             = "error.rentReducedOnReview.required"
  val capitalSumOrPremium             = "error.capitalSumOrPremium.required"
  val receivePaymentWhenLeaseGranted  = "error.receivePaymentWhenLeaseGranted.required"

  //Tied For Goods details
  val tiedTypeRequired    = "error.tiedType.required"
  val tiedForGoodsDetails = "error.tiedForGoodsDetails.required"

  //Current Rent Payable Within 12 Months
  val currentRentPayableWithin12Months = "error.currentRentPayableWithin12Months.required"

  //Lease or Agreement Details - Three Radio buttons on one page
  val tenancy3Years = "error.tenancy3Years.required"
  val rent3Years    = "error.rent3Years.required"
  val underReview   = "error.underReview.required"

  //How is current rent fixed
  val howIsCurrentRentFixed = "error.howIsCurrentRentFixed.required"

  //Method to fix current rent
  val methodToFixCurrentRents = "error.methodToFixCurrentRents.required"

  //Ultimately Responsible
  val outsideRepairs    = "error.outsideRepairs.required"
  val insideRepairs     = "error.insideRepairs.required"
  val buildingInsurance = "error.buildingInsurance.required"

  //What is your current rent based on
  val currentRentBasedOn = "error.currentRentBasedOn.required"

  //generic errors
  val required        = "error.required"
  val booleanMissing  = "error.boolean_missing"
  val noValueSelected = "error.no_value_selected"
  val maxLength       = "error.maxLength"
  val invalidPhone    = "error.invalid_phone"
  val invalidCurrency = "error.invalid_currency"
  val password        = "error.invalid_password"

  //numeric errors
  val invalidNumber      = "error.invalid_number"
  val bigDecimalNegative = "error.BigDecimal_negative"
  val number             = "error.number"

  //Dates
  val dateBefore1900        = "error.date_before_1900"
  val invalidDate           = "error.invalid_date"
  val invalidDateMonth      = "error.month.required"
  val invalidDateYear       = "error.year.required"
  val dateMustBeInPast      = "error.date_must_be_in_past"
  val invalidDurationDays   = "error.duration.days"
  val invalidDurationMonths = "error.duration.months"
  val invalidDurationYears  = "error.duration.years"

}
