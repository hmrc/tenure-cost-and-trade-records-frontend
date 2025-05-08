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

package controllers.accommodation

import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
class IncludedTariffItems6048ControllerSpec extends TestBaseSpec {

  private val nextPage = controllers.accommodation.routes.AccommodationUnitList6048Controller.show.url + "?idx=0"

  def includedTariffItems6048Controller =
    new IncludedTariffItems6048Controller(
      includedTariffItemsView,
      accommodationNavigator,
      preEnrichedActionRefiner(
        referenceNumber = "99996048008", // England
        accommodationDetails = Some(prefilledAccommodationDetails)
      ),
      mockSessionRepo,
      stubMessagesControllerComponents()
    )(using inject[ExecutionContext])

  private def validFormData: Seq[(String, String)] =
    Seq(
      "includedTariffItems[0]" -> "bedLinen",
      "includedTariffItems[1]" -> "gas"
    )

  "GET /" should {
    "return 200" in {
      val result = includedTariffItems6048Controller.show(fakeRequest)
      status(result) shouldBe OK
    }
  }

  "SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = includedTariffItems6048Controller.submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "save the form data and redirect to the next page" in {
      val res = includedTariffItems6048Controller.submit(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

  }

}
