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
import form.connectiontoproperty.TradingNameOperatingFromPropertyForm.tradingNameOperatingFromProperty
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.TradingNameOperatingFromProperty
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.TradingNameOperatingFromPropertyPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.tradingNameOperatingFromProperty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TradingNameOperatingFromPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  nameOfBusinessOperatingFromPropertyView: tradingNameOperatingFromProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        nameOfBusinessOperatingFromPropertyView(
          request.sessionData.stillConnectedDetails.flatMap(_.tradingNameOperatingFromProperty) match {
            case Some(vacantProperties) => tradingNameOperatingFromProperty.fill(vacantProperties)
            case _                      => tradingNameOperatingFromProperty
          },
          request.sessionData.toSummary,
          navigator.from
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[TradingNameOperatingFromProperty](
      tradingNameOperatingFromProperty,
      formWithErrors =>
        BadRequest(
          nameOfBusinessOperatingFromPropertyView(
            formWithErrors,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(tradingNameOperatingFromProperty = Some(data)))
        session.saveOrUpdate(updatedData).map { _ =>
          val redirectToCYA = navigator.cyaPage.filter(_ => navigator.from(request) == "CYA")
          val nextPage      =
            redirectToCYA
              .getOrElse(navigator.nextPage(TradingNameOperatingFromPropertyPageId, updatedData).apply(updatedData))
          Redirect(nextPage)
        }
      }
    )
  }
}
