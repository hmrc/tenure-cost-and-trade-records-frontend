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

package controllers.additionalinformation

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.additionalinformation.CheckYourAnswersAdditionalInformationForm.checkYourAnswersAdditionalInformationForm
import models.Session
import models.submissions.additionalinformation.AdditionalInformation.updateAdditionalInformation
import models.submissions.additionalinformation.CheckYourAnswersAdditionalInformation
import navigation.AdditionalInformationNavigator
import navigation.identifiers.CheckYourAnswersAdditionalInformationId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.additionalinformation.checkYourAnswersAdditionalInformation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersAdditionalInformationController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AdditionalInformationNavigator,
  checkYourAnswersAdditionalInformationView: checkYourAnswersAdditionalInformation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersAdditionalInformationView(
          request.sessionData.additionalInformation.flatMap(_.checkYourAnswersAdditionalInformation) match {
            case Some(checkYourAnswersAdditionalInformation) =>
              checkYourAnswersAdditionalInformationForm.fill(checkYourAnswersAdditionalInformation)
            case _                                           => checkYourAnswersAdditionalInformationForm
          },
          request.sessionData,
          getBackLink(request.sessionData)
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CheckYourAnswersAdditionalInformation](
      checkYourAnswersAdditionalInformationForm,
      formWithErrors =>
        BadRequest(
          checkYourAnswersAdditionalInformationView(
            formWithErrors,
            request.sessionData,
            getBackLink(request.sessionData)
          )
        ),
      data => {
        val updatedData = updateAdditionalInformation(_.copy(checkYourAnswersAdditionalInformation = Some(data)))
          .copy(lastCYAPageUrl =
            Some(controllers.additionalinformation.routes.CheckYourAnswersAdditionalInformationController.show().url)
          )
        session.saveOrUpdate(updatedData).flatMap { _ =>
          Future.successful(
            Redirect(navigator.nextPage(CheckYourAnswersAdditionalInformationId, updatedData).apply(updatedData))
          )
        }
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.additionalInformation.flatMap(_.altDetailsQuestion.map(_.contactDetailsQuestion.name)) match {
      case Some("yes") => controllers.additionalinformation.routes.AlternativeContactDetailsController.show().url
      case Some("no")  => controllers.additionalinformation.routes.ContactDetailsQuestionController.show().url
      case _           =>
        logger.warn(s"Back link for additional info CYA page reached with unknown alt details question value")
        controllers.routes.TaskListController.show().url
    }
}
