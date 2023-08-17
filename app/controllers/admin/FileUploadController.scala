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
import models.admin.UpScanRequests.{UploadConfirmation, UploadConfirmationSuccess}
import play.api.Configuration
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import views.html.admin.{confirmation, fileUpload, login}

import scala.concurrent.{ExecutionContext, Future}

class FileUploadController @Inject() (
  mcc: MessagesControllerComponents,
  upscanConnector: UpscanConnector,
  configuration: Configuration,
  ws: WSClient,
  formProvider: FileUploadDataFormProvider,
  loginView: login,
  confirmationView: confirmation,
  fileUploadView: fileUpload
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  val loginForm: Form[User] = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(User.apply)(User.unapply)
  )

  private[controllers] val uploadForm = formProvider()

  def loginPage = Action { implicit request =>
    Ok(loginView(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      errorForm => BadRequest(loginView(errorForm)),
      user =>
        if (checkUser(user)) {
          Redirect(routes.FileUploadController.uploadPage)
            .withSession(request.session + ("authenticated" -> "true"))
        } else {
          Unauthorized("Invalid credentials.")
        }
    )
  }

  def showResult = AuthenticatedAction(parse.default) { implicit request =>
    Ok(confirmationView())
  }

  def uploadPage = AuthenticatedActionAsync(parse.default) { implicit request =>
    for {
      upscanInitiateResponse <- upscanConnector.initiate()
    } yield Ok(fileUploadView(uploadForm, Some(upscanInitiateResponse)))
  }

  def checkUser(user: User): Boolean = {
    val appEncryptedUsername = configuration.get[String]("app.username")
    val appEncryptedPassword = configuration.get[String]("app.password")
    user.username == appEncryptedUsername && user.password == appEncryptedPassword
  }

  def AuthenticatedAction[A](parser: BodyParser[A])(f: Request[A] => Result): Action[A] = Action(parser) {
    implicit request =>
      request.session.get("authenticated") match {
        case Some(_) => f(request)
        case None    => Unauthorized("You must be logged in to access this page.")
      }
  }

  def AuthenticatedActionAsync[A](parser: BodyParser[A])(f: Request[A] => Future[Result]): Action[A] =
    Action.async(parser) { implicit request =>
      request.session.get("authenticated") match {
        case Some(_) => f(request)
        case None    => Future.successful(Unauthorized("You must be logged in to access this page."))
      }
    }
}

case class User(username: String, password: String)
