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
import form.aboutthetradinghistory.AboutYourTradingHistoryForm.aboutYourTradingHistoryForm
import models.{ForTypes, Session}
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AboutYourTradingHistoryPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutthetradinghistory.aboutYourTradingHistory

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AboutYourTradingHistoryController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  aboutYourTradingHistoryView: aboutYourTradingHistory,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      aboutYourTradingHistoryView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.aboutYourTradingHistory) match {
          case Some(tradingHistory) => aboutYourTradingHistoryForm.fillAndValidate(tradingHistory)
          case _                    => aboutYourTradingHistoryForm
        },
        getBackLink(request.sessionData) match {
          case Right(link) => link
          case Left(msg)   =>
            logger.warn(s"Navigation for about your trading history page reached with error: $msg")
            throw new RuntimeException(s"Navigation for about your trading history page reached with error $msg")
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    aboutYourTradingHistoryForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              aboutYourTradingHistoryView(
                formWithErrors,
                getBackLink(request.sessionData) match {
                  case Right(link) => link
                  case Left(msg)   =>
                    logger.warn(s"Navigation for about your trading history page reached with error: $msg")
                    throw new RuntimeException(
                      s"Navigation for about your trading history page reached with error $msg"
                    )
                }
              )
            )
          ),
        data => {
          val updatedData = updateAboutTheTradingHistory(_.copy(aboutYourTradingHistory = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(AboutYourTradingHistoryPageId).apply(updatedData)))
        }
      )
  }

  private def getBackLink(answers: Session): Either[String, String] =
    answers.forType match {
      case ForTypes.for6010                    =>
        answers.aboutTheProperty.flatMap(_.tiedForGoods.map(_.name)) match {
          case Some("yes") => Right(controllers.abouttheproperty.routes.TiedForGoodsDetailsController.show().url)
          case Some("no")  => Right(controllers.abouttheproperty.routes.TiedForGoodsController.show().url)
          case _           => Right(controllers.routes.TaskListController.show().url)
        }
      case ForTypes.for6011                    =>
        answers.aboutTheProperty.flatMap(_.enforcementAction.map(_.name)) match {
          case Some("yes") =>
            Right(controllers.abouttheproperty.routes.EnforcementActionBeenTakenDetailsController.show().url)
          case Some("no")  => Right(controllers.abouttheproperty.routes.EnforcementActionBeenTakenController.show().url)
          case _           => Right(controllers.routes.TaskListController.show().url)
        }
      case ForTypes.for6015 | ForTypes.for6016 =>
        answers.aboutTheProperty.flatMap(_.premisesLicenseGrantedDetail.map(_.name)) match {
          case Some("yes") =>
            Right(controllers.abouttheproperty.routes.PremisesLicenseGrantedDetailsController.show().url)
          case Some("no")  => Right(controllers.abouttheproperty.routes.PremisesLicenseGrantedController.show().url)
          case _           => Right(controllers.routes.TaskListController.show().url)
        }
      case _                                   => Left(s"Unknown form type with about your trading history back link")
    }
}
