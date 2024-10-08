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
import form.aboutfranchisesorlettings.LettingOtherPartOfPropertyForm.lettingOtherPartOfPropertyForm
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, LettingIncomeRecord, LettingOtherPartOfPropertyInformationDetails}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.LettingTypeDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.lettingTypeDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LettingTypeDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  view: lettingTypeDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[LettingOtherPartOfPropertyInformationDetails] = for {
      requestedIndex <- Some(index)
      allRecords     <- request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome)
      existingRecord <- allRecords.lift(requestedIndex)
      lettingRecord  <- existingRecord match {
                          case letting: LettingIncomeRecord => letting.operatorDetails
                          case _                            => None
                        }
    } yield lettingRecord

    Ok(
      view(
        existingDetails.fold(lettingOtherPartOfPropertyForm)(lettingOtherPartOfPropertyForm.fill),
        index,
        calculateBackLink(index),
        request.sessionData.toSummary,
        request.sessionData.forType
      )
    )
  }

  def submit(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LettingOtherPartOfPropertyInformationDetails](
      lettingOtherPartOfPropertyForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            idx,
            calculateBackLink(idx),
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      data => {
        val updatedSession = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings { aboutFranchisesOrLettings =>
          if (aboutFranchisesOrLettings.rentalIncome.exists(_.isDefinedAt(idx))) {
            val updatedRentalIncome = aboutFranchisesOrLettings.rentalIncome.map { records =>
              records.updated(idx, records(idx).asInstanceOf[LettingIncomeRecord].copy(operatorDetails = Some(data)))
            }
            aboutFranchisesOrLettings.copy(rentalIncome = updatedRentalIncome)
          } else {
            aboutFranchisesOrLettings
          }
        }(request)

        session.saveOrUpdate(updatedSession).map { _ =>
          Redirect(navigator.nextPage(LettingTypeDetailsId, updatedSession).apply(updatedSession))

        }
      }
    )
  }

  private def calculateBackLink(idx: Int)(implicit request: SessionRequest[AnyContent]) =
    request.getQueryString("from") match {
      case Some("CYA") =>
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      case _           => controllers.aboutfranchisesorlettings.routes.TypeOfIncomeController.show(Some(idx)).url
    }
}
