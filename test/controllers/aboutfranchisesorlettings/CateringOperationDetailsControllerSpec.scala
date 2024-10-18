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

package controllers.aboutfranchisesorlettings

import models.ForType
import models.ForType.*
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class CateringOperationDetailsControllerSpec extends TestBaseSpec {

  val mockAboutFranchisesOrLettingsNavigator = mock[AboutFranchisesOrLettingsNavigator]

  def cateringOperationOrLettingAccommodationDetailsController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ) =
    new CateringOperationDetailsController(
      stubMessagesControllerComponents(),
      mockAboutFranchisesOrLettingsNavigator,
      cateringOperationDetailsView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  def cateringOperationOrLettingAccommodationDetailsController6015(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ) =
    new CateringOperationDetailsController(
      stubMessagesControllerComponents(),
      mockAboutFranchisesOrLettingsNavigator,
      cateringOperationDetailsView,
      preEnrichedActionRefiner(forType = FOR6015, aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = cateringOperationOrLettingAccommodationDetailsController().show(None)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 6015" in {
      val result = cateringOperationOrLettingAccommodationDetailsController6015().show(None)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = cateringOperationOrLettingAccommodationDetailsController().show(None)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML 6015" in {
      val result = cateringOperationOrLettingAccommodationDetailsController6015().show(None)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render a page with an empty form" when {
      "not given an index" in {
        val result = cateringOperationOrLettingAccommodationDetailsController().show(None)(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))

        Option(html.getElementById("operatorName").`val`()).value                       shouldBe ""
        Option(html.getElementById("typeOfBusiness").`val`()).value                     shouldBe ""
        Option(html.getElementById("cateringAddress.buildingNameNumber").`val`()).value shouldBe ""
        Option(html.getElementById("cateringAddress.street1").`val`()).value            shouldBe ""
        Option(html.getElementById("cateringAddress.town").`val`()).value               shouldBe ""
        Option(html.getElementById("cateringAddress.county").`val`()).value             shouldBe ""
        Option(html.getElementById("cateringAddress.postcode").`val`()).value           shouldBe ""
      }
      "given an index" which {
        "doesn't already exist in the session" in {
          val result = cateringOperationOrLettingAccommodationDetailsController().show(Some(2))(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))

          Option(html.getElementById("operatorName").`val`()).value                       shouldBe ""
          Option(html.getElementById("typeOfBusiness").`val`()).value                     shouldBe ""
          Option(html.getElementById("cateringAddress.buildingNameNumber").`val`()).value shouldBe ""
          Option(html.getElementById("cateringAddress.street1").`val`()).value            shouldBe ""
          Option(html.getElementById("cateringAddress.town").`val`()).value               shouldBe ""
          Option(html.getElementById("cateringAddress.county").`val`()).value             shouldBe ""
          Option(html.getElementById("cateringAddress.postcode").`val`()).value           shouldBe ""
        }
      }
    }
    // TODO - figure out why this is not rendering with appropriate details from session in test environment - works on future test specs?
//    "render a page with a pre-filled form" when {
//      "given an index" which {
//        "already exists in the session" in {
//          val result = cateringOperationOrLettingAccommodationDetailsController().show(Some(0))(fakeRequest)
//          val html   = Jsoup.parse(contentAsString(result))
//
//          Option(html.getElementById("operatorName").`val`())                       shouldBe "Operator Name"
//          Option(html.getElementById("typeOfBusiness").`val`())                     shouldBe "Type of Business"
//          Option(html.getElementById("cateringAddress.buildingNameNumber").`val`()) shouldBe "004"
//          Option(html.getElementById("cateringAddress.street1").`val`()).value      shouldBe "GORING ROAD"
//          Option(html.getElementById("cateringAddress.town").`val`()).value         shouldBe "GORING-BY-SEA, WORTHING"
//          Option(html.getElementById("cateringAddress.county").`val`()).value       shouldBe "West Sussex"
//          Option(html.getElementById("cateringAddress.postcode").`val`()).value     shouldBe "BN12 4AX"
//        }
//      }
//    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = cateringOperationOrLettingAccommodationDetailsController().submit(None)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
