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

package controllers.accommodation

import play.api.mvc.request.RequestTarget
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class AccommodationUnitList6048ControllerSpec extends TestBaseSpec {

  private val nextPage =
    "/send-trade-and-cost-information/task-list#accommodation-details" // TODO: CYA Accommodation details

  def accommodationUnitList6048Controller =
    new AccommodationUnitList6048Controller(
      accommodationUnitListView,
      removeLastUnitView,
      accommodationNavigator,
      preEnrichedActionRefiner(accommodationDetails = Some(prefilledAccommodationDetails)),
      mockSessionRepo,
      stubMessagesControllerComponents()
    )

  private def validFormData: Seq[(String, String)] =
    Seq(
      "addMoreAccommodationUnits" -> "no"
    )

  "GET /" should {
    "return 200" in {
      val result = accommodationUnitList6048Controller.show(fakeRequest)
      status(result) shouldBe OK
    }
  }

  "SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = accommodationUnitList6048Controller.submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "save the form data and redirect to the next page" in {
      val res = accommodationUnitList6048Controller.submit(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

  }

  "GET /accommodation-unit-remove" should {
    "redirect to units list ignoring wrong idx" in {
      val res = accommodationUnitList6048Controller.remove(
        fakeRequest.withTarget(
          RequestTarget("", "", Map("idx" -> Seq("6")))
        )
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(controllers.accommodation.routes.AccommodationUnitList6048Controller.show.url)
    }
  }

  "POST /accommodation-unit-remove" should {
    "redirect to units list on answer No" in {
      val res = accommodationUnitList6048Controller.removeLast(
        fakeRequest.withTarget(
          RequestTarget("", "", Map("removeLastUnit" -> Seq("no"), "idx" -> Seq("8")))
        )
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(controllers.accommodation.routes.AccommodationUnitList6048Controller.show.url)
    }

    "redirect to units list on answer Yes ignoring wrong idx" in {
      val res = accommodationUnitList6048Controller.removeLast(
        fakeRequest.withTarget(
          RequestTarget("", "", Map("removeLastUnit" -> Seq("yes"), "idx" -> Seq("8")))
        )
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(controllers.accommodation.routes.AccommodationUnitList6048Controller.show.url)
    }
  }

}
