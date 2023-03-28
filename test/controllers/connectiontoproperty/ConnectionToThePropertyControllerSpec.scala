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

package controllers.connectiontoproperty

import form.Errors
import form.connectiontoproperty.ConnectionToThePropertyForm.connectionToThePropertyForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.Helpers._
import utils.TestBaseSpec

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

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = connectionToThePropertyController().submit(
        fakeRequest.withFormUrlEncodedBody(Seq.empty: _*)
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
    val errorKey = new {
      val connectionToTheProperty = "connectionToTheProperty"
    }

    val baseFormData: Map[String, String] = Map(
      "connectionToTheProperty" -> "ownerTrustee"
    )
  }
}
