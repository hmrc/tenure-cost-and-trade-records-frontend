/*
 * Copyright 2025 HM Revenue & Customs
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

import connectors.Audit
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class LettingPartOfPropertyItemsIncludedInRentControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]
  def lettingPartOfPropertyItemsIncludedInRentController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new LettingPartOfPropertyItemsIncludedInRentController(
      stubMessagesControllerComponents(),
      mockAudit,
      connectedToPropertyNavigator,
      lettingPartOfPropertyRentIncludesView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "LettingPartOfPropertyItemsIncludedInRentController GET /" should {
    "return 200 and HTML with Letting Part of PropertyItems Included in session" in {
      val result = lettingPartOfPropertyItemsIncludedInRentController().show(0)(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsRentController.show(0).url
      )
    }

    "return 200 and HTML no Letting Part of PropertyItems Included with None in session" in {
      val controller = lettingPartOfPropertyItemsIncludedInRentController(stillConnectedDetails = None)
      val result     = controller.show(0)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "LettingPartOfPropertyItemsIncludedInRentController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = lettingPartOfPropertyItemsIncludedInRentController().submit(0)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted without CYA param" in {
      val res = lettingPartOfPropertyItemsIncludedInRentController().submit(0)(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "itemsInRent[0]" -> "rates"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Redirect when form data submitted with CYA param" in {
      val res = lettingPartOfPropertyItemsIncludedInRentController().submit(0)(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "itemsInRent[0]" -> "rates"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "calculateBackLink" should {

    "return back link to CYA page when 'from=CYA' query param is present and user is connected to the property" in {
      val result = lettingPartOfPropertyItemsIncludedInRentController().show(0)(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      )
    }
  }
  "return correct back link with corresponding index" in {
    val result = lettingPartOfPropertyItemsIncludedInRentController().show(0)(fakeRequest)
    contentAsString(result) should include(
      controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsRentController.show(0).url
    )
  }

}
