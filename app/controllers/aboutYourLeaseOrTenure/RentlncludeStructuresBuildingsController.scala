/*
 * Copyright 2025 HM Revenue & Customs
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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.RentIncludeStructuresBuildingsForm.rentIncludeStructuresBuildingsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartFour.updateAboutLeaseOrAgreementPartFour
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.RentIncludeStructuresBuildingsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.rentIncludeStructuresBuildings

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RentIncludeStructuresBuildingsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentIncludeStructuresBuildingsView: rentIncludeStructuresBuildings,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("RentIncludeStructuresBuildings")

    Future.successful(
      Ok(
        rentIncludeStructuresBuildingsView(
          request.sessionData.aboutLeaseOrAgreementPartFour.flatMap(_.rentIncludeStructuresBuildings) match {
            case Some(rentIncludeStructuresBuildings) =>
              rentIncludeStructuresBuildingsForm.fill(rentIncludeStructuresBuildings)
            case _                                    => rentIncludeStructuresBuildingsForm
          },
          getBackLink
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      rentIncludeStructuresBuildingsForm,
      formWithErrors => BadRequest(rentIncludeStructuresBuildingsView(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartFour(_.copy(rentIncludeStructuresBuildings = Some(data)))
        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(RentIncludeStructuresBuildingsId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.rentDevelopedLand) match {
      case Some(AnswerYes) => controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandDetailsController.show().url
      case _               => controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandController.show().url
    }

}
