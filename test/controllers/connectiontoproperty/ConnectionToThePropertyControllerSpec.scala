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

package controllers.connectiontoproperty

import navigation.ConnectionToPropertyNavigator
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.connectiontoproperty.connectionToTheProperty

class ConnectionToThePropertyControllerSpec extends TestBaseSpec {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val fakeRequest = FakeRequest("GET", "/")

  val mockConnectedToPropertyNavigator = mock[ConnectionToPropertyNavigator]
  val mockConnectionToThePropertyView  = mock[connectionToTheProperty]
  when(mockConnectionToThePropertyView.apply(any, any)(any, any)).thenReturn(HtmlFormat.empty)

  val connectionToThePropertyController = new ConnectionToThePropertyController(
    stubMessagesControllerComponents(),
    mockConnectedToPropertyNavigator,
    mockConnectionToThePropertyView,
    preFilledSession,
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = connectionToThePropertyController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = connectionToThePropertyController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }
}
