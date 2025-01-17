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
import form.aboutyouandtheproperty.OccupiersDetailsListForm.occupiersDetailsListForm
import models.submissions.aboutyouandtheproperty.{AboutYouAndThePropertyPartTwo, OccupiersDetails}
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class OccupiersDetailsListControllerSpec extends TestBaseSpec {

  import TestData._

  def controller(
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Option(
      prefilledAboutYouAndThePropertyPartTwo6048
    )
  ) = new OccupiersDetailsListController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    occupiersDetailsListView,
    genericRemoveConfirmationView,
    preEnrichedActionRefiner(aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo),
    mockSessionRepo
  )

  "Occupiers details List  GET /" should {
    "return 200 and HTML with Trade Services List in the session" in {
      val result = controller().show(0)(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.OccupiersDetailsController.show(Option(0)).url
      )
    }

    "return 200 and HTML when no Occupiers details List in the session" in {
      val noSesController = controller(aboutYouAndThePropertyPartTwo = None)
      val result          = noSesController.show(0)(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val result = controller().submit(1)(fakeRequest)
        status(result) shouldBe BAD_REQUEST
      }

      "Redirect when form data submitted" in {
        val res = controller().submit(0)(
          FakeRequest(POST, "/").withFormUrlEncodedBody("occupiersDetailsList" -> "yes")
        )
        status(res) shouldBe SEE_OTHER
      }
    }

    "REMOVE /" should {
      "redirect if an empty form is submitted" in {
        val result = controller().remove(1)(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }

  }

  "Remove details" should {

    "render the removal confirmation page on remove" in {
      val partTwo        =
        prefilledAboutYouAndThePropertyPartTwo6048.copy(occupiersList = IndexedSeq(OccupiersDetails("Mike", "Bristol")))
      val idxToRemove    = 0
      val sessionRequest = SessionRequest(stillConnectedDetails6048YesSession, fakeRequest)
      val result         = controller(Some(partTwo)).remove(idxToRemove)(sessionRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }

    "handle form submission with 'Yes' and perform removal" in {
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val sessionRequest  = SessionRequest(stillConnectedDetails6048YesSession, requestWithForm)
      val result          = controller().performRemove(idxToRemove)(sessionRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(
        controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(0).url
      )

    }

    "handle form submission with 'No' and cancel removal" in {
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
      val result          = controller().performRemove(idxToRemove)(requestWithForm)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(
        controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(0).url
      )

    }

  }
  "OccupiersDetails" should {

    "serialize and deserialize correctly" in {
      val occupiersDetails = OccupiersDetails("Mike", "Bristol")
      val json             = Json.toJson(occupiersDetails)
      json.as[OccupiersDetails] shouldBe occupiersDetails
    }

  }

  "Occupiers details list form" should {
    "error if answer is missing" in {
      val formData = baseFormData - errorKey.addOccupiersDetails
      val form     = occupiersDetailsListForm.bind(formData)

      mustContainError(
        errorKey.addOccupiersDetails,
        "error.occupiersDetailsList.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val addOccupiersDetails: String =
        "occupiersDetailsList"
    }

    val baseFormData: Map[String, String] = Map("occupiersDetailsList" -> "yes")
  }
}
