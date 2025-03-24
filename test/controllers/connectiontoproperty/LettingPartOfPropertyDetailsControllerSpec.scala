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

import actions.SessionRequest
import connectors.Audit
import connectors.addressLookup.{AddressLookupConfig, AddressLookupConnector}
import models.submissions.connectiontoproperty.StillConnectedDetails
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec
import scala.concurrent.Future.successful

class LettingPartOfPropertyDetailsControllerSpec extends TestBaseSpec {

  trait ControllerFixture(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ):
    val mockAudit              = mock[Audit]
    val addressLookupConnector = mock[AddressLookupConnector]
    when(addressLookupConnector.initJourney(any[AddressLookupConfig])(any[SessionRequest[AnyContent]]))
      .thenReturn(successful(Some("/on-ramp")))
    val controller             = new LettingPartOfPropertyDetailsController(
      stubMessagesControllerComponents(),
      mockAudit,
      connectedToPropertyNavigator,
      tenantDetailsView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      addressLookupConnector,
      mockSessionRepo
    )

  "LettingPartOfPropertyDetailsController GET /" should {
    "return 200 and HTML with Letting Part of Property Details in session" in new ControllerFixture {
      val result = controller.show(Some(0))(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
      )
    }

    "return 200 and HTML Letting Part of Property Details with none in session" in new ControllerFixture {
      val result = controller.show(Some(0))(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
      )
    }

    "render a page with an empty form" when {
      "not given an index" in new ControllerFixture {
        val result = controller.show(None)(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))

        Option(html.getElementById("tenantName").`val`()).value           shouldBe ""
        Option(html.getElementById("descriptionOfLetting").`val`()).value shouldBe ""
      }

      "given an index" which {
        "doesn't already exist in the session" in new ControllerFixture {
          val result = controller.show(Some(2))(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))

          Option(html.getElementById("tenantName").`val`()).value           shouldBe ""
          Option(html.getElementById("descriptionOfLetting").`val`()).value shouldBe ""

        }
      }
    }
    "calculateBackLink" should {
      "return back link to CYA page if query param present" in new ControllerFixture {
        val result = controller.show(Some(0))(fakeRequestFromCYA)
        contentAsString(result) should include(
          controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
        )
      }
      "return back link to previous letting page" in new ControllerFixture {
        val result = controller.show(Some(1))(fakeRequest)
        contentAsString(result) should include(
          controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0).url
        )
      }
      "return back link to Is Rent Received From page if there is only 1 letting" in new ControllerFixture {
        val result = controller.show(Some(0))(fakeRequest)
        contentAsString(result) should include(
          controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
        )
      }

    }
    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in new ControllerFixture {
        val res = controller.submit(None)(
          FakeRequest().withFormUrlEncodedBody(Seq.empty*)
        )
        status(res) shouldBe BAD_REQUEST
      }
    }

    "Redirect when form data submitted without CYA param" in new ControllerFixture {
      val res = controller.submit(Some(0))(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "tenantName"           -> "Tenants name",
          "descriptionOfLetting" -> "Description of letting"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Redirect when form data submitted with CYA param" in new ControllerFixture {
      val res = controller.submit(Some(0))(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tenantName"           -> "Tenants name",
          "descriptionOfLetting" -> "Description of letting"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Bad request when tenantName exceeds 50 char" in new ControllerFixture {
      val res = controller.submit(Some(0))(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tenantName"           -> "X" * 51,
          "descriptionOfLetting" -> "Description of letting"
        )
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Bad request when descriptionOfLetting exceeds 50 char" in new ControllerFixture {
      val res = controller.submit(Some(0))(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tenantName"           -> "Tenants name",
          "descriptionOfLetting" -> "X" * 51
        )
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
