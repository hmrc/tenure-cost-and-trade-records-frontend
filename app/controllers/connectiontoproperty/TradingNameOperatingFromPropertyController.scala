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

package controllers.connectiontoproperty

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.connectiontoproperty.TradingNameOperatingFromPropertyForm.tradingNameOperatingFromPropertyForm
import form.connectiontoproperty.TradingNameOperatingFromProperty6048Form.tradingNameOperatingFromProperty6048Form
import models.ForType
import models.ForType.*
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.{AddressConnectionTypeYes, AddressConnectionTypeYesChangeAddress, TradingNameOperatingFromProperty}
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
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  nameOfBusinessOperatingFromPropertyView: tradingNameOperatingFromProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  private def forType(implicit request: SessionRequest[?]): ForType = request.sessionData.forType

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("TradingNameOperatingFromProperty")

    if (forType == FOR6048) {
      Future.successful(
        Ok(
          nameOfBusinessOperatingFromPropertyView(
            request.sessionData.stillConnectedDetails.flatMap(_.tradingNameOperatingFromProperty) match {
              case Some(vacantProperties) => tradingNameOperatingFromProperty6048Form.fill(vacantProperties)
              case _                      => tradingNameOperatingFromProperty6048Form
            },
            calculateBackLink,
            request.sessionData.toSummary,
            navigator.from
          )
        )
      )
    } else {
      Future.successful(
        Ok(
          nameOfBusinessOperatingFromPropertyView(
            request.sessionData.stillConnectedDetails.flatMap(_.tradingNameOperatingFromProperty) match {
              case Some(vacantProperties) => tradingNameOperatingFromPropertyForm.fill(vacantProperties)
              case _                      => tradingNameOperatingFromPropertyForm
            },
            calculateBackLink,
            request.sessionData.toSummary,
            navigator.from
          )
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    if (forType == FOR6048) {
      continueOrSaveAsDraft[TradingNameOperatingFromProperty](
        tradingNameOperatingFromProperty6048Form,
        formWithErrors =>
          BadRequest(
            nameOfBusinessOperatingFromPropertyView(
              formWithErrors,
              calculateBackLink,
              request.sessionData.toSummary
            )
          ),
        data => {
          val updatedData = updateStillConnectedDetails(_.copy(tradingNameOperatingFromProperty = Some(data)))
          session.saveOrUpdate(updatedData).map { _ =>
            val redirectToCYA = navigator.cyaPage.filter(_ => navigator.from(using request) == "CYA")
            val nextPage      =
              redirectToCYA
                .getOrElse(navigator.nextPage(TradingNameOperatingFromPropertyPageId, updatedData).apply(updatedData))
            Redirect(nextPage)
          }
        }
      )
    } else {
      continueOrSaveAsDraft[TradingNameOperatingFromProperty](
        tradingNameOperatingFromPropertyForm,
        formWithErrors =>
          BadRequest(
            nameOfBusinessOperatingFromPropertyView(
              formWithErrors,
              calculateBackLink,
              request.sessionData.toSummary
            )
          ),
        data => {
          val updatedData = updateStillConnectedDetails(_.copy(tradingNameOperatingFromProperty = Some(data)))
          session.saveOrUpdate(updatedData).map { _ =>
            val redirectToCYA = navigator.cyaPage.filter(_ => navigator.from(using request) == "CYA")
            val nextPage      =
              redirectToCYA
                .getOrElse(navigator.nextPage(TradingNameOperatingFromPropertyPageId, updatedData).apply(updatedData))
            Redirect(nextPage)
          }
        }
      )
    }
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "TL"  =>
        controllers.routes.TaskListController.show().url + "#name-of-operator-from-property"
      case "CYA" => controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url
      case _     =>
        request.sessionData.forType match {
          case FOR6076 =>
            request.sessionData.stillConnectedDetails.flatMap(_.addressConnectionType) match {
              case Some(AddressConnectionTypeYes)              =>
                controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
              case Some(AddressConnectionTypeYesChangeAddress) =>
                controllers.connectiontoproperty.routes.EditAddressController.show().url
              case _                                           =>
                controllers.routes.TaskListController.show().url
            }
          case _       => controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
        }

    }
}
