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

package controllers.abouttheproperty

import navigation.AboutThePropertyNavigator
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.aboutyouandtheproperty.tiedForGoodsDetails

class TiedForGoodsDetailsControllerSpec extends TestBaseSpec {

  val mockAboutThePropertyNavigator = mock[AboutThePropertyNavigator]
  val mockTiedForGoodsDetailsView   = mock[tiedForGoodsDetails]
  when(mockTiedForGoodsDetailsView.apply(any)(any, any)).thenReturn(HtmlFormat.empty)

  val tiedForGoodsDetailsController = new TiedForGoodsDetailsController(
    stubMessagesControllerComponents(),
    mockAboutThePropertyNavigator,
    mockTiedForGoodsDetailsView,
    preFilledSession,
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = tiedForGoodsDetailsController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = tiedForGoodsDetailsController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }
}
