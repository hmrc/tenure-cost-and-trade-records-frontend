/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.form.{aboutYourTradingHistory, enforcementActionBeenTaken, tiedForGoods, tiedForGoodsDetails}
import views.html.login

class TiedForGoodsControllerSpec extends TestBaseSpec {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val fakeRequest = FakeRequest("GET", "/")

  val mockTiedForGoodsView = mock[tiedForGoods]
  when(mockTiedForGoodsView.apply(any)(any, any)).thenReturn(HtmlFormat.empty)

  val tiedForGoodsController = new TiedForGoodsController(
    stubMessagesControllerComponents(),
    mock[login],
    mockTiedForGoodsView,
    mock[tiedForGoodsDetails],
    mock[aboutYourTradingHistory],
    preFilledSession,
    mockSessionRepo
  )


  "GET /" should {
    "return 200" in {
      val result = tiedForGoodsController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = tiedForGoodsController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }
}
