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
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.PropertyUseLeasebackArrangementForm.propertyUseLeasebackArrangementForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.Session
import models.submissions.common.AnswersYesNo
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.PropertyUseLeasebackAgreementId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.propertyUseLeasebackArrangement

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class PropertyUseLeasebackArrangementController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  propertyUseLeasebackAgreementView: propertyUseLeasebackArrangement,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        propertyUseLeasebackAgreementView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.propertyUseLeasebackAgreement) match {
            case Some(propertyUseLeasebackAgreement) =>
              propertyUseLeasebackArrangementForm.fill(propertyUseLeasebackAgreement)
            case _                                   => propertyUseLeasebackArrangementForm
          },
          getBackLink(request.sessionData),
          request.sessionData.stillConnectedDetails
            .flatMap(_.tradingNameOperatingFromProperty.map(_.tradingName))
            .getOrElse(""),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      propertyUseLeasebackArrangementForm,
      formWithErrors =>
        BadRequest(
          propertyUseLeasebackAgreementView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.stillConnectedDetails
              .flatMap(_.tradingNameOperatingFromProperty.map(_.tradingName))
              .getOrElse(""),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(propertyUseLeasebackAgreement = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(PropertyUseLeasebackAgreementId, updatedData).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session)(implicit request: Request[AnyContent]): String =
    navigator.from match {
      case "TL" => controllers.routes.TaskListController.show().url + "#leaseback-arrangement"
      case _    => controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show().url
    }

}
