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
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.JsoupHelpers.*
import utils.TestBaseSpec

import scala.concurrent.Future.successful

class ConcessionOrFranchiseFeeControllerSpec extends TestBaseSpec:

  "the ConcessionOrFranchiseFee controller" when {
    "handling GET / requests"  should {
      "reply 200 with a fresh HTML form and expected backLink" in new ControllerFixture {
        val result = controller.show(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.getElementsByTag("h1").first().text() shouldBe "concessionOrFranchiseFee.heading"
        html.backLink                                should endWith(routes.FranchiseOrLettingsTiedToPropertyController.show().url)

      }
      "reply 200 with a pre-filled HTML form and expected backLink" in new ControllerFixture(
        Some(prefilledAboutFranchiseOrLettings)
      ) {
        val result = controller.show(fakeRequest.withFormUrlEncodedBody("from" -> "TL"))
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.getElementsByTag("h1").first().text() shouldBe "concessionOrFranchiseFee.heading"
        html.backLink                                should include(controllers.routes.TaskListController.show().url)
      }
    }
    "handling POST / requests" should {
      "reply 400 and error messages if the form is submitted with invalid data" in new ControllerFixture {
        val result  = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "concessionOrFranchiseFee" -> "" // missing
          )
        )
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("error.concessionOrFranchiseFee.missing")
        reset(repository)
      }
      "reply 303 when the form is submitted with 'no', and there were no pre-existing data" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "concessionOrFranchiseFee" -> "no"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value                                           shouldBe routes.LettingOtherPartOfPropertyController.show().url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        data.getValue.aboutFranchisesOrLettings.value.cateringOrFranchiseFee.value should beAnswerNo
        reset(repository)
      }
      "reply 303 when the form is submitted with 'no', and coming from CYA" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "concessionOrFranchiseFee" -> "yes",
            "from"                     -> "CYA"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value                                           shouldBe routes.AddAnotherCateringOperationController.show(0).url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        data.getValue.aboutFranchisesOrLettings.value.cateringOrFranchiseFee.value should beAnswerYes
        reset(repository)
      }
    }
  }

  trait ControllerFixture(aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = None):
    val repository = mock[SessionRepo]
    val data       = captor[Session]
    when(repository.saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])).thenReturn(successful(()))

    val controller = new ConcessionOrFranchiseFeeController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      cateringOperationView,
      preEnrichedActionRefiner(
        aboutFranchisesOrLettings = aboutFranchisesOrLettings
      ),
      repository
    )
