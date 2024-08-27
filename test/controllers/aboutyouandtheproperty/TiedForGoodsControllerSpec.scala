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

package controllers.aboutyouandtheproperty

import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class TiedForGoodsControllerSpec extends TestBaseSpec {

  import TestData._
  import form.aboutyouandtheproperty.TiedForGoodsForm._
  import utils.FormBindingTestAssertions._

  def tiedForGoodsController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new TiedForGoodsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    tiedForGoodsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def tiedForGoodsControllerNoEnforcement(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new TiedForGoodsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    tiedForGoodsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def tiedForGoodsControllerNone() = new TiedForGoodsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    tiedForGoodsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "Tied for goods controller" should {
    "return 200 tied goods in the session" in {
      val result = tiedForGoodsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = tiedForGoodsController().show(fakeRequest)
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenDetailsController.show().url
      )
    }

    "return 200 no to enforcement in the session" in {
      val result = tiedForGoodsControllerNoEnforcement().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show().url
      )
    }

    "return 200 no tied goods in the session" in {
      val result = tiedForGoodsControllerNone().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = tiedForGoodsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }

      "Redirect when form data submitted" in {
        val res = tiedForGoodsController().submit(
          FakeRequest(POST, "/").withFormUrlEncodedBody("tiedForGoods" -> "yes")
        )
        status(res) shouldBe SEE_OTHER
      }
    }
  }

  "Tied for goods form" should {
    "error if tiedForGoods is missing" in {
      val formData = baseFormData - errorKey.tiedForGoods
      val form     = tiedForGoodsForm.bind(formData)

      mustContainError(errorKey.tiedForGoods, "error.tiedForGoods.missing", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val tiedForGoods: String = "tiedForGoods"
    }

    val baseFormData: Map[String, String] = Map("tiedForGoods" -> "yes")
  }
}
