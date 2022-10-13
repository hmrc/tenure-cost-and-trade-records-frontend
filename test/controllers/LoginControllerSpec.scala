/*
 * Copyright 2022 HM Revenue & Customs
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
import models.submissions.Address
import org.joda.time.DateTime
import org.mockito.scalatest.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import security.LoginToBackend.{Postcode, RefNumber, StartTime}
import security.NoExistingDocument
import uk.gov.hmrc.http.HeaderCarrier
import utils.Helpers.fakeRequest2MessageRequest
import views.html.login

import scala.concurrent.{ExecutionContext, Future}

class LoginControllerSpec extends AnyFlatSpec with should.Matchers with MockitoSugar {
  implicit val globalEc = scala.concurrent.ExecutionContext.Implicits.global
//  val documentRepo = mock[FormDocumentRepository]

  private val testAddress = Address("13", Some("Street"), Some("City"), "AA11 1AA")

  "login controller" should "Audit successful login" in {

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
      mock[views.html.testSign]
    )

    val fakeRequest = FakeRequest()
    //should strip out all non digits then split string 3 from end to create ref1/ref2
    val response    = loginController.verifyLogin("01234567000", "BN12 1AB", time)(fakeRequest)

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

  "Login Controller" should "Audit logout event" in {
    val audit = mock[Audit]
    doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

    val loginController =
      new LoginController(
        audit,
        stubMessagesControllerComponents(),
        mock[login],
        null,
        mock[views.html.ErrorTemplate],
        mock[views.html.testSign]
      )

    val fakeRequest = FakeRequest()

    val response = loginController.logout().apply(fakeRequest)

    status(response) shouldBe SEE_OTHER

    verify(audit).sendExplicitAudit(eqTo("Logout"), eqTo(Json.obj(Audit.referenceNumber -> "-")))(
      any[HeaderCarrier],
      any[ExecutionContext]
    )

  }

}
