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

package controllers.aboutyouandtheproperty

import form.Errors
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import form.aboutyouandtheproperty.TiedForGoodsDetailsForm.tiedForGoodsDetailsForm
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class TiedForGoodsDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def tiedForGoodsDetailsController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new TiedForGoodsDetailsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    tiedForGoodsDetailsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def tiedForGoodsDetailsControllerNone() = new TiedForGoodsDetailsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    tiedForGoodsDetailsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "Tied for goods details controller" should {
    "return 200 tied goods details in the session" in {
      val result = tiedForGoodsDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = tiedForGoodsDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 no tied goods details in the session" in {
      val result = tiedForGoodsDetailsControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = tiedForGoodsDetailsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Tied for goods form" should {
    "error if tiedForGoods is missing" in {
      val formData = baseFormData - errorKey.tiedForGoodsDetails
      val form     = tiedForGoodsDetailsForm.bind(formData)

      mustContainError(errorKey.tiedForGoodsDetails, Errors.tiedForGoodsDetails, form)
    }
  }

  object TestData {
    val errorKey: Object {
      val tiedForGoodsDetails: String
    } = new {
      val tiedForGoodsDetails: String = "tiedForGoodsDetails"
    }

    val baseFormData: Map[String, String] = Map("tiedForGoodsDetails" -> "fullTie")
  }
}
