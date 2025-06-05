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

package controllers.aboutfranchisesorlettings

import connectors.Audit
import models.ForType.*
import models.Session
import play.api.test.Helpers.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.JsoupHelpers.*
import utils.TestBaseSpec

import scala.concurrent.Future.successful

class CateringOperationBusinessDetailsControllerSpec extends TestBaseSpec:

  "the CateringOperationBusinessDetails controller" when {
    "handling GET / requests"  should {
      "reply 200 with a fresh HTML form and expected backLink" in new ControllerFixture {
        val result = controller.show(index = None)(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val html = contentAsJsoup(result)
        html.getElementsByTag("h1").first().text()    shouldBe "cateringOperationOrLettingAccommodationDetails.heading"
        html.getElementById("operatorName6030").value shouldBe ""
        html.getElementById("typeOfBusiness").value   shouldBe ""
        html.backLink                                   should endWith(routes.TypeOfIncomeController.show().url)
      }

      "reply 200 with a pre-filled HTML form and expected backLink" in new ControllerFixture {
        val result = controller.show(index = Some(2))(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val html = contentAsJsoup(result)
        html.getElementsByTag("h1").first().text()    shouldBe "cateringOperationOrLettingAccommodationDetails.heading"
        html.getElementById("operatorName6030").value shouldBe ""
        html.getElementById("typeOfBusiness").value   shouldBe ""
        html.backLink                                   should endWith(routes.TypeOfIncomeController.show(Some(2)).url)
      }
    }
    "handling POST / requests" should {
      "reply 400 and error messages when the form is submitted with invalid data" in new ControllerFixture {
        val result  = controller.submit(index = None)(fakePostRequest)
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("error.operatorName6030.required")
        content          should include("error.typeOfBusiness.required")
        content          should include("error.howBusinessPropertyIsUsed.required")
        reset(repository)
      }
    }
  }

  val mockAudit: Audit = mock[Audit]

  trait ControllerFixture:
    val repository = mock[SessionRepo]
    val data       = captor[Session]
    when(repository.saveOrUpdate(any[Session])(using any[HeaderCarrier]))
      .thenReturn(successful(()))

    val controller =
      new CateringOperationBusinessDetailsController(
        stubMessagesControllerComponents(),
        mockAudit,
        aboutFranchisesOrLettingsNavigator,
        cateringOperationDetailsView,
        preEnrichedActionRefiner(
          forType = FOR6030,
          aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings6030)
        ),
        repository
      )
