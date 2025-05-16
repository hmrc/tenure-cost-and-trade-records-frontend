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
import form.aboutfranchisesorlettings.IncomeRecordIncludedForm.incomeRecordIncludedForm as theForm
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, Concession6015IncomeRecord, FranchiseIncomeRecord, LettingIncomeRecord}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.RentalIncomeIncludedId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.rentalIncomeIncluded

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RentalIncomeIncludedController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  view: rentalIncomeIncluded,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with FranchiseAndLettingSupport
    with Logging {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails = getIncomeRecord(index).collect {
      case letting: LettingIncomeRecord               => letting.itemsIncluded
      case concession6015: Concession6015IncomeRecord => concession6015.itemsIncluded
      case franchise: FranchiseIncomeRecord           => franchise.itemsIncluded
      case _                                          => None
    }.flatten
    audit.sendChangeLink("LettingTypeIncluded")

    Ok(
      view(
        existingDetails.fold(theForm)(
          theForm.fill
        ),
        index,
        getOperatorName(index),
        calculateBackLink(index),
        request.sessionData.toSummary,
        request.sessionData.forType
      )
    )
  }

  def submit(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[List[String]](
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
                  case franchise: FranchiseIncomeRecord       => franchise.copy(itemsIncluded = Some(data))
                  case concession: Concession6015IncomeRecord => concession.copy(itemsIncluded = Some(data))
                  case letting: LettingIncomeRecord           => letting.copy(itemsIncluded = Some(data))
                  case _                                      => throw new IllegalStateException("Unknown income record type")
                }
              )
            }
            aboutFranchisesOrLettings.copy(rentalIncome = updatedRentalIncome)
          } else aboutFranchisesOrLettings
        }(using request)

        session.saveOrUpdate(updatedSession).map { _ =>
          Redirect(navigator.nextPage(RentalIncomeIncludedId, updatedSession).apply(updatedSession))

        }
      }
    )
  }

  private def calculateBackLink(idx: Int)(implicit request: SessionRequest[AnyContent]) =
    request.getQueryString("from") match {
      case Some("CYA") =>
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      case _           =>
        request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome).flatMap(_.lift(idx)) match {
          case Some(_: Concession6015IncomeRecord) =>
            controllers.aboutfranchisesorlettings.routes.CalculatingTheRentForController.show(idx).url
          case _                                   =>
            controllers.aboutfranchisesorlettings.routes.RentalIncomeRentController.show(idx).url
        }
    }
}
