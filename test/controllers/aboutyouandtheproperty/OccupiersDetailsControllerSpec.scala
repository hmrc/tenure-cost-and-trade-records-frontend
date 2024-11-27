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

import actions.SessionRequest
import form.aboutyouandtheproperty.OccupiersDetailsForm.occupiersDetailsForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class OccupiersDetailsControllerSpec extends TestBaseSpec {

  import TestData._

  def controller(
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Option(
      prefilledAboutYouAndThePropertyPartTwo6048
    )
  ) = new OccupiersDetailsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    occupiersDetailsView,
    preEnrichedActionRefiner(aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo),
    mockSessionRepo
  )

  def aboutYouControllerNone() = new OccupiersDetailsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    occupiersDetailsView,
    preEnrichedActionRefiner(aboutYouAndThePropertyPartTwo = None),
    mockSessionRepo
  )

  "Occupiers details controller" should {
    "GET / return 200 about you in the session" in {
      val result = controller().show(None)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = controller().show(None)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 no about you in the session" in {
      val result = controller().show(None)(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = controller().submit(None)(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }
      "update session data correctly on successful form submission" in {

        val request        = FakeRequest(POST, "/submit-path")
          .withFormUrlEncodedBody("occupiersDetailsName" -> "John Doe", "occupiersDetailsAddress" -> "123 Test Street")
        val sessionRequest = SessionRequest(baseFilled6048Session, request)
        val result         = controller().submit(Option(0))(sessionRequest)

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/task-list")

      }

    }

    "Occupiers details form" should {

      "error if occupiers name is missing" in {
        val formData = baseFormData - errorKey.name
        val form     = occupiersDetailsForm.bind(formData)
        mustContainError(errorKey.name, "error.aboutYou.occupiersDetails.name.required", form)
      }

      "error if occupiers address is missing" in {
        val formData = baseFormData - errorKey.address
        val form     = occupiersDetailsForm.bind(formData)
        mustContainError(errorKey.address, "error.aboutYou.occupiersDetails.address.required", form)
      }

      "error if occupiers name exceeds max length" in {
        val invalidFormData = baseFormData.updated(errorKey.name, "A" * 101)
        val form            = occupiersDetailsForm.bind(invalidFormData)
        mustContainError(errorKey.name, "error.aboutYou.occupiersDetails.name.maxLength", form)
      }

      "error if occupiers address exceeds max length" in {
        val invalidFormData = baseFormData.updated(errorKey.address, "B" * 1001)
        val form            = occupiersDetailsForm.bind(invalidFormData)
        mustContainError(errorKey.address, "error.aboutYou.occupiersDetails.address.maxLength", form)
      }
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val name: String    = "occupiersDetailsName"
      val address: String = "occupiersDetailsAddress"
    }

    val baseFormData: Map[String, String] = Map(
      errorKey.name    -> "Mr Brown",
      errorKey.address -> "123 Bristol"
    )
  }

}
