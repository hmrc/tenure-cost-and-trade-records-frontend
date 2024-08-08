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

package controllers.aboutthetradinghistory

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.TotalSiteCapacityForm.totalSiteCapacityForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateOtherHolidayAccommodation
import models.submissions.aboutthetradinghistory.TotalSiteCapacity
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TotalSiteCapacityId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.totalSiteCapacity6045

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TotalSiteCapacity6045Controller @Inject() (
  view: totalSiteCapacity6045,
  navigator: AboutTheTradingHistoryNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(
      view(
        aboutTheTradingHistoryPartOne
          .flatMap(_.otherHolidayAccommodation)
          .flatMap(_.totalSiteCapacity)
          .fold(totalSiteCapacityForm)(totalSiteCapacityForm.fill),
        getBackLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[TotalSiteCapacity](
      totalSiteCapacityForm,
      formWithErrors => BadRequest(view(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateOtherHolidayAccommodation(_.copy(totalSiteCapacity = Some(data)))

        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(
            navigator
              .nextPage6045(
                TotalSiteCapacityId,
                updatedData,
                navigator.cyaPageForOtherHolidayAccommodation
              )
              .apply(updatedData)
          )
        }
      }
    )
  }

  private def aboutTheTradingHistoryPartOne(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutTheTradingHistoryPartOne] = request.sessionData.aboutTheTradingHistoryPartOne

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    // TODO update CYA?
    navigator.from match {
      case "TL"  => controllers.routes.TaskListController.show().url
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersOtherHolidayAccommodationController.show().url
      case _     => controllers.aboutthetradinghistory.routes.GrossReceiptsSubLetUnitsController.show().url
    }
}
