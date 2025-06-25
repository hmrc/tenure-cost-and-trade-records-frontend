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
import form.aboutfranchisesorlettings.TypeOfIncomeForm.typeOfIncomeForm
import models.ForType
import models.ForType.*
import models.submissions.aboutfranchisesorlettings.*
import models.submissions.aboutfranchisesorlettings.TypeOfIncome.*
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import views.html.aboutfranchisesorlettings.typeOfIncome

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TypeOfIncomeController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  view: typeOfIncome,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingIncomeRecords                 =
      request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome).getOrElse(IndexedSeq.empty)
    val updatedIndex                          = index match {
      case Some(idx) => idx
      case None      => existingIncomeRecords.length
    }
    val existingDetails: Option[TypeOfIncome] =
      existingIncomeRecords.lift(updatedIndex).map(_.sourceType)
    audit.sendChangeLink("TypeOfIncome")

    Ok(
      view(
        existingDetails.fold(typeOfIncomeForm)(typeOfIncomeForm.fill),
        Some(updatedIndex),
        request.sessionData.toSummary,
        getBackLink,
        forType
      )
    )
  }

  def submit(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>

    val existingIncomeRecords =
      request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome).getOrElse(IndexedSeq.empty)

    if (existingIncomeRecords.length >= 5 && index.isEmpty) {
      Future.successful(
        Redirect(controllers.routes.MaxOfLettingsReachedController.show(Option("typeOfIncome")))
      )
    } else {
      continueOrSaveAsDraft[TypeOfIncome](
        typeOfIncomeForm,
        formWithErrors =>
          BadRequest(
            view(
              formWithErrors,
              index,
              request.sessionData.toSummary,
              getBackLink,
              forType
            )
          ),
        data => {
          val newIncomeRecord       = createIncomeRecord(data)
          val existingIncomeRecords =
            request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome).getOrElse(IndexedSeq.empty)

          index match {
            case Some(idx) if idx >= 0 && idx < existingIncomeRecords.length =>
              val existingRecord = existingIncomeRecords(idx)
              if (existingRecord.getClass == newIncomeRecord.getClass && navigator.from == "CYA") {
                Future.successful(
                  Redirect {
                    controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController
                      .show()
                  }
                )
              } else {
                val updatedRecords = existingIncomeRecords.updated(idx, newIncomeRecord)
                updateSessionAndRedirect(updatedRecords, data, index)
              }
            case _                                                           =>
              val updatedData = existingIncomeRecords :+ newIncomeRecord
              updateSessionAndRedirect(updatedData, data, index)
          }
        }
      )
    }
  }
  private def updateSessionAndRedirect(
    updatedRecords: IndexedSeq[IncomeRecord],
    source: TypeOfIncome,
    updatedIndex: Option[Int]
  )(implicit request: SessionRequest[AnyContent], hc: HeaderCarrier): Future[Result] = {
    val existingFranchisesOrLetting =
      request.sessionData.aboutFranchisesOrLettings.getOrElse(AboutFranchisesOrLettings())
    val updatedSession              = request.sessionData.copy(
      aboutFranchisesOrLettings = Option(
        existingFranchisesOrLetting.copy(
          rentalIncome = Option(updatedRecords),
          rentalIncomeIndex = updatedIndex.getOrElse(0)
        )
      )
    )
    session.saveOrUpdate(updatedSession).map { _ =>
      Redirect(toSpecificController(source, updatedIndex))
    }
  }

  private def createIncomeRecord(sourceType: TypeOfIncome): IncomeRecord =
    sourceType match
      case TypeFranchise      =>
        FranchiseIncomeRecord(
          sourceType = TypeFranchise
        )
      case TypeConcession6015 =>
        ConcessionIncomeRecord(
          sourceType = TypeConcession6015
        )
      case TypeConcession     =>
        ConcessionIncomeRecord(
          sourceType = TypeConcession
        )
      case TypeLetting        =>
        LettingIncomeRecord(
          sourceType = TypeLetting
        )

  private def toSpecificController(typeOfLetting: TypeOfIncome, index: Option[Int])(implicit
    request: SessionRequest[AnyContent]
  ): Call = {
    val targetIndex = index.getOrElse(0)
    typeOfLetting match {
      case TypeFranchise | TypeConcession6015   =>
        controllers.aboutfranchisesorlettings.routes.FranchiseTypeDetailsController.show(targetIndex)
      case TypeConcession if forType == FOR6030 =>
        controllers.aboutfranchisesorlettings.routes.CateringOperationBusinessDetailsController.show(Some(targetIndex))
      case TypeConcession                       =>
        controllers.aboutfranchisesorlettings.routes.ConcessionTypeDetailsController.show(targetIndex)
      case TypeLetting                          => controllers.aboutfranchisesorlettings.routes.LettingTypeDetailsController.show(targetIndex)
    }
  }

  private def forType(implicit request: SessionRequest[AnyContent]): ForType = request.sessionData.forType

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      case _     => controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url
    }
}
