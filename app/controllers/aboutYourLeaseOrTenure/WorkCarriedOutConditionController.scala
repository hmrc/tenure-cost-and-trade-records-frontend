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
import form.aboutYourLeaseOrTenure.WorkCarriedOutConditionForm.workCarriedOutConditionForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.WorkCarriedOutCondition
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.WorkCarriedOutConditionId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.workCarriedOutCondition

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class WorkCarriedOutConditionController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  view: workCarriedOutCondition,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      view(
        request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.workCarriedOutCondition) match {
          case Some(data) => workCarriedOutConditionForm.fill(data)
          case _          => workCarriedOutConditionForm
        },
        calculateBackLink,
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[WorkCarriedOutCondition](
      workCarriedOutConditionForm,
      formWithErrors =>
        BadRequest(
          view(formWithErrors, calculateBackLink, request.sessionData.toSummary)
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartThree(_.copy(workCarriedOutCondition = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(WorkCarriedOutConditionId, updatedData).apply(updatedData)))

      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    request.sessionData.aboutLeaseOrAgreementPartThree
      .flatMap(_.propertyUpdates)
      .map(_.updates) match {
      case Some(AnswerYes) =>
        controllers.aboutYourLeaseOrTenure.routes.WorkCarriedOutDetailsController.show().url
      case Some(AnswerNo)  => controllers.aboutYourLeaseOrTenure.routes.PropertyUpdatesController.show().url
      case _               => controllers.routes.TaskListController.show().url
    }

}
