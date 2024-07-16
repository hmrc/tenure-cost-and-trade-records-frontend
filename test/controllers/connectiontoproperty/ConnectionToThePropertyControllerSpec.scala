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

package controllers.connectiontoproperty

import form.Errors
import form.connectiontoproperty.ConnectionToThePropertyForm.connectionToThePropertyForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class ConnectionToThePropertyControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  def connectionToThePropertyController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYes)
  ) = new ConnectionToThePropertyController(
    stubMessagesControllerComponents(),
    connectedToPropertyNavigator,
    connectionToThePropertyView,
    preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
    mockSessionRepo
  )

  def connectionToThePropertyControllerEditAddress(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsEdit)
  ) = new ConnectionToThePropertyController(
    stubMessagesControllerComponents(),
    connectedToPropertyNavigator,
    connectionToThePropertyView,
    preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
    mockSessionRepo
  )

  def connectionToThePropertyControllerException(
    stillConnectedDetails: Option[StillConnectedDetails] = None
  ) = new ConnectionToThePropertyController(
    stubMessagesControllerComponents(),
    connectedToPropertyNavigator,
    connectionToThePropertyView,
    preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = connectionToThePropertyController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = connectionToThePropertyController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "GET edit address" should {
    "return 200" in {
      val result = connectionToThePropertyControllerEditAddress().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = connectionToThePropertyControllerEditAddress().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "GET empty still connected" should {
    "return exception" in {
      val result = connectionToThePropertyControllerException().show(fakeRequest)
      result.failed.recover { case e: Exception =>
        e.getMessage shouldBe "Navigation for connection to property page reached with error Unknown connection to property back link"
      }
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = connectionToThePropertyController().submit(
        fakeRequest.withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "connection to property form" should {
    "error if isRelated is missing" in {
      val formData = baseFormData - errorKey.connectionToTheProperty
      val form     = connectionToThePropertyForm.bind(formData)

      mustContainError(errorKey.connectionToTheProperty, Errors.connectionToPropertyError, form)
    }
  }

  object TestData {
    val errorKey = new ErrorKey

    class ErrorKey {
      val connectionToTheProperty = "connectionToTheProperty"
    }

    val baseFormData: Map[String, String] = Map(
      "connectionToTheProperty" -> "ownerTrustee"
    )
  }
}
