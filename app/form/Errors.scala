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

package form

object Errors {

  // annual rent
  val annualRentExcludingVAT         = "error.required.annualRentExcludingVat"
  val annualRentExcludingVATCurrency = "error.invalid_currency.annualRent"

  // address
  val addressRequired                    = "address.required"
  val addressBuildingNameNumberRequired  = "error.buildingNameNumber.required"
  val buildingMaxLength                  = "error.buildingNameNumber.maxLength"
  val addressLine2Length                 = "error.addressLineTwo.maxLength"
  val addressTownLength                  = "error.townCity.maxLength"
  val addressCountyLength                = "error.county.maxLength"
  val addressTownCityRequired            = "error.townCity.required"
  val addressCountyRequired              = "error.county.required"
  val addressPostcodeRequired            = "error.address.postcode.required"
  val postcodeRequired                   = "error.postcode.required"
  val postcodeRequiredAlternativeContact = "error.postcodeAlternativeContact.required"
  val postcodeInvalid                    = "error.postcode.invalid"
  val postcodeMaxLength                  = "error.postcode.maxLength"

  // Login
  val invalidRefNum             = "error.invalid_refnum"
  val maxCurrencyAmountExceeded = "error.maxCurrencyAmountExceeded"
  val minSuceeded               = "error.invalid_postcode"
  val toDateIsAfterFromDate     = "error.writtenAgreement.steppedDetails.stepTo.day"
  val fromDateIsAfterToDate     = "error.writtenAgreement.steppedDetails.stepFrom.day"
  val overlappingDates          = "error.steppedDetails.overlappingDates"
  val invalidPostcode           = "error.invalid_postcode"
  val invalidPostcodeOnLetter   = "error.invalid_postcode_as_on_letter"

  // Are you still connected Error
  val isConnectedError                = "error.isRelated"
  val connectionToPropertyError       = "error.no.connection.selected"
  // Not connected
  val isPastConnected                 = "error.pastConnectionType"
  // About You Errors
  val contactDetailsMissing           = "error.contact.details.missing"
  val contactPhoneRequired            = "error.contact.phone.required"
  val contactPhoneAboutYouRequired    = "error.contact.phoneAboutYou.required"
  val contactAlternativePhoneRequired = "error.contact.alternativePhone.required"
  val contactPhoneLength              = "error.contact.phone.invalidLength"
  val contactEmailRequired            = "error.contact.email.required"
  val contactEmailAboutYouRequired    = "error.contact.emailAboutYou.required"
  val contactAlternativeEmailRequired = "error.contact.alternativeEmail.required"
  val emailFormat                     = "error.emailFormat.required"
  val alternativeAddressMissing       = "error.alternative.address.missing"
  val alternativeContactMissing       = "error.alternative.contact.missing"

  // Vacant properties
  val vacantProperties = "error.vacantProperties.required"

  // Website for property
  val buildingOperatingHaveAWebsite = "error.buildingOperatingHaveAWebsite.required"
  val webAddressBlank               = "error.websiteAddressForProperty.required"
  val webaddressFormat              = "error.webaddressFormat.required"

  // Electric Vehicle Charging Points
  val spacesOrBays       = "error.spacesOrBays.required"
  val spacesOrBaysNumber = "error.spacesOrBaysNumber.required"

  // About The Property Errors
  val currentOccupierName   = "error.currentOccupierName.required"
  val propertyCurrentlyUsed = "error.currentPropertyUse.required"

  // About Franchises or Lettings
  val typeOfLetting = "error.typeOfLetting.required"

  val typeOfIncome = "error.typeOfIncome.required"

  // Incentives, Payments and Conditions
  val formerLeaseAgreementSurrendered = "error.formerLeaseAgreementSurrendered.required"
  val rentReducedOnReview             = "error.rentReducedOnReview.required"
  val capitalSumOrPremium             = "error.capitalSumOrPremium.required"
  val receivePaymentWhenLeaseGranted  = "error.receivePaymentWhenLeaseGranted.required"

  // Tied For Goods details
  val tiedTypeRequired    = "error.tiedType.required"
  val tiedForGoodsDetails = "error.tiedForGoodsDetails.required"

  // Renewables plant
  val renewablesPlant = "error.renewablesPlant.required"

  // Current Rent Payable Within 12 Months
  val currentRentPayableWithin12Months = "error.currentRentPayableWithin12Months.required"

  // Lease or Agreement Details - Three Radio buttons on one page
  val tenancy3Years = "error.tenancy3Years.required"
  val rent3Years    = "error.rent3Years.required"
  val underReview   = "error.underReview.required"

  // How is current rent fixed
  val howIsCurrentRentFixed = "error.howIsCurrentRentFixed.required"

  // Method to fix current rent
  val methodToFixCurrentRents = "error.methodToFixCurrentRents.required"

  // Ultimately Responsible
  val outsideRepairs    = "error.outsideRepairs.required"
  val insideRepairs     = "error.insideRepairs.required"
  val buildingInsurance = "error.buildingInsurance.required"

  // What is your current rent based on
  val currentRentBasedOn = "error.currentRentBasedOn.required"

  // generic errors
  val required                   = "error.required"
  val booleanMissing             = "error.boolean_missing"
  val noValueSelected            = "error.no_value_selected"
  val maxLength                  = "error.maxLength"
  val invalidPhone               = "error.invalid_phone"
  val invalidCharAddress1        = "error.invalidCharAddress1"
  val invalidCharAddress2        = "error.invalidCharAddress2"
  val invalidCharAddressTownCity = "error.invalidCharTownCity"
  val invalidCharAddressCounty   = "error.invalidCharCounty"
  val invalidCurrency            = "error.invalid_currency"
  val password                   = "error.invalid_password"

  // numeric errors
  val invalidNumber      = "error.invalid_number"
  val bigDecimalNegative = "error.BigDecimal_negative"
  val number             = "error.number"

  // Dates
  val dateBeforeToday       = "error.date_before_today"
  val dateBefore1900        = "error.date_before_1900"
  val invalidDate           = "error.invalid_date"
  val invalidDateMonth      = "error.month.required"
  val invalidDateYear       = "error.year.required"
  val dateMustBeInPast      = "error.date_must_be_in_past"
  val dateMustBeInFuture    = "error.date.mustBeInFuture"
  val invalidDurationDays   = "error.duration.days"
  val invalidDurationMonths = "error.duration.months"
  val invalidDurationYears  = "error.duration.years"

}
