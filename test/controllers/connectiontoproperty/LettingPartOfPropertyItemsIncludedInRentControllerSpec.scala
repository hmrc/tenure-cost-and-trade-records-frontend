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

import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.test.Helpers.stubMessagesControllerComponents
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class LettingPartOfPropertyItemsIncludedInRentControllerSpec extends TestBaseSpec {

  def lettingPartOfPropertyItemsIncludedInRentController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new LettingPartOfPropertyItemsIncludedInRentController(
      stubMessagesControllerComponents(),
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
      charset(result)       shouldBe Some("utf-8")
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
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
