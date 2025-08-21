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

package controllers

import models.pages.ListPageConfig.*
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class AddedMaximumListItemsControllerSpec extends TestBaseSpec:

  private val nextPageAccommodationUnits =
    controllers.accommodation.routes.AccommodationDetailsCYA6048Controller.show.url

  private val nextPageTradeServices =
    controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show().url

  private val nextPageServicesPaidSeparately =
    controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url

  private val nextPageBunkerFuelCards =
    controllers.aboutthetradinghistory.routes.CustomerCreditAccountsController.show().url

  private val nextPageLowMarginFuelCards =
    controllers.aboutthetradinghistory.routes.NonFuelTurnoverController.show().url

  def addedMaximumListItemsController =
    new AddedMaximumListItemsController(
      addedMaximumListItemsView,
      preEnrichedActionRefiner(accommodationDetails = Some(prefilledAccommodationDetails)),
      mockSessionRepo,
      stubMessagesControllerComponents()
    )

  private def validFormData: Seq[(String, String)] =
    Seq(
      "exceededMaxListItems" -> "true"
    )

  "AccommodationUnits - GET /" should {
    "return 200" in {
      val result = addedMaximumListItemsController.show(AccommodationUnits)(fakeRequest)
      status(result) shouldBe OK
    }
  }

  "AccommodationUnits - SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = addedMaximumListItemsController.submit(AccommodationUnits)(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "save the form data and redirect to the next page" in {
      val res = addedMaximumListItemsController.submit(AccommodationUnits)(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageAccommodationUnits)
    }
  }

  "TradeServices - GET /" should {
    "return 200" in {
      val result = addedMaximumListItemsController.show(TradeServices)(fakeRequest)
      status(result) shouldBe OK
    }
  }

  "TradeServices - SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = addedMaximumListItemsController.submit(TradeServices)(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageTradeServices)
    }
  }

  "ServicesPaidSeparately - GET /" should {
    "return 200" in {
      val result = addedMaximumListItemsController.show(ServicesPaidSeparately)(fakeRequest)
      status(result) shouldBe OK
    }
  }

  "ServicesPaidSeparately - SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = addedMaximumListItemsController.submit(ServicesPaidSeparately)(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageServicesPaidSeparately)
    }
  }

  "BunkerFuelCards - GET /" should {
    "return 200" in {
      val result = addedMaximumListItemsController.show(BunkerFuelCards)(fakeRequest)
      status(result) shouldBe OK
    }
  }

  "BunkerFuelCards - SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = addedMaximumListItemsController.submit(BunkerFuelCards)(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageBunkerFuelCards)
    }
  }

  "LowMarginFuelCards - GET /" should {
    "return 200" in {
      val result = addedMaximumListItemsController.show(LowMarginFuelCards)(fakeRequest)
      status(result) shouldBe OK
    }
  }

  "LowMarginFuelCards - SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = addedMaximumListItemsController.submit(LowMarginFuelCards)(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageLowMarginFuelCards)
    }
  }
