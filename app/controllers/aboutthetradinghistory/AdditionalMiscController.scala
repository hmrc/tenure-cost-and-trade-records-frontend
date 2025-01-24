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
import controllers.{FORDataCaptureController, aboutthetradinghistory}
import form.aboutthetradinghistory.AdditionalMiscForm.additionalMiscForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{AdditionalMisc, AdditionalMiscDetails, TurnoverSection6045}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AdditionalMiscId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.additionalMisc

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AdditionalMiscController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: additionalMisc,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6045 =>
      val years   = turnoverSections6045.map(_.financialYearEnd).map(_.getYear.toString)
      val details = request.sessionData.aboutTheTradingHistoryPartOne
        .flatMap(_.additionalMiscDetails)
        .getOrElse(AdditionalMiscDetails())
      Ok(
        view(
          additionalMiscForm(years).fill(
            (
              turnoverSections6045.map(section =>
                val tradingPeriod = section.additionalShops.fold(52)(_.tradingPeriod)
                section.additionalMisc.getOrElse(AdditionalMisc(tradingPeriod))
              ),
              details
            )
          ),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6045 =>
      val years = turnoverSections6045.map(_.financialYearEnd).map(_.getYear.toString)

      continueOrSaveAsDraft[(Seq[AdditionalMisc], AdditionalMiscDetails)](
        additionalMiscForm(years),
        formWithErrors => BadRequest(view(formWithErrors, getBackLink)),
        success => {
          val updatedSections =
            (success._1 zip turnoverSections6045).map { case (additionalMisc, previousSection) =>
              previousSection.copy(additionalMisc = Some(additionalMisc))
            }

          val details = success._2

          val updatedData = updateAboutTheTradingHistoryPartOne(
            _.copy(
              turnoverSections6045 = Some(updatedSections),
              additionalMiscDetails = Some(details)
            )
          )

          session.saveOrUpdate(updatedData).map { _ =>
            Redirect(
              navigator
                .nextPage6045(
                  AdditionalMiscId,
                  updatedData,
                  navigator.cyaPageForAdditionalActivities
                )
                .apply(updatedData)
            )
          }
        }
      )
    }
  }

  private def runWithSessionCheck(
    action: Seq[TurnoverSection6045] => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6045)
      .filter(_.nonEmpty)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController.show().url
      case _     =>
        aboutthetradinghistory.routes.AdditionalAmusementsController.show().url
    }

}
