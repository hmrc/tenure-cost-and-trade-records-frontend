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

package form.accommodation

import models.submissions.accommodation.AvailableRooms
import play.api.data.FormError
import utils.TestBaseSpec

class AvailableRooms6048FormSpec extends TestBaseSpec {
  "AvailableRooms6048Form" should {

    "fail to bind when single and double bedrooms are zero" in {
      val data = Map(
        "singleBedrooms"                -> "0",
        "doubleBedrooms"                -> "0",
        "bathrooms"                     -> "6",
        "otherAccommodationDescription" -> "",
        "maxGuestsNumber"               -> "10"
      )

      val form = AvailableRooms6048Form.availableRooms6048Form.bind(data)

      form.errors  should contain(FormError("", "error.accommodation.singleBedrooms.zero"))
      form.value shouldBe None
    }
  }
}
