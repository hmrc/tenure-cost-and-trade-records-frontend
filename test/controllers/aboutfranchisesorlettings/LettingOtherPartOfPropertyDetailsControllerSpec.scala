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

import connectors.Audit
import models.Session
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.JsoupHelpers.*
import utils.TestBaseSpec

import scala.concurrent.Future.successful

class LettingOtherPartOfPropertyDetailsControllerSpec extends TestBaseSpec {

  "the LettingOtherPartOfPropertyDetails controller" when {
    "handling GET / requests"  should {
      "reply 200 with a fresh HTML form if not given any index" in new ControllerFixture {
        val result = controller.show(None)(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.getElementById("lettingOperatorName").value               shouldBe ""
        html.getElementById("lettingTypeOfBusiness").value             shouldBe ""
        html.getElementById("lettingAddress.buildingNameNumber").value shouldBe ""
        html.getElementById("lettingAddress.street1").value            shouldBe ""
        html.getElementById("lettingAddress.town").value               shouldBe ""
        html.getElementById("lettingAddress.county").value             shouldBe ""
        html.getElementById("lettingAddress.postcode").value           shouldBe ""
        html.backLink                                                    should endWith(routes.LettingOtherPartOfPropertyController.show().url)
      }
      "reply 200 with a fresh HTML form if given an unknown index" in new ControllerFixture {
        val result = controller.show(Some(2))(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.getElementById("lettingOperatorName").value               shouldBe ""
        html.getElementById("lettingTypeOfBusiness").value             shouldBe ""
        html.getElementById("lettingAddress.buildingNameNumber").value shouldBe ""
        html.getElementById("lettingAddress.street1").value            shouldBe ""
        html.getElementById("lettingAddress.town").value               shouldBe ""
        html.getElementById("lettingAddress.county").value             shouldBe ""
        html.getElementById("lettingAddress.postcode").value           shouldBe ""
        html.backLink                                                    should endWith(routes.AddAnotherLettingOtherPartOfPropertyController.show(1).url)
      }
      "reply 200 with a pre-filled HTML form if given a known index" in new ControllerFixture(
        Some(prefilledAboutFranchiseOrLettings)
      ) {
        val result = controller.show(Some(0))(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.getElementById("lettingOperatorName").value               shouldBe "Operator Name"
        html.getElementById("lettingTypeOfBusiness").value             shouldBe "Type of Business"
        html.getElementById("lettingAddress.buildingNameNumber").value shouldBe "004"
        html.getElementById("lettingAddress.street1").value            shouldBe "GORING ROAD"
        html.getElementById("lettingAddress.town").value               shouldBe "GORING-BY-SEA, WORTHING"
        html.getElementById("lettingAddress.county").value             shouldBe "West sussex"
        html.getElementById("lettingAddress.postcode").value           shouldBe "BN12 4AX"
      }
    }
    "handling POST / requests" should {
      "reply 400 and error messages if the form is submitted with invalid data" in new ControllerFixture {
        val result  = controller.submit(index = None)(fakePostRequest)
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("error.operatorName.required")
        content          should include("error.lettingTypeOfBusiness.required")
        content          should include("error.required")
        content          should include("error.required")
        content          should include("error.postcode.required")
        reset(repository)
      }
      "reply 303 when the form is submitted good, with index=1 but no pre-existing data" in new ControllerFixture(
        Some(prefilledAboutFranchiseOrLettings)
      ) {
        val result = controller.submit(index = Some(1))(
          fakePostRequest.withFormUrlEncodedBody(
            "lettingOperatorName"               -> "Some operator",
            "lettingTypeOfBusiness"             -> "Nice type of business",
            "lettingAddress.buildingNameNumber" -> "5",
            "lettingAddress.street1"            -> "My Street",
            "lettingAddress.town"               -> "Fuzzy Town",
            "lettingAddress.county"             -> "Neverland",
            "lettingAddress.postcode"           -> "BN124AX"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.LettingOtherPartOfPropertyDetailsRentController.show(1).url
      }
      "reply 303 when the form is submitted good, with index=0 and pre-existing data" in new ControllerFixture(
        Some(prefilledAboutFranchiseOrLettings)
      ) {
        val result = controller.submit(index = Some(0))(
          fakePostRequest.withFormUrlEncodedBody(
            "lettingOperatorName"               -> "Some operator",
            "lettingTypeOfBusiness"             -> "Nice type of business",
            "lettingAddress.buildingNameNumber" -> "5",
            "lettingAddress.street1"            -> "My Street",
            "lettingAddress.town"               -> "Fuzzy Town",
            "lettingAddress.county"             -> "Neverland",
            "lettingAddress.postcode"           -> "BN124AX"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value                                                                                               shouldBe routes.LettingOtherPartOfPropertyDetailsRentController.show(0).url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        data.getValue.aboutFranchisesOrLettings.value.lettingSections.head.lettingOtherPartOfPropertyInformationDetails.operatorName shouldBe "Some operator"
        reset(repository)
      }
    }
  }

  val mockAudit: Audit = mock[Audit]

  trait ControllerFixture(aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = None):
    val repository = mock[SessionRepo]
    val data       = captor[Session]
    when(repository.saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])).thenReturn(successful(()))

    val controller =
      new LettingOtherPartOfPropertyDetailsController(
        stubMessagesControllerComponents(),
        mockAudit,
        aboutFranchisesOrLettingsNavigator,
        lettingOtherPartOfPropertyDetailsView,
        preEnrichedActionRefiner(
          aboutFranchisesOrLettings = aboutFranchisesOrLettings
        ),
        repository
      )
}
