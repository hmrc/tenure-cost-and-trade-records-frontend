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

package controllers.aboutYourLeaseOrTenure

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.ConnectedToLandlordDetailsForm.connectedToLandlordDetailsForm
import models.audit.ChangeLinkAudit
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.ConnectedToLandlordInformationDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.ConnectedToLandlordDetailsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.connectedToLandlordDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConnectedToLandlordDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  connectedToLandlordDetailsView: connectedToLandlordDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val containCYA = request.uri
    val forType    = request.sessionData.forType

    containCYA match {
      case containsCYA if containsCYA.contains("=CYA") =>
        audit.sendExplicitAudit(
          "cya-change-link",
          ChangeLinkAudit(forType.toString, request.uri, "ConnectedToLandlordDetails")
        )
      case _                                           =>
        Future.successful(
          Ok(
            connectedToLandlordDetailsView(
              request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.connectedToLandlordDetails) match {
                case Some(connectedToLandlordDetails) =>
                  connectedToLandlordDetailsForm.fill(connectedToLandlordDetails)
                case _                                => connectedToLandlordDetailsForm
              },
              request.sessionData.toSummary
            )
          )
        )
    }
    Future.successful(
      Ok(
        connectedToLandlordDetailsView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.connectedToLandlordDetails) match {
            case Some(connectedToLandlordDetails) =>
              connectedToLandlordDetailsForm.fill(connectedToLandlordDetails)
            case _                                => connectedToLandlordDetailsForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ConnectedToLandlordInformationDetails](
      connectedToLandlordDetailsForm,
      formWithErrors => BadRequest(connectedToLandlordDetailsView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(connectedToLandlordDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(ConnectedToLandlordDetailsPageId, updatedData).apply(updatedData))
      }
    )
  }

}
