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

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import views.html.aboutYourLeaseOrTenure._
import views.html.aboutfranchisesorlettings._
import views.html.login

trait FakeViews { this: GuiceOneAppPerSuite =>

  // Sign in
  lazy val loginView = app.injector.instanceOf[login]

  // About the franchise or letting
  lazy val franchiseOrLettingsTiedToPropertyView      =
    app.injector.instanceOf[franchiseOrLettingsTiedToProperty]
  lazy val cateringOperationView                      =
    app.injector.instanceOf[cateringOperationOrLettingAccommodation]
  lazy val cateringOperationDetailsView               =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationDetails]
  lazy val cateringOperationRentDetailsView           =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentDetails]
  lazy val cateringOperationRentIncludesView          =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentIncludes]
  lazy val lettingOtherPartOfPropertyView             =
    app.injector.instanceOf[cateringOperationOrLettingAccommodation]
  lazy val lettingOtherPartOfPropertyDetailsView      =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationDetails]
  lazy val lettingOtherPartOfPropertyRentDetailsView  =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentDetails]
  lazy val lettingOtherPartOfPropertyRentIncludesView =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentIncludes]
  lazy val concessionOrFranchiseView                  =
    app.injector.instanceOf[concessionOrFranchise]

  // About the lease or tenure
  lazy val aboutYourLandlordView = app.injector.instanceOf[aboutYourLandlord]

  lazy val currentRentPayableWithin12MonthsView =
    app.injector.instanceOf[currentRentPayableWithin12Months]
  lazy val currentAnnualRentView                =
    app.injector.instanceOf[currentAnnualRent]
  lazy val leaseOrAgreementYearsView            =
    app.injector.instanceOf[leaseOrAgreementYears]

}
