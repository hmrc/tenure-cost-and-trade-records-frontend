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

package utils

import models.submissions.aboutfranchisesorlettings._
import models.submissions.abouttheproperty.PremisesLicenseGrantedNo
import models.submissions.Form6010.LandlordAddress
import models.submissions.aboutLeaseOrAgreement.AboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.{AboutTheLandlord, AgreedReviewedAlteredThreeYearsYes, CommenceWithinThreeYearsYes, CurrentRentPayableWithin12Months, CurrentRentWithin12MonthsYes, LeaseOrAgreementYearsDetails, RentUnderReviewNegotiatedYes}
import models.submissions.aboutfranchisesorlettings
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, CateringOperationNo, CateringOperationYes, LettingSection}
import models.submissions.abouttheproperty.{AboutTheProperty, BuildingOperationHaveAWebsiteYes, CurrentPropertyHotel, EnforcementActionsNo, LicensableActivitiesNo, PremisesLicensesConditionsNo, PropertyDetails, TiedGoodsNo, WebsiteForPropertyDetails}
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutYourTradingHistory}
import models.submissions.aboutyou.{AboutYou, CustomerDetails}
import models.submissions.additionalinformation.{AdditionalInformation, AltContactInformation, AlternativeContactDetails, AlternativeContactDetailsAddress, FurtherInformationOrRemarksDetails}
import models.{AnnualRent, Session, UserLoginDetails}
import models.submissions.common.{Address, ContactDetails}
import models.submissions.connectiontoproperty.{AddressConnectionTypeYes, StillConnectedDetails}
import models.submissions.notconnected.{RemoveConnectionDetails, RemoveConnectionsDetails}

import java.time.LocalDate

