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
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.TenantsAdditionsDisregardedForm.tenantsAdditionsDisregardedForm
import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.TenantAdditionsDisregardedDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.TenantsAdditionsDisregardedId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.tenantsAdditionsDisregarded
import controllers.toOpt

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TenantsAdditionsDisregardedController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  tenantsAdditionsDisregardedView: tenantsAdditionsDisregarded,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      tenantsAdditionsDisregardedView(
        request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.tenantAdditionsDisregardedDetails) match {
          case Some(data) => tenantsAdditionsDisregardedForm.fill(data)
          case _          => tenantsAdditionsDisregardedForm
        },
        getBackLink,
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[TenantAdditionsDisregardedDetails](
      tenantsAdditionsDisregardedForm,
      formWithErrors =>
        BadRequest(tenantsAdditionsDisregardedView(formWithErrors, getBackLink, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(tenantAdditionsDisregardedDetails = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(TenantsAdditionsDisregardedId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    request.sessionData.forType match {
      case FOR6020           => controllers.aboutYourLeaseOrTenure.routes.WorkCarriedOutConditionController.show().url
      case FOR6045 | FOR6046 =>
        request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(
          _.incentivesPaymentsConditionsDetails.flatMap(_.formerLeaseSurrendered.name)
        ) match {
          case Some("yes") =>
            controllers.aboutYourLeaseOrTenure.routes.SurrenderLeaseAgreementDetailsController.show().url
          case _           => controllers.aboutYourLeaseOrTenure.routes.IncentivesPaymentsConditionsController.show().url
        }
      case _                 =>
        controllers.aboutYourLeaseOrTenure.routes.IncentivesPaymentsConditionsController.show().url
    }

}
