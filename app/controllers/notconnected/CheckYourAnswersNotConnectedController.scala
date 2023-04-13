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

package controllers.notconnected

import actions.{RefNumRequest, SessionRequest, WithSessionRefiner}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.notconnected.checkYourAnswersNotConnected
import views.html.confirmationNotConnected

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersNotConnectedController @Inject() (
                                                         mcc: MessagesControllerComponents,
                                                         //  repository: FormDocumentRepository,
                                                         checkYourAnswersNotConnectedView: checkYourAnswersNotConnected,
                                                         confirmationNotConnectedView: confirmationNotConnected,
                                                         //  audit: Audit,
                                                         withSessionRefiner: WithSessionRefiner,
                                                         @Named("session") val session: SessionRepo
                                                       )(implicit ec: ExecutionContext)
  extends FrontendController(mcc)
    with I18nSupport {

  lazy val confirmationUrl = controllers.routes.FormSubmissionController.confirmation().url

  val log = Logger(classOf[CheckYourAnswersNotConnectedController])

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(checkYourAnswersNotConnectedView()))
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    submit(request.sessionData.referenceNumber)
  }

  private def submit[T](refNum: String)(implicit request: SessionRequest[T]): Future[Result] = {
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    for {
      _ <- submitNotConnectedTInformation(refNum)(hc, request)
    } yield Found(confirmationUrl)
  }

  def submitNotConnectedTInformation(
                                      refNum: String
                                    )(implicit hc: HeaderCarrier, request: SessionRequest[_]): Future[Unit] = {
    //    val auditType = "FormSubmission"
    // Dummy data from session to able creation of audit dashboards
    //    val submissionJson = Json.toJson(request.sessionData).as[JsObject]
    submitToBackend()
    //    audit.sendExplicitAudit(auditType, submissionJson ++ Audit.languageJson)
    Future.unit
  }

  def confirmation: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(Ok(confirmationNotConnectedView()))
  }

  private def submitToBackend()(implicit hc: HeaderCarrier, request: SessionRequest[_]): Future[Unit] = {
    log.warn(s"**&&** ${request.sessionData.removeConnectionDetails.flatMap(_.removeConnectionDetails)}")
    Future.unit
  }

  //  def onPageSubmit = refNumAction.async { implicit request =>
  //    findSummary.flatMap {
  //      case Some(summary) =>
  //        val json = Json.obj(Audit.referenceNumber -> summary.referenceNumber) ++
  //          Addresses.addressJson(summary) ++
  //          Audit.languageJson
  //        submitToHod(summary).map { _ =>
  //          audit.sendExplicitAudit("NotConnectedSubmission", json)
  //          Redirect(routes.NotConnectedCheckYourAnswersController.onConfirmationView)
  //        }.recover {
  //          case e: Exception =>
  //            logger.error(s"Could not send data to HOD - ${request.refNum} - ${hc.sessionId}")
  //            audit.sendExplicitAudit("NotConnectedSubmissionFailed", json)
  //            InternalServerError(errorView(500))
  //        }
  //      case None => NotFound(errorView(404))
  //    }
  //  }

  //  def getPreviouslyConnectedFromCache()(implicit hc: HeaderCarrier) = {
  //    cache.fetchAndGetEntry[PreviouslyConnected](SessionId(hc), PreviouslyConnectedController.cacheKey).flatMap {
  //      case Some(x) => Future.successful(x)
  //      case None => Future.failed(new RuntimeException("Unable to find record in cache for previously connected"))
  //    }
  //  }
  //
  //  def getNotConnectedFromCache()(implicit hc: HeaderCarrier): Future[NotConnected] = {
  //    cache.fetchAndGetEntry[NotConnected](SessionId(hc), NotConnectedController.cacheKey).flatMap {
  //      case Some(x) => Future.successful(x)
  //      case None => Future.failed(new RuntimeException("Unable to find record in cache for not connected"))
  //    }
  //  }

  //  private def submitToHod(summary: Summary)(implicit hc: HeaderCarrier, messages: Messages) = {
  //    getNotConnectedFromCache().flatMap { notConnected =>
  //      getPreviouslyConnectedFromCache().flatMap { previouslyConnected =>
  //        val submission = NotConnectedSubmission(
  //          summary.referenceNumber,
  //          summary.address.get,
  //          notConnected.fullName,
  //          notConnected.emailAddress,
  //          notConnected.phoneNumber,
  //          notConnected.additionalInformation,
  //          Instant.now(),
  //          previouslyConnected.previouslyConnected,
  //          messages.lang.language
  //        )
  //
  //        submissionConnector.submitNotConnected(summary.referenceNumber, submission)
  //      }
  //    }
  //  }

}
