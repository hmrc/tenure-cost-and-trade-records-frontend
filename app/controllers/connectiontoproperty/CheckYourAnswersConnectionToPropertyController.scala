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

package controllers.connectiontoproperty

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.connectiontoproperty.CheckYourAnswersConnectionToPropertyForm.checkYourAnswersConnectionToPropertyForm
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.CheckYourAnswersConnectionToProperty
import models.{ForTypes, Session}
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.CheckYourAnswersAboutThePropertyPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.checkYourAnswersConnectionToProperty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CheckYourAnswersConnectionToPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  checkYourAnswersConnectionToPropertyView: checkYourAnswersConnectionToProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersConnectionToPropertyView(
          request.sessionData.stillConnectedDetails.flatMap(_.checkYourAnswersConnectionToProperty) match {
            case Some(checkYourAnswersAboutTheProperty) =>
              checkYourAnswersConnectionToPropertyForm.fillAndValidate(checkYourAnswersAboutTheProperty)
            case _                                      => checkYourAnswersConnectionToPropertyForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CheckYourAnswersConnectionToProperty](
      checkYourAnswersConnectionToPropertyForm,
      formWithErrors =>
        BadRequest(
          checkYourAnswersConnectionToPropertyView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(checkYourAnswersConnectionToProperty = Some(data)))
          .copy(lastCYAPageUrl =
            Some(controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url)
          )
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(CheckYourAnswersAboutThePropertyPageId, updatedData).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session): String =
    controllers.connectiontoproperty.routes.AreYouThirdPartyController.show().url
}
