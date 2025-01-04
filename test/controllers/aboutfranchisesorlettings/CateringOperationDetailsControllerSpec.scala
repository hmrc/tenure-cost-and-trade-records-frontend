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

import models.ForType.*
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import models.{ForType, Session}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.JsoupHelpers.*
import utils.TestBaseSpec

import scala.concurrent.Future.successful

class CateringOperationDetailsControllerSpec extends TestBaseSpec:

  "the CateringOperationDetails controller" when {
    "handling GET / requests"  should {
      "reply 200 with a fresh HTML form 6010 and backLink to /catering-operation-or-letting-accommodation" in new ControllerFixture(
        FOR6010
      ) {
        val result = controller.show(index = None)(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.getElementsByTag("h1").first().text()                      shouldBe "cateringOperationOrLettingAccommodationDetails.heading"
        html.getElementById("operatorName").value                       shouldBe ""
        html.getElementById("typeOfBusiness").value                     shouldBe ""
        html.getElementById("cateringAddress.buildingNameNumber").value shouldBe ""
        html.getElementById("cateringAddress.street1").value            shouldBe ""
        html.getElementById("cateringAddress.town").value               shouldBe ""
        html.getElementById("cateringAddress.county").value             shouldBe ""
        html.getElementById("cateringAddress.postcode").value           shouldBe ""
        html.backLink                                                     should endWith("/catering-operation-or-letting-accommodation")
      }
      "reply 200 with a pre-filled HTML form 6010 and backLink to /catering-operation-or-letting-accommodation" in new ControllerFixture(
        FOR6010
      ) {
        val result = controller.show(index = Some(0))(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.getElementsByTag("h1").first().text()                      shouldBe "cateringOperationOrLettingAccommodationDetails.heading"
        html.getElementById("operatorName").value                       shouldBe "Operator Name"
        html.getElementById("typeOfBusiness").value                     shouldBe "Type of Business"
        html.getElementById("cateringAddress.buildingNameNumber").value shouldBe "004"
        html.getElementById("cateringAddress.street1").value            shouldBe "GORING ROAD"
        html.getElementById("cateringAddress.town").value               shouldBe "GORING-BY-SEA, WORTHING"
        html.getElementById("cateringAddress.county").value             shouldBe "West sussex"
        html.getElementById("cateringAddress.postcode").value           shouldBe "BN12 4AX"
        html.backLink                                                     should endWith("/catering-operation-or-letting-accommodation")
      }
      "reply 200 with the HTML form 6015 and backLink to /concession-or-franchise" in new ControllerFixture(FOR6015) {
        val result = controller.show(index = Some(2))(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.backLink should endWith("/concession-or-franchise")
      }
      "reply 200 with the HTML form 6010 and backLink to /add-another-catering-operation" in new ControllerFixture(
        FOR6010
      ) {
        val result = controller.show(index = Some(2))(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.backLink should endWith("/add-another-catering-operation?idx=1") // the given index is decremented by 1
      }
    }
    "handling POST / requests" should {
      "reply 400 and error messages when the form is submitted with invalid data" in new ControllerFixture(FOR6010) {
        val result  = controller.submit(index = None)(fakePostRequest)
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("error.operatorName.required")
        content          should include("error.typeOfBusiness.required")
      }
      "reply 303 when the form is submitted with good data and index=0" in new ControllerFixture(FOR6010) {
        val result = controller.submit(index = Some(0))(
          fakePostRequest.withFormUrlEncodedBody(
            "operatorName"                       -> "Another Operator",
            "typeOfBusiness"                     -> "Different Business",
            "cateringAddress.buildingNameNumber" -> "004",
            "cateringAddress.street1"            -> "GORING ROAD",
            "cateringAddress.town"               -> "GORING-BY-SEA, WORTHING",
            "cateringAddress.county"             -> "West sussex",
            "cateringAddress.postcode"           -> "BN12 4AX"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.CateringOperationDetailsRentController.show(0).url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        val updatedCateringOperationDetails =
          data.getValue.aboutFranchisesOrLettings.get.cateringOperationSections(0).cateringOperationDetails
        updatedCateringOperationDetails.operatorName   shouldBe "Another Operator" // instead of "Operator Name"
        updatedCateringOperationDetails.typeOfBusiness shouldBe "Different Business" // instead of "Type of Business"
        reset(repository)
      }
      "reply 303 when the form is submitted with good data but missing index" in new ControllerFixture(FOR6010) {
        pending
        // val result = controller.submit(index = None)(
        //   fakePostRequest.withFormUrlEncodedBody(
        //     "operatorName" -> "Another Operator",
        //     "typeOfBusiness" -> "Different Business",
        //     "cateringAddress.buildingNameNumber" -> "004",
        //     "cateringAddress.street1" -> "GORING ROAD",
        //     "cateringAddress.town" -> "GORING-BY-SEA, WORTHING",
        //     "cateringAddress.county" -> "West sussex",
        //     "cateringAddress.postcode" -> "BN12 4AX"
        //   )
        // )
        // status(result) shouldBe SEE_OTHER
        // redirectLocation(result).value shouldBe "/path/to/anywhere"
        // verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        // val updatedCateringOperationDetails =
        //   data.getValue.aboutFranchisesOrLettings.get.cateringOperationSections(0).cateringOperationDetails
        // updatedCateringOperationDetails.operatorName shouldBe "Another Operator" // instead of "Operator Name"
        // updatedCateringOperationDetails.typeOfBusiness shouldBe "Different Business" // instead of "Type of Business"
        // reset(repository)
      }
      "reply 303 when the form is submitted with good data and the aboutFranchisesOrLettings was missing in session" in new ControllerFixture(
        aboutFranchisesOrLettings = None
      ) {
        val result = controller.submit(index = None)(
          fakePostRequest.withFormUrlEncodedBody(
            "operatorName"                       -> "Another Operator",
            "typeOfBusiness"                     -> "Different Business",
            "cateringAddress.buildingNameNumber" -> "004",
            "cateringAddress.street1"            -> "GORING ROAD",
            "cateringAddress.town"               -> "GORING-BY-SEA, WORTHING",
            "cateringAddress.county"             -> "West sussex",
            "cateringAddress.postcode"           -> "BN12 4AX"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.CateringOperationDetailsRentController.show(0).url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        val updatedCateringOperationDetails =
          data.getValue.aboutFranchisesOrLettings.get.cateringOperationSections(0).cateringOperationDetails
        updatedCateringOperationDetails.operatorName   shouldBe "Another Operator" // instead of "Operator Name"
        updatedCateringOperationDetails.typeOfBusiness shouldBe "Different Business" // instead of "Type of Business"
        reset(repository)
      }
    }
  }

  trait ControllerFixture(
    givenForType: ForType = FOR6010,
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ):
    val repository = mock[SessionRepo]
    val data       = captor[Session]
    when(repository.saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])).thenReturn(successful(()))

    val controller =
      new CateringOperationDetailsController(
        stubMessagesControllerComponents(),
        aboutFranchisesOrLettingsNavigator,
        cateringOperationDetailsView,
        preEnrichedActionRefiner(
          forType = givenForType,
          aboutFranchisesOrLettings = aboutFranchisesOrLettings
        ),
        repository
      )
