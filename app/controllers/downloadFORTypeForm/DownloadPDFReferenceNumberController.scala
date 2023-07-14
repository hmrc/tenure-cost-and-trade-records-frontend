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

package controllers.downloadFORTypeForm

import actions.WithSessionRefiner
import models.submissions.downloadFORTypeForm.DownloadPDF
import play.api.Logging
import views.html.downloadFORTypeForm.downloadPDF
import scala.util.{Failure, Success}
import connectors.BackendConnector
import form.downloadFORTypeForm.DownloadPDFReferenceNumberForm.downloadPDFReferenceNumberForm
import models.Session
import models.submissions.common.Address
import models.submissions.downloadFORTypeForm.DownloadPDFDetails.updateDownloadPDFDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.DownloadPDFReferenceNumberPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.downloadFORTypeForm.downloadPDFReferenceNumber

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DownloadPDFReferenceNumberController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  downloadPDFReferenceNumberView: downloadPDFReferenceNumber,
  backendConnector: BackendConnector,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with Logging
    with I18nSupport {

  def startWithSession: Action[AnyContent] = Action.async { implicit request =>
    session.start(Session("", "", Address("", None, "", None, ""), ""))
    Future.successful(Redirect(routes.DownloadPDFReferenceNumberController.show()))
  }

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      downloadPDFReferenceNumberView(
        request.sessionData.downloadPDFDetails.flatMap(_.downloadPDFReferenceNumber) match {
          case Some(data) => downloadPDFReferenceNumberForm.fillAndValidate(data)
          case _          => downloadPDFReferenceNumberForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    downloadPDFReferenceNumberForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(downloadPDFReferenceNumberView(formWithErrors))),
        data => {
          val updatedData = updateDownloadPDFDetails(_.copy(downloadPDFReferenceNumber = Some(data)))
          session.saveOrUpdate(updatedData)

          backendConnector
            .retrieveFORType(data.downloadPDFReferenceNumber)
            .onComplete({
              case Success(value) =>
                session.saveOrUpdate(updateDownloadPDFDetails(_.copy(downloadPDF = Some(DownloadPDF(value)))))
              case Failure(ex)    => logger.debug(s"Failed to retrieve a valid FOR Type: ${ex.getMessage}")
            })

          Future.successful(
            Redirect(navigator.nextPage(DownloadPDFReferenceNumberPageId, updatedData).apply(updatedData))
          )
        }
      )
  }

}
