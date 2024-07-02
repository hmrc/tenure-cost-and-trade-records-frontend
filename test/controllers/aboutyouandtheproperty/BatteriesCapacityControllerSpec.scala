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

import form.aboutyouandtheproperty.BatteriesCapacityForm.batteriesCapacityForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class BatteriesCapacityControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  def batteriesCapacityController(
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Some(prefilledAboutYouAndThePropertyPartTwo)
  ) = new BatteriesCapacityController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    batteriesCapacityView,
    preEnrichedActionRefiner(aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo),
    mockSessionRepo
  )

  def batteriesCapacityControllerNone() = new BatteriesCapacityController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    batteriesCapacityView,
    preEnrichedActionRefiner(aboutYouAndThePropertyPartTwo = None),
    mockSessionRepo
  )

  "GET / batteries capacity" should {
    "GET / return 200 about you in the session" in {
      val result = batteriesCapacityController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = batteriesCapacityController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 plant and technology in the session" in {
      val result = batteriesCapacityControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT / batteries capacity" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val result = batteriesCapacityController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(result) shouldBe BAD_REQUEST
    }
  }

  "generator capacity form" should {
    "error if  value is missing" in {
      val empty = baseFormData.updated(TestData.errorKey.batteriesCapacity, "")
      val form  = batteriesCapacityForm.bind(empty)

      mustContainError(errorKey.batteriesCapacity, "error.batteriesCapacity.required", form)
    }
  }

  object TestData {
    val errorKey = new {
      val batteriesCapacity = "batteriesCapacity"
    }

    val baseFormData: Map[String, String] = Map(
      "batteriesCapacity" -> "test capacity"
    )
  }

}
