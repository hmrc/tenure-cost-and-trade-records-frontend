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
import form.aboutfranchisesorlettings.ConcessionTypeDetailsForm.concessionTypeDetailsForm
import models.submissions.aboutfranchisesorlettings.*
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.ConcessionTypeDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.concessionTypeDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ConcessionTypeDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  view: concessionTypeDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[ConcessionBusinessDetails] = for {
      requestedIndex   <- Some(index)
      allRecords       <- request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome)
      existingRecord   <- allRecords.lift(requestedIndex)
      concessionRecord <- existingRecord match {
                            case concession: ConcessionIncomeRecord => concession.businessDetails
                            case _                                  => None
                          }
    } yield concessionRecord

    audit.sendChangeLink("ConcessionTypeDetails")
    Ok(
      view(
        existingDetails.fold(concessionTypeDetailsForm)(
          concessionTypeDetailsForm.fill
        ),
        index,
        calculateBackLink(index),
        request.sessionData.toSummary
      )
    )
  }

  def submit(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ConcessionBusinessDetails](
      concessionTypeDetailsForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            idx,
            calculateBackLink(idx),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedSession = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings { aboutFranchisesOrLettings =>
          if (aboutFranchisesOrLettings.rentalIncome.exists(_.isDefinedAt(idx))) {
            val updatedRentalIncome = aboutFranchisesOrLettings.rentalIncome.map { records =>
              records.updated(idx, records(idx).asInstanceOf[ConcessionIncomeRecord].copy(businessDetails = Some(data)))
            }
            aboutFranchisesOrLettings.copy(rentalIncome = updatedRentalIncome, rentalIncomeIndex = idx)
          } else {
            aboutFranchisesOrLettings
          }
        }(using request)

        session.saveOrUpdate(updatedSession).map { _ =>
          Redirect(navigator.nextPage(ConcessionTypeDetailsId, updatedSession).apply(updatedSession))
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
