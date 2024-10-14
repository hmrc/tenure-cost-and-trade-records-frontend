/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.aboutfranchisesorlettings

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.CateringOperationOrLettingAccommodationRentIncludesForm.cateringOperationOrLettingAccommodationRentIncludesForm
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, LettingIncomeRecord}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.LettingTypeIncludedId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.lettingTypeIncluded

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LettingTypeIncludedController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  view: lettingTypeIncluded,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  private def getLettingIncomeRecord(index: Int)(implicit request: SessionRequest[?]): Option[LettingIncomeRecord] =
    for {
      allRecords <- request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome)
      record     <- allRecords.lift(index)
      letting    <- record match {
                      case letting: LettingIncomeRecord => Some(letting)
                      case _                            => None
                    }
    } yield letting

  private def getOperatorName(index: Int)(implicit request: SessionRequest[?]): String =
    getLettingIncomeRecord(index).flatMap(_.operatorDetails.map(_.operatorName)).getOrElse("")

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails = getLettingIncomeRecord(index).flatMap(_.itemsIncluded)
    val operatorName    = getOperatorName(index)

    Ok(
      view(
        existingDetails.fold(cateringOperationOrLettingAccommodationRentIncludesForm)(
          cateringOperationOrLettingAccommodationRentIncludesForm.fill
        ),
        index,
        operatorName,
        calculateBackLink(index),
        request.sessionData.toSummary,
        request.sessionData.forType
      )
    )
  }

  def submit(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>

    val operatorName = getOperatorName(idx)

    continueOrSaveAsDraft[List[String]](
      cateringOperationOrLettingAccommodationRentIncludesForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            idx,
            operatorName,
            calculateBackLink(idx),
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      data => {
        val updatedSession = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings { aboutFranchisesOrLettings =>
          if (aboutFranchisesOrLettings.rentalIncome.exists(_.isDefinedAt(idx))) {
            val updatedRentalIncome = aboutFranchisesOrLettings.rentalIncome.map { records =>
              records.updated(idx, records(idx).asInstanceOf[LettingIncomeRecord].copy(itemsIncluded = Some(data)))
            }
            aboutFranchisesOrLettings.copy(rentalIncome = updatedRentalIncome)
          } else {
            aboutFranchisesOrLettings
          }
        }(request)

        session.saveOrUpdate(updatedSession).map { _ =>
          Redirect(navigator.nextPage(LettingTypeIncludedId, updatedSession).apply(updatedSession))

        }
      }
    )
  }

  private def calculateBackLink(idx: Int)(implicit request: SessionRequest[AnyContent]) =
    request.getQueryString("from") match {
      case Some("CYA") =>
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      case _           => controllers.aboutfranchisesorlettings.routes.LettingTypeRentController.show(idx).url
    }
}
