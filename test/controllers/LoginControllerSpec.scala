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

package controllers

import config.LoginToBackendAction
import connectors.Audit
import org.joda.time.DateTime
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import security.LoginToBackend.{Postcode, RefNumber, StartTime}
import security.NoExistingDocument
import uk.gov.hmrc.http.HeaderCarrier
import utils.Helpers.fakeRequest2MessageRequest
import views.html.login
import utils.TestBaseSpec

import scala.concurrent.{ExecutionContext, Future}

class LoginControllerSpec extends TestBaseSpec {

  val loginToBackend = mock[LoginToBackendAction]

  "login controller" should {
    "Audit successful login" in {

      val audit = mock[Audit]
      doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

      val loginToBackendFunction = (refNum: RefNumber, postcode: Postcode, start: StartTime) => {
        assert(refNum.equals("01234567000"))
        Future.successful(NoExistingDocument("token", "forNum", testAddress))
      }

      val loginToBackend = mock[LoginToBackendAction]
      val time           = DateTime.now()
      when(loginToBackend.apply(any[HeaderCarrier], any[ExecutionContext])).thenReturn(loginToBackendFunction)

      val loginController = new LoginController(
        audit,
        stubMessagesControllerComponents(),
        mock[login],
        loginToBackend,
        mock[views.html.ErrorTemplate],
        mock[views.html.loginFailed],
        mock[views.html.lockedOut],
        preFilledSession,
        mockSessionRepo,
        mock[views.html.testSign]
      )

//      val fakeRequest = FakeRequest()
      val response = loginController.verifyLogin("01234567000", "BN12 1AB", time)(fakeRequest)

      status(response) shouldBe SEE_OTHER

      verify(audit).sendExplicitAudit(
        eqTo("UserLogin"),
        eqTo(
          Json.obj(
            Audit.referenceNumber -> "01234567000",
            "returningUser"       -> false,
            Audit.formOfReturn    -> "forNum",
            "address"             -> testAddress
          )
        )
      )(any[HeaderCarrier], any[ExecutionContext])

    }

    "Audit logout event" in {
      val audit = mock[Audit]
      doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

      val loginController = new LoginController(
        audit,
        stubMessagesControllerComponents(),
        mock[login],
        loginToBackend,
        mock[views.html.ErrorTemplate],
        mock[views.html.loginFailed],
        mock[views.html.lockedOut],
        preFilledSession,
        mockSessionRepo,
        mock[views.html.testSign]
      )

//      val fakeRequest = FakeRequest()

      val response = loginController.logout.apply(fakeRequest)

      status(response) shouldBe SEE_OTHER

      verify(audit).sendExplicitAudit(eqTo("Logout"), eqTo(Json.obj(Audit.referenceNumber -> "123456")))(
        any[HeaderCarrier],
        any[ExecutionContext]
      )

    }

  }

}
