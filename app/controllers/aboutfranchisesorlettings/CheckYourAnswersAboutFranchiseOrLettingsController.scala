/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.aboutfranchisesorlettings

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.CheckYourAnswersAboutFranchiseOrLettingsForm.checkYourAnswersAboutFranchiseOrLettingsForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.CheckYourAnswersAboutFranchiseOrLettings
import models.submissions.common.AnswersYesNo.*
import models.Session
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.CheckYourAnswersAboutFranchiseOrLettingsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.checkYourAnswersAboutFranchiseOrLettings

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersAboutFranchiseOrLettingsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  checkYourAnswersAboutFranchiseOrLettingsView: checkYourAnswersAboutFranchiseOrLettings,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersAboutFranchiseOrLettingsView(
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.checkYourAnswersAboutFranchiseOrLettings) match {
            case Some(checkYourAnswersAboutFranchiseOrLettings) =>
              checkYourAnswersAboutFranchiseOrLettingsForm.fill(checkYourAnswersAboutFranchiseOrLettings)
            case _                                              => checkYourAnswersAboutFranchiseOrLettingsForm
          },
          getBackLink(request.sessionData)
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CheckYourAnswersAboutFranchiseOrLettings](
      checkYourAnswersAboutFranchiseOrLettingsForm,
      formWithErrors =>
        BadRequest(
          checkYourAnswersAboutFranchiseOrLettingsView(
            formWithErrors,
            getBackLink(request.sessionData)
          )
        ),
      data => {
        val updatedData = updateAboutFranchisesOrLettings(_.copy(checkYourAnswersAboutFranchiseOrLettings = Some(data)))
          .copy(lastCYAPageUrl =
            Some(
              controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
            )
          )
        session.saveOrUpdate(updatedData).flatMap { _ =>
          Future.successful(
            Redirect(navigator.nextPage(CheckYourAnswersAboutFranchiseOrLettingsId, updatedData).apply(updatedData))
          )
        }
      }
    )
  }

  private def getBackLink(
    answers: Session
  ): String =
    answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty) match
      case Some(AnswerNo) =>
        controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url
      case _              =>
        answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty) match
          case Some(AnswerYes) =>
            controllers.aboutfranchisesorlettings.routes.RentalIncomeListController
              .show(answers.aboutFranchisesOrLettings.fold(0)(_.rentalIncomeIndex))
              .url
          case _               => controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url

}
