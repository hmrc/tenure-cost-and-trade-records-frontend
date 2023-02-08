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
import views.html.aboutYourLeaseOrTenure.aboutYourLandlord
import views.html.form._
import views.html.login

trait FakeViews { this: GuiceOneAppPerSuite =>

  lazy val cateringOperationOrLettingAccommodationView              =
    app.injector.instanceOf[cateringOperationOrLettingAccommodation]
  lazy val cateringOperationOrLettingAccommodationDetailsView       =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationDetails]
  lazy val cateringOperationOrLettingAccommodationRentDetailsView   =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentDetails]
  lazy val cateringOperationOrLettingAccommodationCheckboxesDetails =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationCheckboxesDetails]
  lazy val loginView                                                =
    app.injector.instanceOf[login]
  lazy val aboutYourLandlordView                                    =
    app.injector.instanceOf[aboutYourLandlord]
  lazy val lettingOtherPartOfPropertyView                           =
    app.injector.instanceOf[cateringOperationOrLettingAccommodation]
  lazy val lettingOtherPartOfPropertyDetailsView                    =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationDetails]
  lazy val lettingOtherPartOfPropertyRentDetailsView                =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentDetails]
  lazy val lettingOtherPartOfPropertyCheckboxesDetailsView          =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationCheckboxesDetails]

}
