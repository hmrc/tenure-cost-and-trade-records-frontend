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

import play.api.Logging
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import connectors.{Audit, BackendConnector}
import play.api.data.Form
import play.api.data.Forms._
import views.html.{login, testSign}
import org.joda.time.DateTime
import play.api.data.JodaForms._
import form.{Errors, MappingSupport}
import models.submissions.Address
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import views.html.Form6010.areYouStillConnected

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class LoginDetails(referenceNumber: String, postcode: String, startTime: DateTime)

object LoginController {
  val loginForm = Form(
    mapping(
      //format of reference number should be 7 or 8 digits then / then 3 digits
      "referenceNumber" -> text.verifying(Errors.invalidRefNum, x => {
        val cleanRefNumber = x.replaceAll("\\D+", "")
        val validLength = cleanRefNumber.length > 9 && cleanRefNumber.length < 12: Boolean
        validLength
      }),
      "postcode" -> text.verifying(Errors.invalidPostcodeOnLetter, pc => {
        var cleanPostcode = pc.replaceAll("[^\\w\\d]", "")
        cleanPostcode = cleanPostcode.patch(cleanPostcode.length - 3, " ", 0).toUpperCase
        val isValid = cleanPostcode.matches(MappingSupport.postcodeRegex): Boolean
        isValid
      }),
      "start-time" -> jodaDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    )(LoginDetails.apply)(LoginDetails.unapply))
}

@Singleton
class LoginController  @Inject()(
                                  audit: Audit,
                                  mcc: MessagesControllerComponents,
                                  login: login,
                                  test: testSign,
                                  connector: BackendConnector,
                                  areYouStillConnected: areYouStillConnected
                                ) (implicit ec: ExecutionContext) extends FrontendController(mcc) with Logging {

  import LoginController.loginForm

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(login(loginForm)))
  }

  def submit = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(login(formWithErrors))),
      loginData => verifyLogin(loginData.referenceNumber, loginData.postcode, loginData.startTime)
    )
  }

  def verifyLogin(referenceNumber: String, postcode: String, startTime: DateTime)(implicit r: MessagesRequest[AnyContent]) = {
    val cleanedRefNumber = referenceNumber.replaceAll("[^0-9]", "")
    var cleanPostcode = postcode.replaceAll("[^\\w\\d]", "")
    cleanPostcode = cleanPostcode.patch(cleanPostcode.length - 4, " ", 0)

    implicit val hc2: HeaderCarrier = hc.copy(sessionId = Some(SessionId(java.util.UUID.randomUUID().toString)))
    logger.debug(s"Signing in with: reference number : ${cleanedRefNumber}, postcode: ${cleanPostcode}")
    connector.verifyCredentials(referenceNumber, postcode)(hc2, ec).flatMap {
      case name =>
        auditLogin(referenceNumber, false, Address("Somewhere", None, None, "BN12 4AX"), "6010")(hc2)
        Future.successful(Ok(areYouStillConnected()))
    }
  }

  private def auditLogin(refNumber: String, returnUser: Boolean, address: Address, formOfReturn: String)(implicit hc: HeaderCarrier): Unit = {
    val json = Json.obj("returningUser" -> returnUser, Audit.referenceNumber -> refNumber, Audit.formOfReturn -> formOfReturn)
    audit.sendExplicitAudit("UserLogin", json)
  }
}