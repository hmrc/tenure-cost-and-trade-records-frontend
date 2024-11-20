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

import models.Session
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.JsoupHelpers.*
import utils.TestBaseSpec

import scala.concurrent.Future.successful

class FeeReceivedControllerSpec extends TestBaseSpec {

  "the FeeReceived controller" when {
    "handling GET / requests"  should {
      "reply 303 redirect if the given index does not exist" in new ControllerFixture {
        val result = controller.show(3)(fakeRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.CateringOperationBusinessDetailsController.show(Some(3)).url
      }
      "reply 200 and the pre-filled form if given index exists" in new ControllerFixture {
        val result = controller.show(0)(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.getElementsByTag("h1").first().text()                                       shouldBe "feeReceived.heading"
        html.getElementById("feeReceivedPerYear.year[0].tradingPeriod").value            shouldBe "52"
        html.getElementById("feeReceivedPerYear.year[0].concessionOrFranchiseFee").value shouldBe "1000"
        html.backLink                                                                      should endWith(routes.CateringOperationBusinessDetailsController.show(Some(0)).url)
      }
    }
    "handling POST / requests" should {
      "reply 400 and error messages if the form is submitted with invalid data" in new ControllerFixture {
        val result  = controller.submit(0)(fakePostRequest)
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("error.weeksMapping.blank")
        content          should include("error.feeReceived.concessionOrFranchiseFee.required")
        reset(repository)
      }
      "reply 303 when the form is submitted with good data and index=0" in new ControllerFixture {
        val result = controller.submit(0)(
          fakePostRequest.withFormUrlEncodedBody(
            "feeReceivedPerYear.year[0].tradingPeriod"            -> "24",
            "feeReceivedPerYear.year[0].concessionOrFranchiseFee" -> "500"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.AddAnotherCateringOperationController.show(0).url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        val feeReceivedPerYear =
          data.getValue.aboutFranchisesOrLettings.value.cateringOperationBusinessSections.value.head.feeReceived.value.feeReceivedPerYear.head
        feeReceivedPerYear.tradingPeriod                  shouldBe 24
        feeReceivedPerYear.concessionOrFranchiseFee.value shouldBe 500
        reset(repository)
      }
    }
  }

  trait ControllerFixture:
    val repository = mock[SessionRepo]
    val data       = captor[Session]
    when(repository.saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])).thenReturn(successful(()))

    val controller =
      new FeeReceivedController(
        stubMessagesControllerComponents(),
        aboutFranchisesOrLettingsNavigator,
        feeReceivedView,
        preEnrichedActionRefiner(
          aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings)
        ),
        repository
      )

}
