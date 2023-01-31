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

package controllers.Form6010

import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.form.{cateringOperationOrLettingAccommodationCheckboxesDetails, cateringOperationOrLettingAccommodationRentDetails}

class CateringOperationOrLettingAccommodationDetailsRentControllerSpec extends TestBaseSpec {

  val mockCateringOperationOrLettingAccommodationCheckboxesDetails =
    mock[cateringOperationOrLettingAccommodationCheckboxesDetails]
  val mockCateringOperationOrLettingAccommodationRentDetails       = mock[cateringOperationOrLettingAccommodationRentDetails]
  when(mockCateringOperationOrLettingAccommodationRentDetails.apply(any, any)(any, any)).thenReturn(HtmlFormat.empty)

  val cateringOperationOrLettingAccommodationDetailsRentController =
    new CateringOperationOrLettingAccommodationDetailsRentController(
      stubMessagesControllerComponents(),
      mockCateringOperationOrLettingAccommodationRentDetails,
      preFilledSession,
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = cateringOperationOrLettingAccommodationDetailsRentController.show(0)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    //TODO - need to find way to mock data to be returned after call is made with index
//    "return HTML" in {
//      val result = cateringOperationOrLettingAccommodationDetailsRentController.show(0)(fakeRequest)
//      contentType(result) shouldBe Some("text/html")
//      charset(result)     shouldBe Some("utf-8")
//    }
  }
}
