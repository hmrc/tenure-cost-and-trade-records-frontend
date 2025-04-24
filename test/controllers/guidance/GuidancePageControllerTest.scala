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

package controllers.guidance

import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import utils.{JsoupHelpers, TestBaseSpec}
import views.html.guidance.guidancePage as GuidancePageView

class GuidancePageControllerTest extends TestBaseSpec with JsoupHelpers:

  "the GuidancePage controller" should {
    "be handling GET /FOR6010 and reply 200 with the guidance page" in new ControllerFixture {
      val result = controller.show("FOR6010")(fakeGetRequest)
      status(result)            shouldBe OK
      contentType(result).value shouldBe HTML
      charset(result).value     shouldBe UTF_8.charset
      val page = contentAsJsoup(result)
      page.heading shouldBe "guidance.FOR6010.heading"
    }
    "be handling GET /FOR6020 and reply 200 with the guidance page" in new ControllerFixture {
      val result = controller.show("FOR6020")(fakeGetRequest)
      status(result)            shouldBe OK
      contentType(result).value shouldBe HTML
      charset(result).value     shouldBe UTF_8.charset
      val page = contentAsJsoup(result)
      page.heading shouldBe "guidance.FOR6020.heading"
    }
    "be handling GET /FOR6048 and reply 200 with the guidance page" in new ControllerFixture {
      val result = controller.show("FOR6048")(fakeGetRequest)
      status(result)            shouldBe OK
      contentType(result).value shouldBe HTML
      charset(result).value     shouldBe UTF_8.charset
      val page = contentAsJsoup(result)
      page.heading shouldBe "guidance.FOR6048.heading"
    }
    "be handling GET /invalid and reply 404 with an error message" in new ControllerFixture {
      val result = controller.show("invalid")(fakeGetRequest)
      status(result)          shouldBe NOT_FOUND
      contentAsString(result) shouldBe "Invalid forType: invalid"
    }
  }

  trait ControllerFixture:
    val controller = new GuidancePageController(
      mcc = stubMessagesControllerComponents(),
      guidancePageView = inject[GuidancePageView]
    )
