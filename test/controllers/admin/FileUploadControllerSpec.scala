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

package controllers.admin

import connectors.UpscanConnector
import form.admin.FileUploadDataFormProvider
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.test.Helpers._
import utils.TestBaseSpec
import models.admin.UpScanRequests._
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import views.html.admin.{confirmation, fileUpload, login}

import scala.concurrent.{ExecutionContext, Future}

class FileUploadControllerSpec extends TestBaseSpec {

  val mockUpscanConnector: UpscanConnector = mock[UpscanConnector]
  val mockWs: WSClient = mock[WSClient]
  //val mockFormProvider: FileUploadDataFormProvider = mock[FileUploadDataFormProvider]
  val realFormProvider = new FileUploadDataFormProvider()
  val controllerComponents: ControllerComponents = injector.instanceOf[ControllerComponents]
  val messagesControllerComponents: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]
  val configuration: Configuration = injector.instanceOf[Configuration]
  val adminLoginView: login = injector.instanceOf[login]
  val confirmationView: confirmation = injector.instanceOf[confirmation]
  val fileUploadView: fileUpload = injector.instanceOf[fileUpload]

  val controller = new FileUploadController(
    messagesControllerComponents,
    mockUpscanConnector,
    configuration,
    mockWs,
    realFormProvider,
    adminLoginView,
    confirmationView,
    fileUploadView
  )

  "FileUploadController" should {

    "render the login page" in {
      val result: Future[Result] = controller.loginPage.apply(fakeRequest)
      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }

    "authenticate a valid user" in {
     val postRequest = FakeRequest(POST, "/yourAuthRoute")
       .withHeaders("Content-Type" -> "application/x-www-form-urlencoded")
       .withFormUrlEncodedBody("username" -> "validUsername", "password" -> "validPassword")

      val result: Future[Result] = controller.authenticate.apply(postRequest)
      status(result) shouldBe SEE_OTHER
    }

    "reject an invalid user" in {
      val postRequest = FakeRequest(POST, "/yourAuthRoute")
        .withHeaders("Content-Type" -> "application/x-www-form-urlencoded")
        .withFormUrlEncodedBody("username" -> "validUsername", "password" -> "invalidPassword")

      val result: Future[Result] = controller.authenticate.apply(postRequest)
      status(result) shouldBe UNAUTHORIZED
    }

    "display the upload page for authenticated users" in {

      val mockFields = Map("key1" -> "value1", "key2" -> "value2")
      val mockUploadRequest = UploadRequest("http://example.com/upload", mockFields)
      val mockInitiateResponse = InitiateResponse("someReference", mockUploadRequest)

      when(mockUpscanConnector.initiate()(any[HeaderCarrier], any[ExecutionContext]))
        .thenReturn(Future.successful(mockInitiateResponse))

      val requestWithSession = fakeRequest.withSession(("authenticated", "true"))
      val result: Future[Result] = controller.uploadPage.apply(requestWithSession)
      status(result) shouldBe OK
    }

    "showResult" should {

      "display the confirmation view for authenticated users" in {
        val requestWithSession = fakeRequest.withSession(("authenticated", "true"))
        val result: Future[Result] = controller.showResult.apply(requestWithSession)
        status(result) shouldBe OK
        contentType(result) shouldBe Some("text/html")
        // Optionally, you can check for specific content or parts of the view rendered.
      }

      "reject access for unauthenticated users" in {
        val result: Future[Result] = controller.showResult.apply(fakeRequest)
        status(result) shouldBe UNAUTHORIZED
      }
    }

  }
}
