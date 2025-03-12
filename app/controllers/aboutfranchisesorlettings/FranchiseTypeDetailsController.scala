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
import form.aboutfranchisesorlettings.FranchiseTypeDetailsForm.franchiseTypeDetailsForm
import models.submissions.aboutfranchisesorlettings.*
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.FranchiseTypeDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.franchiseTypeDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class FranchiseTypeDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  view: franchiseTypeDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[BusinessDetails] = for {
      requestedIndex  <- Some(index)
      allRecords      <- request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome)
      existingRecord  <- allRecords.lift(requestedIndex)
      franchiseRecord <- existingRecord match {
                           case record: FranchiseIncomeRecord => record.businessDetails
                           case _                             => None
                         }
    } yield franchiseRecord

    audit.sendChangeLink("FranchiseTypeDetails")
    Ok(
      view(
        existingDetails.fold(franchiseTypeDetailsForm)(
          franchiseTypeDetailsForm.fill
        ),
        index,
        calculateBackLink(index),
        request.sessionData.toSummary,
        request.sessionData.forType
      )
    )
  }

  def submit(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[BusinessDetails](
      franchiseTypeDetailsForm,
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
              records.updated(
                idx,
                records(idx) match {
                  case franchise: FranchiseIncomeRecord       => franchise.copy(businessDetails = Some(data))
                  case concession: Concession6015IncomeRecord => concession.copy(businessDetails = Some(data))
                  case _                                      => throw new IllegalStateException("Unknown income record type")
                }
              )
            }
            aboutFranchisesOrLettings.copy(rentalIncome = updatedRentalIncome, rentalIncomeIndex = idx)
          } else {
            aboutFranchisesOrLettings
          }
        }(request)

        session.saveOrUpdate(updatedSession).map { _ =>
          Redirect(navigator.nextPage(FranchiseTypeDetailsId, updatedSession).apply(updatedSession))
        }
      }
    )
  }

  private def calculateBackLink(idx: Int)(implicit request: SessionRequest[AnyContent]): String =
    if (request.getQueryString("from") == Some("CYA")) {
      controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    } else {
      controllers.aboutfranchisesorlettings.routes.TypeOfIncomeController.show(Some(idx)).url
    }
}
