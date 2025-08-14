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

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.AddedMaximumListItemsForm.addedMaximumListItemsForm
import models.submissions.accommodation.AccommodationDetails.updateAccommodationDetails
import models.pages.MaxListItemsPage
import models.pages.MaxListItemsPage.*
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
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
    val answer =
      list match {
        case AccommodationUnits => request.sessionData.accommodationDetails.flatMap(_.exceededMaxUnits)
        case TradeServices      => Some(false) // TODO: get answer from session
      }

    Ok(
      addedMaximumListItemsView(
        addedMaximumListItemsForm(list.itemsInPlural).fill(answer),
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
          val updatedData =
            list match {
              case AccommodationUnits =>
                updateAccommodationDetails(
                  _.copy(
                    exceededMaxUnits = data
                  )
                )
              case TradeServices      => request.sessionData // TODO: update answer in session
            }

          val nextPage = list match {
            case AccommodationUnits => controllers.accommodation.routes.AccommodationDetailsCYA6048Controller.show
            case TradeServices      => controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show()
          }

          session
            .saveOrUpdate(updatedData)
            .map(_ => Redirect(nextPage))
        }
      )
    }

}
