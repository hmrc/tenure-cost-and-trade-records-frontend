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
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.RentalIncomeListForm.rentalIncomeListForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, ConcessionIncomeRecord, FranchiseIncomeRecord, IncomeRecord, LettingIncomeRecord}
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.rentalIncomeList
import views.html.genericRemoveConfirmation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RentalIncomeListController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  view: rentalIncomeList,
  genericRemoveConfirmationView: genericRemoveConfirmation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  private def franchisesOrLettingsData(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutFranchisesOrLettings] =
    request.sessionData.aboutFranchisesOrLettings

  private def getOperatorName(idx: Int)(implicit request: SessionRequest[AnyContent]): Option[String] =
    franchisesOrLettingsData.flatMap { data =>
      data.rentalIncome.flatMap { incomeRecord =>
        incomeRecord.lift(idx).map {
          case franchise: FranchiseIncomeRecord   => franchise.businessDetails.fold("")(_.operatorName)
          case concession: ConcessionIncomeRecord => concession.businessDetails.map(_.operatorName).getOrElse("")
          case letting: LettingIncomeRecord       => letting.operatorDetails.map(_.operatorName).getOrElse("")
        }
      }
    }

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>

    val addAnother: Option[AnswersYesNo] = for {
      data      <- franchisesOrLettingsData
      income    <- data.rentalIncome
      record    <- income.lift(index)
      addRecord <- record.addAnotherRecord
    } yield addRecord
    audit.sendChangeLink("RentalIncomeList")

    Future.successful(
      Ok(
        view(
          addAnother.fold(rentalIncomeListForm)(rentalIncomeListForm.fill),
          index,
          getBackLink(index),
          request.sessionData.forType
        )
      )
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    val rentalIncomeData           = request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome)
    val numberOfRentalIncomes: Int =
      request.sessionData.aboutFranchisesOrLettings.map(_.rentalIncome.getOrElse(IndexedSeq.empty).size).getOrElse(0)
    continueOrSaveAsDraft[AnswersYesNo](
      rentalIncomeListForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            index,
            getBackLink(index),
            request.sessionData.forType
          )
        ),
      answer =>
        if (answer == AnswerYes && numberOfRentalIncomes >= 5 && navigator.from != "CYA") {
          Future.successful(Redirect(controllers.routes.MaxOfLettingsReachedController.show(Some("rentalIncome"))))
        } else {
          rentalIncomeData match {
            case Some(entries) if entries.isDefinedAt(index) =>
              val updatedRentalIncome: IndexedSeq[IncomeRecord] =
                entries.updated(index, update(entries(index), Some(answer)))
              val updatesSession                                = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings(about =>
                about.copy(rentalIncome = Some(updatedRentalIncome))
              )
              session.saveOrUpdate(updatesSession).map { _ =>
                if (answer == AnswerYes) {
                  Redirect(routes.TypeOfIncomeController.show(Some(index + 1)))
                } else {
                  Redirect(routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())
                }
              }
            case Some(entries) if entries.isEmpty            =>
              if (answer == AnswerYes) {
                Redirect(routes.TypeOfIncomeController.show())
              } else {
                Redirect(routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())
              }
            case _                                           =>
              Future.successful(
                Redirect(
                  routes.RentalIncomeListController.show(rentalIncomeData.map(_.size).getOrElse(0))
                )
              )
          }
        }
    )
  }

  private def update(
    incomeRecord: IncomeRecord,
    answer: Option[AnswersYesNo]
  ): IncomeRecord = incomeRecord match {
    case franchise: FranchiseIncomeRecord   => franchise.copy(addAnotherRecord = answer)
    case concession: ConcessionIncomeRecord => concession.copy(addAnotherRecord = answer)
    case letting: LettingIncomeRecord       => letting.copy(addAnotherRecord = answer)
  }

  def remove(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    getOperatorName(idx)
      .map { operatorName =>
        Future.successful(
          Ok(
            genericRemoveConfirmationView(
              confirmableActionForm,
              operatorName,
              "label.section.aboutTheLettings",
              request.sessionData.toSummary,
              idx,
              controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.performRemove(idx),
              controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(idx),
              Some(request.sessionData.forType)
            )
          )
        )
      }
      .getOrElse(Redirect(controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(0)))
  }

  def performRemove(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        getOperatorName(idx)
          .map { operatorName =>
            Future.successful(
              BadRequest(
                genericRemoveConfirmationView(
                  formWithErrors,
                  operatorName,
                  "label.section.aboutTheLettings",
                  request.sessionData.toSummary,
                  idx,
                  controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.performRemove(idx),
                  controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(idx),
                  Some(request.sessionData.forType)
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
          if (idx >= 0 && idx < incomeRecords.length) {
            val updatedRecords = incomeRecords.patch(idx, Nil, 1)
            val updatedAbout   = aboutFranchisesOrLettings.copy(rentalIncome = Some(updatedRecords))
            session.saveOrUpdate(request.sessionData.copy(aboutFranchisesOrLettings = Some(updatedAbout))).map { _ =>
              Redirect(
                controllers.aboutfranchisesorlettings.routes.RentalIncomeListController
                  .show(if (updatedRecords.isEmpty) 0 else updatedRecords.length - 1)
              )
            }
          } else {
            Future.successful(
              Redirect(controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(0))
            )
          }
        case AnswerNo  =>
          Redirect(controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(idx))
      }
    )
  }

  private def getBackLink(idx: Int)(implicit request: SessionRequest[AnyContent]): String = {
    val rentalIncomeData = request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome)

    navigator.from match {
      case "CYA" =>
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      case _     =>
        rentalIncomeData.flatMap(_.lift(idx)) match {
          case Some(incomeRecord: FranchiseIncomeRecord)  =>
            controllers.aboutfranchisesorlettings.routes.RentalIncomeIncludedController.show(idx).url
          case Some(incomeRecord: ConcessionIncomeRecord) =>
            controllers.aboutfranchisesorlettings.routes.ConcessionTypeFeesController.show(idx).url
          case Some(incomeRecord: LettingIncomeRecord)    =>
            controllers.aboutfranchisesorlettings.routes.RentalIncomeIncludedController.show(idx).url
          case _                                          =>
            controllers.routes.TaskListController.show().url
        }

    }
  }
}
