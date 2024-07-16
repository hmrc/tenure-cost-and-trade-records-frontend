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

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.CheckYourAnswersOtherHolidayAccommodationForm.checkYourAnswersOtherHolidayAccommodationForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateOtherHolidayAccommodation
import models.submissions.aboutthetradinghistory.CheckYourAnswersOtherHolidayAccommodation
import models.Session
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.CheckYourAnswersOtherHolidayAccommodationId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.checkYourAnswersOtherHolidayAccommodation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersOtherHolidayAccommodationController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  checkYourAnswersOtherHolidayAccommodationView: checkYourAnswersOtherHolidayAccommodation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersOtherHolidayAccommodationView(
          request.sessionData.aboutTheTradingHistoryPartOne
            .flatMap(_.otherHolidayAccommodation.flatMap(_.checkYourAnswersOtherHolidayAccommodation)) match {
            case Some(checkYourAnswersAboutTheTradingHistory) =>
              checkYourAnswersOtherHolidayAccommodationForm.fill(checkYourAnswersAboutTheTradingHistory)
            case _                                            => checkYourAnswersOtherHolidayAccommodationForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CheckYourAnswersOtherHolidayAccommodation](
      checkYourAnswersOtherHolidayAccommodationForm,
      formWithErrors =>
        BadRequest(
          checkYourAnswersOtherHolidayAccommodationView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData =
          updateOtherHolidayAccommodation(_.copy(checkYourAnswersOtherHolidayAccommodation = Some(data)))

        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(CheckYourAnswersOtherHolidayAccommodationId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def getBackLink(answers: Session): String =
    controllers.aboutthetradinghistory.routes.OtherHolidayAccommodationController.show().url
}
