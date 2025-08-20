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

package controllers

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.AddedMaximumListItemsForm.addedMaximumListItemsForm
import models.ForType.*
import models.pages.ListPageConfig
import models.pages.ListPageConfig.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.accommodation.AccommodationDetails.updateAccommodationDetails
import models.{ForType, Session}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.addedMaximumListItems

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class AddedMaximumListItemsController @Inject() (
  addedMaximumListItemsView: addedMaximumListItems,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(list: ListPageConfig): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(
      addedMaximumListItemsView(
        addedMaximumListItemsForm(list.itemsInPluralKey).fill(readAnswer(list)),
        list
      )
    )
  }

  def submit(list: ListPageConfig): Action[AnyContent] =
    (Action andThen withSessionRefiner).async { implicit request =>
      continueOrSaveAsDraft[Option[Boolean]](
        addedMaximumListItemsForm(list.itemsInPluralKey),
        formWithErrors => BadRequest(addedMaximumListItemsView(formWithErrors, list)),
        data => {
          val updatedData = saveAnswer(list, data)

          session
            .saveOrUpdate(updatedData)
            .map(_ => Redirect(nextPage(list)))
        }
      )
    }

  private def sessionData(using request: SessionRequest[AnyContent]): Session = request.sessionData

  private def forType(using request: SessionRequest[AnyContent]): ForType = sessionData.forType

  private def readAnswer(list: ListPageConfig)(using request: SessionRequest[AnyContent]): Option[Boolean] =
    list match {
      case AccommodationUnits     => sessionData.accommodationDetails.flatMap(_.exceededMaxUnits)
      case TradeServices          => sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.exceededMaxTradeServices)
      case ServicesPaidSeparately => sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.exceededMaxServicesPaid)
      case BunkerFuelCards        => sessionData.aboutTheTradingHistory.flatMap(_.exceededMaxBunkerFuelCards)
      case LowMarginFuelCards     => sessionData.aboutTheTradingHistory.flatMap(_.exceededMaxLowMarginFuelCards)
    }

  private def saveAnswer(list: ListPageConfig, data: Option[Boolean])(using
    request: SessionRequest[AnyContent]
  ): Session =
    list match {
      case AccommodationUnits     =>
        updateAccommodationDetails(
          _.copy(exceededMaxUnits = data)
        )
      case TradeServices          =>
        updateAboutLeaseOrAgreementPartThree(
          _.copy(exceededMaxTradeServices = data)
        )
      case ServicesPaidSeparately =>
        updateAboutLeaseOrAgreementPartThree(
          _.copy(exceededMaxServicesPaid = data)
        )
      case BunkerFuelCards        =>
        updateAboutTheTradingHistory(
          _.copy(exceededMaxBunkerFuelCards = data)
        )
      case LowMarginFuelCards     =>
        updateAboutTheTradingHistory(
          _.copy(exceededMaxLowMarginFuelCards = data)
        )
    }

  private def nextPage(list: ListPageConfig)(using request: SessionRequest[AnyContent]): Call =
    list match {
      case AccommodationUnits     => controllers.accommodation.routes.AccommodationDetailsCYA6048Controller.show
      case TradeServices          => controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show()
      case ServicesPaidSeparately =>
        forType match {
          case FOR6020 => aboutYourLeaseOrTenure.routes.DoesRentIncludeParkingController.show()
          case _       => aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show()
        }
      case BunkerFuelCards        => controllers.aboutthetradinghistory.routes.CustomerCreditAccountsController.show()
      case LowMarginFuelCards     => controllers.aboutthetradinghistory.routes.NonFuelTurnoverController.show()
    }

}
