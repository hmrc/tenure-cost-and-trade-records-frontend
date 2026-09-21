/*
 * Copyright 2026 HM Revenue & Customs
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

import connectors.Audit
import form.aboutyouandtheproperty.TiedForGoodsForm.*
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import test.ControllerSpec

import scala.language.reflectiveCalls

class TiedForGoodsControllerSpec extends ControllerSpec:

  import TestData.*

  val mockAudit: Audit = mock[Audit]

  def tiedForGoodsController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ): TiedForGoodsController =
    TiedForGoodsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYouAndThePropertyNavigator,
      tiedForGoodsView,
      preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def tiedForGoodsControllerNoEnforcement(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ): TiedForGoodsController =
    TiedForGoodsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYouAndThePropertyNavigator,
      tiedForGoodsView,
      preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def tiedForGoodsControllerNone(): TiedForGoodsController =
    TiedForGoodsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYouAndThePropertyNavigator,
      tiedForGoodsView,
      preEnrichedActionRefiner(aboutYouAndTheProperty = None),
      mockSessionRepository
    )

  "GET /" should {
    "return 200 tied goods in the session" in {
      val result = tiedForGoodsController().show(getRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = tiedForGoodsController().show(getRequest)
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenDetailsController.show().url
      )
    }

    "return 200 no to enforcement in the session" in {
      val result = tiedForGoodsControllerNoEnforcement().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show().url
      )
    }

    "return 200 no tied goods in the session" in {
      val result = tiedForGoodsControllerNone().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = tiedForGoodsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "redirect when form data submitted" in {
      val res = tiedForGoodsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("tiedForGoods" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Tied for goods form" should {
    "error if tiedForGoods is missing" in {
      val formData = baseFormData - errorKey.tiedForGoods
      val form     = tiedForGoodsForm.bind(formData)

      mustContainError(errorKey.tiedForGoods, "error.tiedForGoods.missing", form)
    }
  }

  object TestData:
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey:
      val tiedForGoods: String = "tiedForGoods"

    val baseFormData: Map[String, String] = Map("tiedForGoods" -> "yes")
