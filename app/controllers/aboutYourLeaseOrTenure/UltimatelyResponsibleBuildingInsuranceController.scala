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
import form.aboutYourLeaseOrTenure.UltimatelyResponsibleIBuildingInsuranceForm.ultimatelyResponsibleBuildingInsuranceForm
import models.audit.ChangeLinkAudit
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.UltimatelyResponsibleBuildingInsurance
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.UltimatelyResponsibleBusinessInsurancePageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.ultimatelyResponsibleBuildingInsurance

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UltimatelyResponsibleBuildingInsuranceController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  ultimatelyResponsibleBIView: ultimatelyResponsibleBuildingInsurance,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val containCYA = request.uri
    val forType    = request.sessionData.forType

    containCYA match {
      case containsCYA if containsCYA.contains("=CYA") =>
        audit.sendExplicitAudit(
          "cya-change-link",
          ChangeLinkAudit(forType.toString, request.uri, "UltimatelyResponsibleBuildingInsurance")
        )
      case _                                           =>
        Future.successful(
          Ok(
            ultimatelyResponsibleBIView(
              request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.ultimatelyResponsibleBuildingInsurance) match {
                case Some(ultimatelyResponsibleBI) =>
                  ultimatelyResponsibleBuildingInsuranceForm.fill(ultimatelyResponsibleBI)
                case _                             => ultimatelyResponsibleBuildingInsuranceForm
              },
              request.sessionData.toSummary
            )
          )
        )
    }
    Future.successful(
      Ok(
        ultimatelyResponsibleBIView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.ultimatelyResponsibleBuildingInsurance) match {
            case Some(ultimatelyResponsibleBI) =>
              ultimatelyResponsibleBuildingInsuranceForm.fill(ultimatelyResponsibleBI)
            case _                             => ultimatelyResponsibleBuildingInsuranceForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[UltimatelyResponsibleBuildingInsurance](
      ultimatelyResponsibleBuildingInsuranceForm,
      formWithErrors => BadRequest(ultimatelyResponsibleBIView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData =
          updateAboutLeaseOrAgreementPartTwo(_.copy(ultimatelyResponsibleBuildingInsurance = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ =>
            Redirect(navigator.nextPage(UltimatelyResponsibleBusinessInsurancePageId, updatedData).apply(updatedData))
          )

      }
    )
  }

}
