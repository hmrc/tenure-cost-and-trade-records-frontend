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

package controllers.aboutfranchisesorlettings

import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.aboutYourLeaseOrTenure.aboutYourLandlord
import views.html.form.{addAnotherCateringOperationOrLettingAccommodation, cateringOperationOrLettingAccommodationDetails}
import views.html.login

class AddAnotherLettingOtherPartOfPropertyControllerSpec extends TestBaseSpec {

  val mockAddAnotherLettingOtherPartOfProperty = mock[addAnotherCateringOperationOrLettingAccommodation]
  when(mockAddAnotherLettingOtherPartOfProperty.apply(any, any, any, any)(any, any)).thenReturn(HtmlFormat.empty)

  val addAnotherLettingOtherPartOfPropertyController = new AddAnotherLettingOtherPartOfPropertyController(
    stubMessagesControllerComponents(),
    mockAddAnotherLettingOtherPartOfProperty,
    mock[cateringOperationOrLettingAccommodationDetails],
    mock[login],
    mock[aboutYourLandlord],
    preFilledSession,
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = addAnotherLettingOtherPartOfPropertyController.show(0)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    //TODO - need to find way to mock data to be returned after call is made with index
    "return HTML" in {
      val result = addAnotherLettingOtherPartOfPropertyController.show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }
}
