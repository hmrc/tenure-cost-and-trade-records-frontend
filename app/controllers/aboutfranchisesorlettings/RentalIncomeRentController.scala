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

package controllers.aboutfranchisesorlettings

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.IncomeRecordRentForm.incomeRecordRentForm as theForm
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, FranchiseIncomeRecord, LettingIncomeRecord, PropertyRentDetails}
import models.submissions.aboutfranchisesorlettings.TypeOfIncome.TypeLetting
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.RentalIncomeRentId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.rentalIncomeRent

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import controllers.toOpt

@Singleton
class RentalIncomeRentController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  view: rentalIncomeRent,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with FranchiseAndLettingSupport
    with I18nSupport
    with Logging {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails = getIncomeRecord(index).collect {
      case letting: LettingIncomeRecord     => letting.rent
      case franchise: FranchiseIncomeRecord => franchise.rent
      case _                                => None
    }.flatten

    audit.sendChangeLink("LettingTypeRent")

    Ok(
      view(
        existingDetails.fold(theForm)(theForm.fill),
        index,
        getOperatorName(index),
        calculateBackLink(index),
        request.sessionData.toSummary,
        request.sessionData.forType
      )
    )
  }

  def submit(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PropertyRentDetails](
      theForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            idx,
            getOperatorName(idx),
            calculateBackLink(idx),
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      data => {
        val updatedSession = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings { aboutFranchisesOrLettings =>
          if (aboutFranchisesOrLettings.rentalIncome.exists(_.isDefinedAt(idx))) {
            val updatedRentalIncome = aboutFranchisesOrLettings.rentalIncome.map { records =>
              records.updated(
                idx,
                records(idx) match {
                  case franchise: FranchiseIncomeRecord => franchise.copy(rent = Some(data))
                  case letting: LettingIncomeRecord     => letting.copy(rent = Some(data))
                  case _                                => throw new IllegalStateException("Unknown income record type")
                }
              )
            }
            aboutFranchisesOrLettings.copy(rentalIncome = updatedRentalIncome)
          } else {
            aboutFranchisesOrLettings
          }
        }(using request)

        session.saveOrUpdate(updatedSession).map { _ =>
          Redirect(navigator.nextPage(RentalIncomeRentId, updatedSession).apply(updatedSession))
        }
      }
    )
  }

  private def calculateBackLink(idx: Int)(implicit request: SessionRequest[AnyContent]) =
    request.getQueryString("from") match {
      case Some("CYA") =>
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      case _           =>
        getIncomeRecord(idx).flatMap(_.sourceType) match
          case Some(TypeLetting) =>
            controllers.aboutfranchisesorlettings.routes.LettingTypeDetailsController.show(idx).url
          case _                 => controllers.aboutfranchisesorlettings.routes.FranchiseTypeDetailsController.show(idx).url
    }
}
