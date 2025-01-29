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

package controllers.aboutYourLeaseOrTenure

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.UltimatelyResponsibleOutsideRepairsForm.ultimatelyResponsibleOutsideRepairsForm
import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.UltimatelyResponsibleOutsideRepairs
import models.submissions.common.AnswerYes
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.UltimatelyResponsibleOutsideRepairsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.ultimatelyResponsibleOutsideRepairs

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UltimatelyResponsibleOutsideRepairsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  ultimatelyResponsibleORView: ultimatelyResponsibleOutsideRepairs,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("UltimatelyResponsibleOutsideRepairs")

    Future.successful(
      Ok(
        ultimatelyResponsibleORView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.ultimatelyResponsibleOutsideRepairs) match {
            case Some(ultimatelyResponsible) => ultimatelyResponsibleOutsideRepairsForm.fill(ultimatelyResponsible)
            case _                           => ultimatelyResponsibleOutsideRepairsForm
          },
          getBackLink
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[UltimatelyResponsibleOutsideRepairs](
      ultimatelyResponsibleOutsideRepairsForm,
      formWithErrors => BadRequest(ultimatelyResponsibleORView(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(ultimatelyResponsibleOutsideRepairs = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ =>
            Redirect(navigator.nextPage(UltimatelyResponsibleOutsideRepairsPageId, updatedData).apply(updatedData))
          )

      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    request.sessionData.forType match {
      case FOR6020           =>
        if (
          request.sessionData.aboutLeaseOrAgreementPartOne
            .flatMap(_.includedInYourRentDetails)
            .exists(_.includedInYourRent contains "vat")
        ) {
          controllers.aboutYourLeaseOrTenure.routes.IsVATPayableForWholePropertyController.show().url
        } else {
          controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show().url
        }
      case FOR6045 | FOR6046 =>
        request.sessionData.aboutLeaseOrAgreementPartFour.flatMap(_.rentIncludeStructuresBuildings) match {
          case Some(AnswerYes) =>
            controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsDetailsController.show().url
          case _               => controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsController.show().url
        }
      case _                 => controllers.aboutYourLeaseOrTenure.routes.DoesTheRentPayableController.show().url
    }

}
