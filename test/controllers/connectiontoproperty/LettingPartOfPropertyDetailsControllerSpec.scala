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

import models.submissions.connectiontoproperty.StillConnectedDetails
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class LettingPartOfPropertyDetailsControllerSpec extends TestBaseSpec {

  def lettingPartOfPropertyDetailsController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new LettingPartOfPropertyDetailsController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      tenantDetailsView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "LettingPartOfPropertyDetailsController GET /" should {
    "return 200 and HTML with Letting Part of Property Details in session" in {
      val result = lettingPartOfPropertyDetailsController().show(Some(0))(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
      )
    }

    "return 200 and HTML Letting Part of Property Details with none in session" in {
      val controller = lettingPartOfPropertyDetailsController(stillConnectedDetails = None)
      val result     = controller.show(Some(0))(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
      )
    }

    "render a page with an empty form" when {
      "not given an index" in {
        val result = lettingPartOfPropertyDetailsController().show(None)(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))

        Option(html.getElementById("tenantName").`val`()).value                               shouldBe ""
        Option(html.getElementById("descriptionOfLetting").`val`()).value                     shouldBe ""
        Option(html.getElementById("correspondenceAddress.buildingNameNumber").`val`()).value shouldBe ""
        Option(html.getElementById("correspondenceAddress.street1").`val`()).value            shouldBe ""
        Option(html.getElementById("correspondenceAddress.town").`val`()).value               shouldBe ""
        Option(html.getElementById("correspondenceAddress.county").`val`()).value             shouldBe ""
        Option(html.getElementById("correspondenceAddress.postcode").`val`()).value           shouldBe ""
      }

      "given an index" which {
        "doesn't already exist in the session" in {
          val result = lettingPartOfPropertyDetailsController().show(Some(2))(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))

          Option(html.getElementById("tenantName").`val`()).value                               shouldBe ""
          Option(html.getElementById("descriptionOfLetting").`val`()).value                     shouldBe ""
          Option(html.getElementById("correspondenceAddress.buildingNameNumber").`val`()).value shouldBe ""
          Option(html.getElementById("correspondenceAddress.street1").`val`()).value            shouldBe ""
          Option(html.getElementById("correspondenceAddress.town").`val`()).value               shouldBe ""
          Option(html.getElementById("correspondenceAddress.county").`val`()).value             shouldBe ""
          Option(html.getElementById("correspondenceAddress.postcode").`val`()).value           shouldBe ""
        }
      }
    }
    "calculateBackLink" should {
      "return back link to CYA page if query param present" in {
        val result = lettingPartOfPropertyDetailsController().show(Some(0))(fakeRequestFromCYA)
        contentAsString(result) should include(
          controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
        )
      }
      "return back link to previous letting page" in {
        val result = lettingPartOfPropertyDetailsController().show(Some(1))(fakeRequest)
        contentAsString(result) should include(
          controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0).url
        )
      }
      "return back link to Is Rent Received From page if there is only 1 letting" in {
        val result = lettingPartOfPropertyDetailsController().show(Some(0))(fakeRequest)
        contentAsString(result) should include(
          controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
        )
      }

    }
    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = lettingPartOfPropertyDetailsController().submit(None)(
          FakeRequest().withFormUrlEncodedBody(Seq.empty*)
        )
        status(res) shouldBe BAD_REQUEST
      }
    }

    "Redirect when form data submitted" in {
      val res = lettingPartOfPropertyDetailsController().submit(Some(0))(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tenantName"                               -> "Tenants name",
          "descriptionOfLetting"                     -> "Description of letting",
          "correspondenceAddress.buildingNameNumber" -> "Building name number",
          "correspondenceAddress.street1"            -> "Street",
          "correspondenceAddress.town"               -> "Town",
          "correspondenceAddress.county"             -> "County",
          "correspondenceAddress.postcode"           -> "SW1A 1AA"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Bad request when tenantName exceeds 50 char" in {
      val res = lettingPartOfPropertyDetailsController().submit(Some(0))(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tenantName"                               -> "X" * 51,
          "descriptionOfLetting"                     -> "Description of letting",
          "correspondenceAddress.buildingNameNumber" -> "Building name number",
          "correspondenceAddress.street1"            -> "Street",
          "correspondenceAddress.town"               -> "Town",
          "correspondenceAddress.county"             -> "County",
          "correspondenceAddress.postcode"           -> "SW1A 1AA"
        )
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Bad request when descriptionOfLetting exceeds 50 char" in {
      val res = lettingPartOfPropertyDetailsController().submit(Some(0))(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tenantName"                               -> "Tenants name",
          "descriptionOfLetting"                     -> "X" * 51,
          "correspondenceAddress.buildingNameNumber" -> "Building name number",
          "correspondenceAddress.street1"            -> "Street",
          "correspondenceAddress.town"               -> "Town",
          "correspondenceAddress.county"             -> "County",
          "correspondenceAddress.postcode"           -> "SW1A 1AA"
        )
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Bad request when buildingNameNumber exceeds 50 char" in {
      val res = lettingPartOfPropertyDetailsController().submit(Some(0))(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tenantName"                               -> "Tenants name",
          "descriptionOfLetting"                     -> "Description of letting",
          "correspondenceAddress.buildingNameNumber" -> "X" * 51,
          "correspondenceAddress.street1"            -> "Street",
          "correspondenceAddress.town"               -> "X" * 51,
          "correspondenceAddress.buildingNameNumber" -> "County",
          "correspondenceAddress.postcode"           -> "SW1A 1AA"
        )
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Bad request when town exceeds 50 char" in {
      val res = lettingPartOfPropertyDetailsController().submit(Some(0))(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tenantName"                               -> "Tenants name",
          "descriptionOfLetting"                     -> "Description of letting",
          "correspondenceAddress.buildingNameNumber" -> "Building name number",
          "correspondenceAddress.street1"            -> "Street",
          "correspondenceAddress.town"               -> "X" * 51,
          "correspondenceAddress.county"             -> "County",
          "correspondenceAddress.postcode"           -> "SW1A 1AA"
        )
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
