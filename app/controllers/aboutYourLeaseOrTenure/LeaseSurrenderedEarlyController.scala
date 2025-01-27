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
import form.aboutYourLeaseOrTenure.LeaseSurrenderedEarlyForm.leaseSurrenderedEarlyForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.LeaseSurrenderedEarly
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.LeaseSurrenderedEarlyId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.leaseSurrenderdEarly

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LeaseSurrenderedEarlyController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  view: leaseSurrenderdEarly,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("LeaseSurrenderedEarly")

    Ok(
      view(
        request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.leaseSurrenderedEarly) match {
          case Some(data) => leaseSurrenderedEarlyForm.fill(data)
          case _          => leaseSurrenderedEarlyForm
        },
        calculateBackLink(request),
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LeaseSurrenderedEarly](
      leaseSurrenderedEarlyForm,
      formWithErrors => BadRequest(view(formWithErrors, calculateBackLink(request), request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartThree(_.copy(leaseSurrenderedEarly = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(LeaseSurrenderedEarlyId, updatedData).apply(updatedData)))

      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    request.sessionData.aboutLeaseOrAgreementPartTwo
      .flatMap(_.tenantAdditionsDisregardedDetails)
      .map(_.tenantAdditionalDisregarded) match {
      case Some(AnswerYes) =>
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController.show().url
      case Some(AnswerNo)  => controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
      case _               => controllers.routes.TaskListController.show().url
    }

}
