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
import models.{ForType, Session}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.libs.json.Writes
import play.api.test.Helpers.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.JsoupHelpers.*
import utils.TestBaseSpec

import scala.concurrent.Future.successful
import scala.language.reflectiveCalls

class LettingOtherPartOfPropertyControllerSpec extends TestBaseSpec {

  "the LettingOtherPartOfProperty controller" when {
    "handling GET / requests"  should {
      "reply 200 with the fresh form 6010 if no data in session" in new ControllerFixture(
        forType = FOR6010,
        aboutFranchisesOrLettings = None
      ) {
        val result = controller.show(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        contentAsString(result)     should not include "checked"
      }
      "reply 200 with the pre-filled form 6010" in new ControllerFixture(
        forType = FOR6010,
        aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings)
      ) {
        val result = controller.show(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val html = contentAsJsoup(result)
        html.getElementsByTag("h1").first().text()               shouldBe "lettingOtherPartOfProperty.heading"
        html.getElementById("lettingOtherPartOfProperty").toString should include("""value="yes" checked>""")
        html.backLink                                            shouldBe routes.AddAnotherCateringOperationController.show(0).url
      }
    }
    "handling POST / requests" should {
      "reply 400 and error messages if form 6010 is submitted with invalid data from TL" in new ControllerFixture(
        forType = FOR6010
      ) {
        val result  = controller.submit(
          fakePostRequest
            .withQueryString(
              "from" -> Seq("TL")
            )
            .withFormUrlEncodedBody(
              "lettingOtherPartOfProperty" -> "" // missing !!!
            )
        )
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("error.lettingOtherPartOfProperty.missing")
        content          should include(s"${controllers.routes.TaskListController.show().url}#letting-other-part-of-property")
      }
    }
    "reply 303 redirect to 'LettingOtherPartOfPropertyDetails' page if answer='yes' and from CYA" in new ControllerFixture(
      forType = FOR6015
    ) {
      val result = controller.submit(
        fakePostRequest
          .withQueryString(
            "from" -> Seq("CYA")
          )
          .withFormUrlEncodedBody(
            "lettingOtherPartOfProperty" -> "yes"
          )
      )
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    }
    "reply 303 redirect to 'LettingOtherPartOfPropertyDetails' page if answer='yes'" in new ControllerFixture(
      forType = FOR6010
    ) {
      val result = controller.submit(
        fakePostRequest
          .withFormUrlEncodedBody(
            "lettingOtherPartOfProperty" -> "yes"
          )
      )
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.LettingOtherPartOfPropertyDetailsController.show(Some(0)).url
    }
  }

  trait ControllerFixture(
    forType: ForType = FOR6010,
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ):
    val repository       = mock[SessionRepo]
    val data             = captor[Session]
    val mockAudit: Audit = mock[Audit]
    when(repository.saveOrUpdate(any[Session])(using any[Writes[Session]], any[HeaderCarrier]))
      .thenReturn(successful(()))

    val controller =
      new LettingOtherPartOfPropertyController(
        stubMessagesControllerComponents(),
        mockAudit,
        aboutFranchisesOrLettingsNavigator,
        lettingOtherPartOfPropertyView,
        preEnrichedActionRefiner(
          forType = forType,
          aboutFranchisesOrLettings = aboutFranchisesOrLettings
        ),
        repository
      )
}
