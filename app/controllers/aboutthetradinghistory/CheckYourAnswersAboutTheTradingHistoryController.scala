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

package controllers.aboutthetradinghistory

import actions.WithSessionRefiner
import form.aboutthetradinghistory.CheckYourAnswersAboutTheTradingHistoryForm.checkYourAnswersAboutTheTradingHistoryForm
import models.{ForTypes, Session}
import navigation.AboutThePropertyNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutthetradinghistory.checkYourAnswersAboutTheTradingHistory
import views.html.taskList

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CheckYourAnswersAboutTheTradingHistoryController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutThePropertyNavigator,
  checkYourAnswersAboutTheTradingHistoryView: checkYourAnswersAboutTheTradingHistory,
  taskListView: taskList,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersAboutTheTradingHistoryView(
          request.sessionData.aboutTheTradingHistory.flatMap(_.checkYourAnswersAboutTheTradingHistory) match {
            case Some(checkYourAnswersAboutTheTradingHistory) =>
              checkYourAnswersAboutTheTradingHistoryForm.fillAndValidate(checkYourAnswersAboutTheTradingHistory)
            case _                                      => checkYourAnswersAboutTheTradingHistoryForm
          },
          getBackLink(request.sessionData)
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(Ok(taskListView()))
  }

  private def getBackLink(answers: Session): String =
    answers.userLoginDetails.forNumber match {
      case ForTypes.for6010                    =>
        answers.aboutTheProperty.flatMap(_.tiedForGoods.map(_.name)) match {
          case Some("yes") => controllers.abouttheproperty.routes.TiedForGoodsDetailsController.show().url
          case Some("no")  => controllers.abouttheproperty.routes.TiedForGoodsController.show().url
          case _           =>
            logger.warn(s"Back link for enforcement action page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
      case ForTypes.for6011                    =>
        answers.aboutTheProperty.flatMap(_.enforcementAction.map(_.name)) match {
          case Some("yes") => controllers.abouttheproperty.routes.EnforcementActionBeenTakenDetailsController.show().url
          case Some("no")  => controllers.abouttheproperty.routes.EnforcementActionBeenTakenController.show().url
          case _           =>
            logger.warn(s"Back link for enforcement action details page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
      case ForTypes.for6015 | ForTypes.for6016 =>
        answers.aboutTheProperty.flatMap(_.premisesLicenseGrantedDetail.map(_.name)) match {
          case Some("yes") => controllers.abouttheproperty.routes.PremisesLicenseGrantedDetailsController.show().url
          case Some("no")  => controllers.abouttheproperty.routes.PremisesLicenseGrantedController.show().url
          case _           =>
            logger.warn(s"Back link for premises license page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
    }
}
