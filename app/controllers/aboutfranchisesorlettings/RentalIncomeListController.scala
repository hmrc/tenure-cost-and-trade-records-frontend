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

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.RentalIncomeListForm.theForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, Concession6015IncomeRecord, ConcessionIncomeRecord, FranchiseIncomeRecord, IncomeRecord, LettingIncomeRecord}
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.rentalIncomeList as RentalIncomeListView
import views.html.genericRemoveConfirmation as RemoveConfirmationView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class RentalIncomeListController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  theListView: RentalIncomeListView,
  theConfirmationView: RemoveConfirmationView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with FranchiseAndLettingSupport
    with I18nSupport:

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).apply { implicit request =>
    audit.sendChangeLink("RentalIncomeList")
    val freshForm  = theForm
    val filledForm =
      for
        aboutFranchisesOrLettings <- request.sessionData.aboutFranchisesOrLettings
        rentalIncome              <- aboutFranchisesOrLettings.rentalIncome
        incomeRecord              <- rentalIncome.lift(index)
        addAnotherRecord          <- incomeRecord.addAnotherRecord
      yield freshForm.fill(addAnotherRecord)

    Ok(
      theListView(
        filledForm.getOrElse(freshForm),
        index
      )
    )
  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      formWithErrors =>
        successful(
          BadRequest(
            theListView(
              formWithErrors,
              index
            )
          )
        ),
      formData =>
        val numberOfRentalIncomes = request.sessionData.aboutFranchisesOrLettings
          .map(_.rentalIncome.getOrElse(IndexedSeq.empty).size)
          .getOrElse(0)

        if formData == AnswerYes && numberOfRentalIncomes >= 5 && navigator.from != "CYA"
        then successful(Redirect(controllers.routes.MaxOfLettingsReachedController.show(src = Some("rentalIncome"))))
        else
          val rentalIncomeData = request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome)
          rentalIncomeData match
            case Some(entries) if entries.isDefinedAt(index) =>
              val updatedRentalIncome = entries.updated(index, update(entries(index), Some(formData)))
              val updatesSession      = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings(about =>
                about.copy(rentalIncome = Some(updatedRentalIncome))
              )
              repository.saveOrUpdate(updatesSession).map { _ =>
                if formData == AnswerYes
                then Redirect(routes.TypeOfIncomeController.show(Some(entries.size)))
                else Redirect(routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())
              }
            case Some(entries) if entries.isEmpty            =>
              if formData == AnswerYes
              then Redirect(routes.TypeOfIncomeController.show())
              else Redirect(routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())
            case _                                           =>
              successful(
                Redirect(
                  routes.RentalIncomeListController.show(rentalIncomeData.map(_.size).getOrElse(0))
                )
              )
    )
  }

  private def update(
    incomeRecord: IncomeRecord,
    answer: Option[AnswersYesNo]
  ): IncomeRecord = incomeRecord match {
    case franchise: FranchiseIncomeRecord           => franchise.copy(addAnotherRecord = answer)
    case concession: ConcessionIncomeRecord         => concession.copy(addAnotherRecord = answer)
    case concession6015: Concession6015IncomeRecord => concession6015.copy(addAnotherRecord = answer)
    case letting: LettingIncomeRecord               => letting.copy(addAnotherRecord = answer)
  }

  def remove(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Some(getOperatorName(idx))
      .map { operatorName =>
        successful(
          Ok(
            theConfirmationView(
              confirmableActionForm,
              operatorName,
              controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.performRemove(idx),
              controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(idx)
            )
          )
        )
      }
      .getOrElse(Redirect(controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(0)))
  }

  def performRemove(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        Some(getOperatorName(idx))
          .map { operatorName =>
            successful(
              BadRequest(
                theConfirmationView(
                  formWithErrors,
                  operatorName,
                  controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.performRemove(idx),
                  controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(idx)
                )
              )
            )
          }
          .getOrElse(
            Redirect(controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(0))
          ),
      {
        case AnswerYes =>
          val aboutFranchisesOrLettings =
            request.sessionData.aboutFranchisesOrLettings.getOrElse(AboutFranchisesOrLettings())
          val incomeRecords             = aboutFranchisesOrLettings.rentalIncome.getOrElse(IndexedSeq.empty)
          if idx >= 0 && idx < incomeRecords.length
          then
            val updatedRecords = incomeRecords.patch(idx, Nil, 1)
            val updatedAbout   = aboutFranchisesOrLettings.copy(rentalIncome = Some(updatedRecords))
            repository.saveOrUpdate(request.sessionData.copy(aboutFranchisesOrLettings = Some(updatedAbout))).map { _ =>
              Redirect(
                controllers.aboutfranchisesorlettings.routes.RentalIncomeListController
                  .show(if (updatedRecords.isEmpty) 0 else updatedRecords.length - 1)
              )
            }
          else
            successful(
              Redirect(controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(0))
            )

        case AnswerNo =>
          Redirect(controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(idx))
      }
    )
  }
