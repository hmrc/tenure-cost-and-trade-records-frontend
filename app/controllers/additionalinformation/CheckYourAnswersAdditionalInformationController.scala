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
import navigation.AdditionalInformationNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.additionalinformation.checkYourAnswersAdditionalInformation
import views.html.taskList

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CheckYourAnswersAdditionalInformationController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AdditionalInformationNavigator,
  checkYourAnswersAdditionalInformationView: checkYourAnswersAdditionalInformation,
  taskListView: taskList,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersAdditionalInformationView(
          request.sessionData.additionalInformation.flatMap(_.checkYourAnswersAdditionalInformation) match {
            case Some(checkYourAnswersAdditionalInformation) =>
              checkYourAnswersAdditionalInformationForm.fillAndValidate(checkYourAnswersAdditionalInformation)
            case _                                           => checkYourAnswersAdditionalInformationForm
          },
          request.sessionData
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft(
      Ok(taskListView())
    )
  }

}
