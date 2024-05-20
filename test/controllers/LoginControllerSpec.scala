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
import connectors.{Audit, BackendConnector}
import models.audit.UserData
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json, Writes}
import play.api.mvc.Result
import play.api.test.Helpers._
import security.LoginToBackend.{Postcode, RefNumber, StartTime}
import security.NoExistingDocument
import stub.StubSessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.Helpers.fakeRequest2MessageRequest
import views.html.login
import utils.TestBaseSpec
import util.DateUtil.nowInUK

import scala.concurrent.{ExecutionContext, Future}

class LoginControllerSpec extends TestBaseSpec {

  val loginToBackend = mock[LoginToBackendAction]

  "LoginDetails" should {
    "reference number cleaned" in {
      val loginDetails           = LoginDetails("12345678/123", "BN12 4AX", nowInUK)
      val cleanedReferenceNumber = loginDetails.referenceNumberCleaned
      cleanedReferenceNumber shouldBe "12345678123"
    }
  }

  override def fakeApplication(): play.api.Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"                         -> false,
        "metrics.enabled"                     -> false,
        "bannerNotice.enabled"                -> false,
        "create-internal-auth-token-on-start" -> false
      )
      .build()

  "LoginController" should {
    "show login form" in {
      val loginController = new LoginController(
        inject[BackendConnector],
        inject[Audit],
        stubMessagesControllerComponents(),
        inject[login],
        loginToBackend,
        inject[views.html.error.error],
        inject[views.html.loginFailed],
        inject[views.html.lockedOut],
        inject[views.html.loggedOut],
        preFilledSession,
        StubSessionRepo(),
        inject[views.html.testSign]
      )

      val result = loginController.show(fakeRequest)

      status(result) shouldBe OK

      val content = contentAsString(result)
      content should include("login.heading")
      content should include("""name="referenceNumber"""")
      content should include("""name="postcode"""")
    }

    "show logged out page" in {
      val loginController = new LoginController(
        inject[BackendConnector],
        inject[Audit],
        stubMessagesControllerComponents(),
        inject[login],
        loginToBackend,
        inject[views.html.error.error],
        inject[views.html.loginFailed],
        inject[views.html.lockedOut],
        inject[views.html.loggedOut],
        preFilledSession,
        StubSessionRepo(),
        inject[views.html.testSign]
      )

      val result = loginController.loggedOut(fakeRequest)

      status(result) shouldBe OK

      val content = contentAsString(result)
      content should include("logout.header")
      content should include("""logout.paragraph""")
      content should include("""logout.loginAgain""")
    }
    "show locked out page" in {
      val loginController = new LoginController(
        inject[BackendConnector],
        inject[Audit],
        stubMessagesControllerComponents(),
        inject[login],
        loginToBackend,
        inject[views.html.error.error],
        inject[views.html.loginFailed],
        inject[views.html.lockedOut],
        inject[views.html.loggedOut],
        preFilledSession,
        StubSessionRepo(),
        inject[views.html.testSign]
      )

      val result = loginController.lockedOut(fakeRequest)

      status(result) shouldBe UNAUTHORIZED
    }

    "show login failed page" in {
      val loginController = new LoginController(
        inject[BackendConnector],
        inject[Audit],
        stubMessagesControllerComponents(),
        inject[login],
        loginToBackend,
        inject[views.html.error.error],
        inject[views.html.loginFailed],
        inject[views.html.lockedOut],
        inject[views.html.loggedOut],
        preFilledSession,
        StubSessionRepo(),
        inject[views.html.testSign]
      )

      val attemptsRemaining = 1

      val result: Future[Result] = loginController.loginFailed(attemptsRemaining)(fakeRequest)

      status(result) shouldBe UNAUTHORIZED

    }

    "Audit successful login" in {

      val audit = mock[Audit]
      doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

      val loginToBackendFunction = (refNum: RefNumber, postcode: Postcode, start: StartTime) => {
        assert(refNum.equals("01234567000"))
        Future.successful(NoExistingDocument("token", "forNum", prefilledAddress))
      }

      val loginToBackend = mock[LoginToBackendAction]
      val time           = nowInUK
      when(loginToBackend.apply(any[HeaderCarrier], any[ExecutionContext])).thenReturn(loginToBackendFunction)

      val loginController = new LoginController(
        inject[BackendConnector],
        audit,
        stubMessagesControllerComponents(),
        mock[login],
        loginToBackend,
        mock[views.html.error.error],
        mock[views.html.loginFailed],
        mock[views.html.lockedOut],
        inject[views.html.loggedOut],
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
            "address"             -> prefilledAddress
          )
        )
      )(any[HeaderCarrier], any[ExecutionContext])

    }

    "Audit logout event" in {
      val audit = mock[Audit]
      doNothing
        .when(audit)
        .sendExplicitAudit(any[String], any[UserData])(any[HeaderCarrier], any[ExecutionContext], any[Writes[UserData]])

      val loginController = new LoginController(
        inject[BackendConnector],
        audit,
        stubMessagesControllerComponents(),
        mock[login],
        loginToBackend,
        mock[views.html.error.error],
        mock[views.html.loginFailed],
        mock[views.html.lockedOut],
        inject[views.html.loggedOut],
        preFilledSession,
        mockSessionRepo,
        mock[views.html.testSign]
      )

      val result = loginController.logout(fakeRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.LoginController.loggedOut.url)

      verify(audit).sendExplicitAudit(
        eqTo("Logout"),
        eqTo(
          UserData(
            referenceNumber,
            forType6010,
            prefilledAddress,
            stillConnectedDetails = Some(prefilledStillConnectedDetailsYes),
            removeConnectionDetails = Some(prefilledRemoveConnection),
            aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo),
            additionalInformation = Some(prefilledAdditionalInformation),
            aboutTheTradingHistory = Some(prefilledAboutTheTradingHistory),
            aboutTheTradingHistoryPartOne = Some(prefilledAboutTheTradingHistoryPartOne),
            aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings),
            aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOne),
            aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwo),
            aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree),
            requestReferenceNumber = Some(prefilledRequestRefNumCYA),
            None
          )
        )
      )(
        any[HeaderCarrier],
        any[ExecutionContext],
        any[Writes[UserData]]
      )

    }

  }

}