trait FakeObjects {
  val prefilledStillConnectedDetailsYes         = StillConnectedDetails(Some(AddressConnectionTypeYes))
  val prefilledAddress                          = Address("001", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), "BN12 4AX")
  val prefilledContactDetails                   = ContactDetails("1234567890", "TestEmail@gmail.com")
  val prefilledFakeName                         = "John Doe"
  val prefilledContactAddress                   = AlternativeContactDetailsAddress(
    "004",
    Some("GORING ROAD"),
    Some("GORING-BY-SEA, WORTHING"),
    Some("West sussex"),
    "BN12 4AX"
  )
  val prefilledCateringAddress                  =
    CateringAddress("004", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("West sussex"), "BN12 4AX")
  val prefilledLettingAddress                   =
    LettingAddress("004", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("West sussex"), "BN12 4AX")
  val prefilledLandlordAddress                  =
    LandlordAddress("004", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("West sussex"), "BN12 4AX")
  val prefilledFirstOccupy                      = LocalDate.of(1996, 3, 15)
  val prefilledFinancialYear                    = LocalDate.of(2022, 6, 1)
  val prefilledDateInput                        = LocalDate.of(2022, 6, 1)
  val prefilledBigDecimal                       = BigDecimal(9999999)
  val prefilledAnnualRent                       = AnnualRent(prefilledBigDecimal)
  val prefilledCurrentRentPayableWithin12Months =
    CurrentRentPayableWithin12Months(CurrentRentWithin12MonthsYes, prefilledDateInput)

  val prefilledCateringOperationSection     = CateringOperationSection(
    CateringOperationDetails(
      "Operator Name",
      "Type of Business",
      prefilledCateringAddress
    ),
    Some(
      CateringOperationRentDetails(
        BigDecimal(1500),
        prefilledDateInput
      )
    )
  )
  val prefilledLettingSection               = LettingSection(
    aboutfranchisesorlettings.LettingOtherPartOfPropertyInformationDetails(
      "Operator Name",
      "Type of Business",
      prefilledLettingAddress
    ),
    Some(
      LettingOtherPartOfPropertyRentDetails(
        BigDecimal(1500),
        prefilledDateInput
      )
    )
  )
  val prefilledAboutTheLandlord             =
    AboutTheLandlord(
      prefilledFakeName,
      prefilledLandlordAddress
    )
  val prefilledLeaseOrAgreementYearsDetails =
    LeaseOrAgreementYearsDetails(
      CommenceWithinThreeYearsYes,
      AgreedReviewedAlteredThreeYearsYes,
      RentUnderReviewNegotiatedYes
    )
  val prefilledUserLoginDetails             =
    UserLoginDetails("Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=", "FOR6010", "99996010004", prefilledAddress)
  val prefilledUserLoginDetails6015         =
    UserLoginDetails("Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=", "FOR6015", "99996015001", prefilledAddress)
  val prefilledBaseSession                  = Session(prefilledUserLoginDetails)
  val prefilledRemoveConnection             =
    RemoveConnectionDetails(
      Some(
        RemoveConnectionsDetails(
          "John Smith",
          ContactDetails("12345678909", "test@email.com"),
          Some("Additional Information is here")
        )
      )
    )

  val prefilledAboutYou                 = AboutYou(Some(CustomerDetails("Tobermory", ContactDetails("12345678909", "test@email.com"))))
  val prefilledAboutThePropertyNo       = AboutTheProperty(
    Some(PropertyDetails("OccupierName", CurrentPropertyHotel, None)),
    Some(WebsiteForPropertyDetails(BuildingOperationHaveAWebsiteYes, Some("webAddress"))),
    Some(PremisesLicenseGrantedNo),
    None,
    Some(LicensableActivitiesNo),
    None,
    Some(PremisesLicensesConditionsNo),
    None,
    Some(EnforcementActionsNo),
    None,
    Some(TiedGoodsNo),
    None
  )
  val prefilledAdditionalInformation    = AdditionalInformation(
    Some(FurtherInformationOrRemarksDetails("Further information or remarks details string"))
  )
  val prefilledAltContactInformation    = AltContactInformation(
    Some(
      AlternativeContactDetails(
        "Full Alternative Contact Name",
        prefilledContactDetails,
        prefilledContactAddress
      )
    )
  )
  val prefilledAboutTheTradingHistory   = AboutTheTradingHistory(
    aboutYourTradingHistory = Some(
      AboutYourTradingHistory(
        prefilledFirstOccupy,
        prefilledFinancialYear
      )
    )
  )
  val prefilledAboutFranchiseOrLettings = AboutFranchisesOrLettings(
    Some(FranchiseOrLettingsTiedToPropertiesYes),
    None,
    Some(CateringOperationYes),
    0,
    IndexedSeq(prefilledCateringOperationSection),
    Some(LettingOtherPartOfPropertiesYes),
    0,
    IndexedSeq(prefilledLettingSection)
  )

  val prefilledAboutFranchiseOrLettingsNo = AboutFranchisesOrLettings(
    Some(FranchiseOrLettingsTiedToPropertiesNo),
    None,
    Some(CateringOperationNo),
    0,
    IndexedSeq(prefilledCateringOperationSection),
    Some(LettingOtherPartOfPropertiesNo),
    0,
    IndexedSeq(prefilledLettingSection)
  )

  val prefilledAboutFranchiseOrLettings6015 = AboutFranchisesOrLettings(
    Some(FranchiseOrLettingsTiedToPropertiesYes),
    Some(ConcessionOrFranchiseYes),
    Some(CateringOperationYes),
    0,
    IndexedSeq(prefilledCateringOperationSection),
    Some(LettingOtherPartOfPropertiesYes),
    0,
    IndexedSeq(prefilledLettingSection)
  )

  val prefilledAboutFranchiseOrLettingsNo6015 = AboutFranchisesOrLettings(
    Some(FranchiseOrLettingsTiedToPropertiesNo),
    Some(ConcessionOrFranchiseNo),
    Some(CateringOperationNo),
    0,
    IndexedSeq(prefilledCateringOperationSection),
    Some(LettingOtherPartOfPropertiesNo),
    0,
    IndexedSeq(prefilledLettingSection)
  )

  val prefilledAboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    Some(prefilledLeaseOrAgreementYearsDetails),
    Some(prefilledCurrentRentPayableWithin12Months),
    Some(prefilledAnnualRent)
  )
}
