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
import form.{Errors, MappingSupport}
import models.ForTypes
import models.submissions.Form6010.Address
import org.joda.time.DateTime
import play.api.Logging
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.JodaForms._
import play.api.libs.json.{Format, Json}
import play.api.mvc._
import security.NoExistingDocument
import uk.gov.hmrc.http.HeaderNames.trueClientIp
import uk.gov.hmrc.http.{HeaderCarrier, SessionId, SessionKeys, Upstream4xxResponse}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class LoginDetails(referenceNumber: String, postcode: String, startTime: DateTime)

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
      "postcode"        -> text.verifying(
        Errors.invalidPostcodeOnLetter,
        pc => {
          var cleanPostcode = pc.replaceAll("[^\\w\\d]", "")
          cleanPostcode = cleanPostcode.patch(cleanPostcode.length - 3, " ", 0).toUpperCase
          val isValid       = cleanPostcode.matches(MappingSupport.postcodeRegex): Boolean
          isValid
        }
      ),
      "start-time"      -> jodaDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    )(LoginDetails.apply)(LoginDetails.unapply)
  )
}

@Singleton
class LoginController @Inject() (
  audit: Audit,
  mcc: MessagesControllerComponents,
  login: login,
  loginToBackend: LoginToBackendAction,
  errorView: ErrorTemplate,
  loginFailedView: loginFailed,
  lockedOutView: lockedOut,
  test: testSign // setup proper error page
//                                  connector: DefaultBackendConnector,
//                                  areYouStillConnected: areYouStillConnected
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with Logging {

  import LoginController.loginForm

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(login(loginForm)))
  }

  def logout = Action { implicit request =>
    val refNum                     = request.session.get("refNum").getOrElse("-")
    val refNumJson                 = Json.obj(Audit.referenceNumber -> refNum)
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

//    hc.sessionId.map(sessionId =>
//      documentRepo.findById(sessionId.value, refNum).flatMap {
//        case Some(doc) => refNumJson ++ Addresses.addressJson(SummaryBuilder.build(doc))
//        case None => refNumJson
//      }.map(jsObject => audit.sendExplicitAudit("Logout", jsObject))
//    ).getOrElse {
    audit.sendExplicitAudit("Logout", refNumJson)
//    }

    Redirect(routes.LoginController.show()).withNewSession
  }

  def submit = Action.async { implicit request =>
    loginForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(login(formWithErrors))),
        loginData => verifyLogin(loginData.referenceNumber, loginData.postcode, loginData.startTime)
      )
  }

  def notValidFORType: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(test()))
  }

  def verifyLogin(referenceNumber: String, postcode: String, startTime: DateTime)(implicit
    r: MessagesRequest[AnyContent]
  ) = {
    val sessionId = java.util.UUID.randomUUID().toString //TODO - Why new session? Why manually?

    val cleanedRefNumber = referenceNumber.replaceAll("[^0-9]", "")
    var cleanPostcode    = postcode.replaceAll("[^\\w\\d]", "")
    cleanPostcode = cleanPostcode.patch(cleanPostcode.length - 4, " ", 0)

    implicit val hc2: HeaderCarrier = hc.copy(sessionId = Some(SessionId(java.util.UUID.randomUUID().toString)))
    logger.debug(s"Signing in with: reference number : $cleanedRefNumber, postcode: $cleanPostcode")

    loginToBackend(hc2, ec)(cleanedRefNumber, cleanPostcode, startTime)
      .map { case NoExistingDocument(token, forNum, address) =>
        auditLogin(referenceNumber, false, address, forNum)(hc2)
        ForTypes.find(forNum) match {
          case Some(_) =>
            withNewSession(
              Redirect(controllers.routes.AreYouStillConnectedController.show()),
              token,
              forNum,
              s"$referenceNumber",
              sessionId
            )
          case None    =>
            withNewSession(
              Redirect(routes.LoginController.notValidFORType()),
              token,
              forNum,
              s"$referenceNumber",
              sessionId
            )
        }
      }
      .recover {
        case Upstream4xxResponse(_, 409, _, _)    =>
          Conflict(errorView("409 Conflict Error", "409 Conflict Error", "409 Conflict Error"))
        case Upstream4xxResponse(_, 403, _, _)    =>
          Conflict(errorView("403 Forbidden Error", "403 Forbidden Error", "403 Forbidden Error"))
        case Upstream4xxResponse(body, 401, _, _) =>
          val failed            = Json.parse(body).as[FailedLoginResponse]
          val remainingAttempts = failed.numberOfRemainingTriesUntilIPLockout
          logger.warn(s"Failed login: RefNum: $referenceNumber Attempts remaining: $remainingAttempts")
          if (remainingAttempts < 1) {
            val clientIP = r.headers.get(trueClientIp).getOrElse("")
            auditLockedOut(cleanedRefNumber, postcode, cleanPostcode, clientIP)(hc2)

            Redirect(routes.LoginController.lockedOut)
          } else {
            Redirect(routes.LoginController.loginFailed(remainingAttempts))
          }
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

  private def withNewSession(r: Result, token: String, forNum: String, ref: String, sessionId: String)(implicit
    req: Request[AnyContent]
  ) =
    r.withSession(
      (req.session.data ++ Seq(
        SessionKeys.sessionId -> sessionId,
        SessionKeys.authToken -> token,
        "forNum"              -> forNum,
        "refNum"              -> ref
      )).toSeq: _*
    )

}

object FailedLoginResponse {
  implicit val f: Format[FailedLoginResponse] = Json.format[FailedLoginResponse]
}

case class FailedLoginResponse(numberOfRemainingTriesUntilIPLockout: Int)
