/*
 * Copyright 2023 HM Revenue & Customs
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
import form.aboutfranchisesorlettings.RentReceivedFromForm.rentReceivedFromForm as theForm
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, Concession6015IncomeRecord, IncomeRecord, RentReceivedFrom}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.RentReceivedFromPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.rentReceivedFrom

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RentReceivedFromController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  rentReceivedFromView: rentReceivedFrom,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with FranchiseAndLettingSupport
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails = getIncomeRecord(index).collect {
      case concession: Concession6015IncomeRecord => concession.rent
      case _                                      => None
    }.flatten

    audit.sendChangeLink("RentReceivedFrom")

    Ok(
      rentReceivedFromView(
        existingDetails.fold(theForm)(theForm.fill),
        index,
        getOperatorName(index),
        request.sessionData.toSummary
      )
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[RentReceivedFrom](
      theForm,
      formWithErrors =>
        BadRequest(
          rentReceivedFromView(
            formWithErrors,
            index,
            getOperatorName(index),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedSession = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings { aboutFranchisesOrLettings =>
          if (aboutFranchisesOrLettings.rentalIncome.exists(_.isDefinedAt(index))) {
            val updatedRentalIncome = aboutFranchisesOrLettings.rentalIncome.map { records =>
              records.updated(
                index,
                records(index) match {
                  case concession: Concession6015IncomeRecord => concession.copy(rent = Some(data))
                  case _                                      => throw new IllegalStateException("Unknown income record type")
                }
              )
            }
            aboutFranchisesOrLettings.copy(rentalIncome = updatedRentalIncome)
          } else aboutFranchisesOrLettings
        }(request)

        session.saveOrUpdate(updatedSession).map { _ =>
          Redirect(navigator.nextPage(RentReceivedFromPageId, updatedSession).apply(updatedSession))

        }
      }
    )
  }
}
