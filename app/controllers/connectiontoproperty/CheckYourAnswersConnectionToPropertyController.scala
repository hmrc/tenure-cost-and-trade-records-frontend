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
import form.CheckYourAnswersAndConfirmForm.theForm
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.Session
import models.submissions.common.CheckYourAnswersAndConfirm
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.CheckYourAnswersConnectionToPropertyId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.checkYourAnswersConnectionToProperty as CheckYourAnswersConnectionToPropertyView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersConnectionToPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  theView: CheckYourAnswersConnectionToPropertyView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repo: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val freshForm  = theForm
    val filledForm =
      for
        stillConnectedDetails      <- request.sessionData.stillConnectedDetails
        checkYourAnswersAndConfirm <- stillConnectedDetails.checkYourAnswersConnectionToProperty
      yield theForm.fill(checkYourAnswersAndConfirm)

    Ok(
      theView(
        filledForm.getOrElse(freshForm),
        getBackLink,
        request.sessionData.toSummary
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CheckYourAnswersAndConfirm](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(
            formWithErrors,
            getBackLink,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(checkYourAnswersConnectionToProperty = Some(data)))
          .copy(lastCYAPageUrl =
            Some(controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url)
          )
        repo.saveOrUpdate(updatedData).flatMap { _ =>
          Future.successful(
            Redirect(navigator.nextPage(CheckYourAnswersConnectionToPropertyId, updatedData).apply(updatedData))
          )
        }
      }
    )
  }

  private def getBackLink: String =
    controllers.connectiontoproperty.routes.AreYouThirdPartyController.show().url
