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

class LettingPartOfPropertyDetailsRentControllerSpec extends TestBaseSpec {

  def lettingPartOfPropertyDetailsRentController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new LettingPartOfPropertyDetailsRentController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      lettingPartOfPropertyRentDetailsView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "LettingPartOfPropertyDetailsRentController GET /" should {
    "return 200 and HTML with Letting Part of Property Details Rent in session" in {
      val result = lettingPartOfPropertyDetailsRentController().show(0)(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show(Some(0)).url
      )
    }

    "redirect the user to the other Letting other part of property details rent page" when {
      "given an index" which {
        "does not exist within the session" in {
          val result = lettingPartOfPropertyDetailsRentController().show(2)(fakeRequest)
          status(result) shouldBe SEE_OTHER

          redirectLocation(result) shouldBe Some(
            controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show(None).url
          )
        }
      }
    }

    "display the page with the fields prefilled in" when {
      "given an index" which {
        "exists within the session" in {
          val result = lettingPartOfPropertyDetailsRentController().show(0)(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))

          Option(html.getElementById("annualRent").`val`()).value      shouldBe "2000"
          Option(html.getElementById("dateInput.month").`val`()).value shouldBe "6"
          Option(html.getElementById("dateInput.year").`val`()).value  shouldBe "2022"
        }
      }
    }
  }

  "LettingPartOfPropertyDetailsRentController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = lettingPartOfPropertyDetailsRentController().submit(0)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = lettingPartOfPropertyDetailsRentController().submit(0)(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "annualRent"      -> "12345",
          "dateInput.day"   -> "22",
          "dateInput.month" -> "10",
          "dateInput.year"  -> "2024"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "calculateBackLink" should {

    "return back link to CYA page when 'from=CYA' query param is present and user is connected to the property" in {
      val result = lettingPartOfPropertyDetailsRentController().show(0)(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      )
    }
  }
  "return correct back link with corresponding index" in {
    val result = lettingPartOfPropertyDetailsRentController().show(0)(fakeRequest)
    contentAsString(result) should include(
      controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show(Some(0)).url
    )
  }
}
