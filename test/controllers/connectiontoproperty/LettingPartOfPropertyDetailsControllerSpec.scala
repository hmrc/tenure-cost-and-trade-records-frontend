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
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class LettingPartOfPropertyDetailsControllerSpec extends TestBaseSpec {

  def lettingPartOfPropertyDetailsControllerr(
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
      val result = lettingPartOfPropertyDetailsControllerr().show(Some(0))(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
      )
    }

    "return 200 and HTML Letting Part of Property Details with none in session" in {
      val controller = lettingPartOfPropertyDetailsControllerr(stillConnectedDetails = None)
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
        val result = lettingPartOfPropertyDetailsControllerr().show(None)(fakeRequest)
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
          val result = lettingPartOfPropertyDetailsControllerr().show(Some(2))(fakeRequest)
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

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = lettingPartOfPropertyDetailsControllerr().submit(None)(
          FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
        )
        status(res) shouldBe BAD_REQUEST
      }
    }

  }
}
