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

package controllers

import actions.WithSessionRefiner
import config.LoginToBackendAction
import connectors.{Audit, BackendConnector}
import controllers.LoginController.startPage
import form.PostcodeMapping.customPostcodeMapping
import form.Errors
import models.audit.DownloadPDFAudit
import models.submissions.common.Address
import models.{ForTypes, Session}
import play.api.Logging
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.libs.json.{Format, Json}
import play.api.mvc._
import repositories.SessionRepo
import security.NoExistingDocument
import uk.gov.hmrc.http.HeaderNames.trueClientIp
import uk.gov.hmrc.http.{HeaderCarrier, JsValidationException, UpstreamErrorResponse}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.DateUtil.nowInUK
import views.html._

import java.time.{ZoneOffset, ZonedDateTime}
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class LoginDetails(referenceNumber: String, postcode: String, startTime: ZonedDateTime) {

  /**
    * Returns only referenceNumber digits without slash or any other special char to use in endpoint path.
    */
  def referenceNumberCleaned = referenceNumber.replaceAll("[^0-9]", "")
}

object LoginController {
  val loginForm = Form(
    mapping(
      //format of reference number should be 7 or 8 digits then / then 3 digits
      "referenceNumber" -> text.verifying(
        Errors.invalidRefNum,
        x => {
          val cleanRefNumber = x.replaceAll("\\D+", "")
          val validLength    = cleanRefNumber.length > 9 && cleanRefNumber.length < 12: Boolean
          validLength
        }
      ),
      "postcode"        -> customPostcodeMapping,
      "start-time"      -> default(
        localDateTime("yyyy-MM-dd'T'HH:mm:ss.SSS")
          .transform[ZonedDateTime](_.atZone(ZoneOffset.UTC), _.toLocalDateTime),
        nowInUK
      )
    )(LoginDetails.apply)(LoginDetails.unapply)
  )

  val startPage: Call = controllers.connectiontoproperty.routes.AreYouStillConnectedController.show()

}

@Singleton
class LoginController @Inject() (
  backendConnector: BackendConnector,
  audit: Audit,
  mcc: MessagesControllerComponents,
  login: login,
  loginToBackend: LoginToBackendAction,
  errorView: views.html.error.error,
  loginFailedView: loginFailed,
  lockedOutView: lockedOut,
  loggedOutView: loggedOut,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  test: testSign // setup proper error page
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with Logging
    with I18nSupport {

  import LoginController.loginForm

  def show: Action[AnyContent] = Action.async { implicit request =>
    val referenceNumberFromUrl = request.getQueryString("ref").getOrElse("")
    val forTypeFromURL         = request.getQueryString("forType")
    val form                   = loginForm.fill(LoginDetails(referenceNumberFromUrl, "", nowInUK))

    forTypeFromURL match {
      case Some(forType) =>
        audit.sendExplicitAudit(
          "ForRequestedFromContinue",
          DownloadPDFAudit(referenceNumberFromUrl, forType, request.uri)
        )
      case _             => Future.successful(Ok(login(form)))
    }
    Future.successful(Ok(login(form)))
  }

  def logout = (Action andThen withSessionRefiner).async { implicit request =>
    session.remove().map { _ =>
      audit.sendExplicitAudit("Logout", request.sessionData.toUserData)
      Redirect(routes.LoginController.loggedOut).withNewSession
    }
  }

  def loggedOut = Action.async { implicit request =>
    Ok(loggedOutView())
  }

  def submit = Action.async { implicit request =>
    loginForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(login(formWithErrors))),
        loginData => verifyLogin(loginData.referenceNumberCleaned, loginData.postcode, loginData.startTime)
      )
  }

  def notValidFORType: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(test()))
  }

  def verifyLogin(referenceNumber: String, postcode: String, startTime: ZonedDateTime)(implicit
    r: MessagesRequest[AnyContent]
  ) = {

    val cleanedRefNumber = referenceNumber.replaceAll("[^0-9]", "")
    var cleanPostcode    = postcode.replaceAll("[^\\w\\d]", "")
    cleanPostcode = cleanPostcode.patch(cleanPostcode.length - 4, " ", 0)

    logger.debug(s"Signing in with: reference number : $cleanedRefNumber, postcode: $cleanPostcode")

    loginToBackend(hc, ec)(cleanedRefNumber, cleanPostcode, startTime)
      .flatMap { case NoExistingDocument(token, forNum, address) =>
        auditLogin(referenceNumber, false, address, forNum)
        ForTypes.find(forNum) match {
          case Some(_) =>
            session
              .start(Session(referenceNumber, forNum, address, token))
              .flatMap { _ =>
                backendConnector.loadSubmissionDraft(cleanedRefNumber, hc).map {
                  case Some(_) => Redirect(controllers.routes.SaveAsDraftController.loginToResume)
                  case _       => Redirect(startPage)
                }
              }
          case None    =>
            session
              .start(Session(referenceNumber, forNum, address, token))
              .map(_ => Redirect(routes.LoginController.notValidFORType()))
        }
      }
      .recover {
        case UpstreamErrorResponse(_, 409, _, _)    =>
          Conflict(errorView(409))
        case UpstreamErrorResponse(_, 403, _, _)    =>
          Conflict(errorView(403))
        case UpstreamErrorResponse(body, 401, _, _) =>
          val failed            = Json.parse(body).as[FailedLoginResponse]
          val remainingAttempts = failed.numberOfRemainingTriesUntilIPLockout
          logger.warn(s"Failed login: RefNum: $referenceNumber Attempts remaining: $remainingAttempts")
          if (remainingAttempts < 1) {
            val clientIP = r.headers.get(trueClientIp).getOrElse("")
            auditLockedOut(cleanedRefNumber, postcode, cleanPostcode, clientIP)

            Redirect(routes.LoginController.lockedOut)
          } else {
            Redirect(routes.LoginController.loginFailed(remainingAttempts))
          }
      }
      .recoverWith { case e: JsValidationException =>
        Redirect(controllers.error.routes.ErrorHandlerController.showJsonError)
      }

  }

  private def auditLogin(refNumber: String, returnUser: Boolean, address: Address, formOfReturn: String)(implicit
    hc: HeaderCarrier
  ): Unit = {
    val json = Json.obj(
      "returningUser"       -> returnUser,
      Audit.referenceNumber -> refNumber,
      Audit.formOfReturn    -> formOfReturn,
      Audit.address         -> address
    )
    audit.sendExplicitAudit("UserLogin", json)
  }

  private def auditLockedOut(refNumber: String, postcode: String, postcodeCleaned: String, lockedIP: String)(implicit
    hc: HeaderCarrier
  ): Unit = {
    val detailJson = Json.obj(
      Audit.referenceNumber -> refNumber,
      "postcode"            -> postcode,
      "postcodeCleaned"     -> postcodeCleaned,
      "lockedIP"            -> lockedIP
    )
    audit.sendExplicitAudit("LockedOut", detailJson)
  }

  def lockedOut = Action { implicit request =>
    Unauthorized(lockedOutView())
  }

  def loginFailed(attemptsRemaining: Int) = Action { implicit request =>
    Unauthorized(loginFailedView(attemptsRemaining))
  }

}

object FailedLoginResponse {
  implicit val f: Format[FailedLoginResponse] = Json.format[FailedLoginResponse]
}

case class FailedLoginResponse(numberOfRemainingTriesUntilIPLockout: Int)
