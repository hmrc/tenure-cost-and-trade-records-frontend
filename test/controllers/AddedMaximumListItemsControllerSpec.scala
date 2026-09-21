/*
 * Copyright 2026 HM Revenue & Customs
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
import test.ControllerSpec

/**
  * @author Yuriy Tumakha
  */
class AddedMaximumListItemsControllerSpec extends ControllerSpec:

  private val nextPageAccommodationUnits = controllers.accommodation.routes.AccommodationDetailsCYA6048Controller.show.url

  private val nextPageTradeServices = controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show().url

  private val nextPageServicesPaidSeparately = controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url

  private val nextPageBunkerFuelCards = controllers.aboutthetradinghistory.routes.CustomerCreditAccountsController.show().url

  private val nextPageLowMarginFuelCards = controllers.aboutthetradinghistory.routes.NonFuelTurnoverController.show().url

  def addedMaximumListItemsController: AddedMaximumListItemsController =
    AddedMaximumListItemsController(
      addedMaximumListItemsView,
      preEnrichedActionRefiner(accommodationDetails = Some(prefilledAccommodationDetails)),
      mockSessionRepository,
      stubMessagesControllerComponents()
    )

  private def validFormData: Seq[(String, String)] =
    Seq(
      "exceededMaxListItems" -> "true"
    )

  "AccommodationUnits - GET /" should:
    "return 200" in {
      val result = addedMaximumListItemsController.show(AccommodationUnits)(getRequest)
      status(result) shouldBe OK
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
        postRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageAccommodationUnits)
    }
  }

  "TradeServices - GET /" should {
    "return 200" in {
      val result = addedMaximumListItemsController.show(TradeServices)(getRequest)
      status(result) shouldBe OK
    }
  }

  "TradeServices - SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = addedMaximumListItemsController.submit(TradeServices)(
        postRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageTradeServices)
    }
  }

  "ServicesPaidSeparately - GET /" should {
    "return 200" in {
      val result = addedMaximumListItemsController.show(ServicesPaidSeparately)(getRequest)
      status(result) shouldBe OK
    }
  }

  "ServicesPaidSeparately - SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = addedMaximumListItemsController.submit(ServicesPaidSeparately)(
        postRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageServicesPaidSeparately)
    }
  }

  "BunkerFuelCards - GET /" should {
    "return 200" in {
      val result = addedMaximumListItemsController.show(BunkerFuelCards)(getRequest)
      status(result) shouldBe OK
    }
  }

  "BunkerFuelCards - SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = addedMaximumListItemsController.submit(BunkerFuelCards)(
        postRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageBunkerFuelCards)
    }
  }

  "LowMarginFuelCards - GET /" should {
    "return 200" in {
      val result = addedMaximumListItemsController.show(LowMarginFuelCards)(getRequest)
      status(result) shouldBe OK
    }
  }

  "LowMarginFuelCards - SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = addedMaximumListItemsController.submit(LowMarginFuelCards)(
        postRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageLowMarginFuelCards)
    }
  }
