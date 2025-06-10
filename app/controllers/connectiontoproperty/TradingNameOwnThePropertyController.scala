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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import models.submissions.common.AnswersYesNo
import navigation.ConnectionToPropertyNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.tradingNameOwnTheProperty as TradingNameOwnThePropertyView
import form.connectiontoproperty.TradingNameOwnThePropertyForm.theForm
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import navigation.identifiers.TradingNameOwnThePropertyPageId

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TradingNameOwnThePropertyController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  theView: TradingNameOwnThePropertyView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repo: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with ReadOnlySupport
    with I18nSupport
    with Logging:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("TradingNameOwnTheProperty")
    Ok(
      theView(
        ownThePropertyInSession match {
          case Some(ownTheProperty) => theForm.fill(ownTheProperty)
          case _                    => theForm
        },
        getBackLink,
        request.sessionData.stillConnectedDetails
          .flatMap(_.tradingNameOperatingFromProperty.map(_.tradingName))
          .getOrElse(""),
        request.sessionData.toSummary,
        isReadOnly
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(
            formWithErrors,
            getBackLink,
            request.sessionData.stillConnectedDetails
              .flatMap(_.tradingNameOperatingFromProperty.map(_.tradingName))
              .getOrElse(""),
            request.sessionData.toSummary,
            isReadOnly
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(tradingNameOwnTheProperty = Some(data)))
        repo
          .saveOrUpdate(updatedData)
          .map { _ =>
            navigator
              .cyaPageDependsOnSession(updatedData)
              .filter(_ => navigator.from == "CYA" && ownThePropertyInSession.contains(data))
              .getOrElse(navigator.nextPage(TradingNameOwnThePropertyPageId, updatedData).apply(updatedData))
          }
          .map(Redirect)
      }
    )
  }

  private def ownThePropertyInSession(implicit
    request: SessionRequest[AnyContent]
  ): Option[AnswersYesNo] =
    request.sessionData.stillConnectedDetails.flatMap(_.tradingNameOwnTheProperty)

  private def getBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" =>
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url
      case _     => controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show().url
    }
