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
import models.Session
import models.submissions.accommodation.AccommodationDetails.updateAccommodationDetails
import models.pages.MaxListItemsPage
import models.pages.MaxListItemsPage.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
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

  def show(list: MaxListItemsPage): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(
      addedMaximumListItemsView(
        addedMaximumListItemsForm(list.itemsInPlural).fill(readAnswer(list)),
        list
      )
    )
  }

  def submit(list: MaxListItemsPage): Action[AnyContent] =
    (Action andThen withSessionRefiner).async { implicit request =>
      continueOrSaveAsDraft[Option[Boolean]](
        addedMaximumListItemsForm(list.itemsInPlural),
        formWithErrors => BadRequest(addedMaximumListItemsView(formWithErrors, list)),
        data => {
          val updatedData = saveAnswer(list, data)

          session
            .saveOrUpdate(updatedData)
            .map(_ => Redirect(nextPage(list)))
        }
      )
    }

  private def readAnswer(list: MaxListItemsPage)(using request: SessionRequest[AnyContent]): Option[Boolean] =
    list match {
      case AccommodationUnits => request.sessionData.accommodationDetails.flatMap(_.exceededMaxUnits)
      case TradeServices      => request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.exceededMaxTradeServices)
    }

  private def saveAnswer(list: MaxListItemsPage, data: Option[Boolean])(using
    request: SessionRequest[AnyContent]
  ): Session =
    list match {
      case AccommodationUnits =>
        updateAccommodationDetails(
          _.copy(exceededMaxUnits = data)
        )
      case TradeServices      =>
        updateAboutLeaseOrAgreementPartThree(
          _.copy(exceededMaxTradeServices = data)
        )
    }

  private def nextPage(list: MaxListItemsPage): Call =
    list match {
      case AccommodationUnits => controllers.accommodation.routes.AccommodationDetailsCYA6048Controller.show
      case TradeServices      => controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show()
    }

}
