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
import connectors.addressLookup.AddressLookupConnector
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.FranchiseTypeDetailsForm.theForm
import models.Session
import models.submissions.aboutfranchisesorlettings.*
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.FranchiseTypeDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.franchiseTypeDetails as FranchiseTypeDetailsView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class FranchiseTypeDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  theView: FranchiseTypeDetailsView,
  withSessionRefiner: WithSessionRefiner,
  addressLookupConnector: AddressLookupConnector,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging:

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("FranchiseTypeDetails")
    val freshForm  = theForm
    val filledForm =
      for
        aboutFranchisesOrLettings <- request.sessionData.aboutFranchisesOrLettings
        rentalIncome              <- aboutFranchisesOrLettings.rentalIncome
        incomeRecord              <- rentalIncome.lift(index)
        businessDetails           <- incomeRecord match {
                                       case r: FranchiseIncomeRecord      => r.businessDetails
                                       case r: Concession6015IncomeRecord => r.businessDetails
                                       case _                             => None
                                     }
      yield theForm.fill(businessDetails)

    Ok(
      theView(
        filledForm.getOrElse(freshForm),
        index,
        backLink(index),
        request.sessionData.forType
      )
    )
  }

  def submit(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[BusinessDetails](
      theForm,
      formWithErrors =>
        successful(
          BadRequest(
            theView(
              formWithErrors,
              idx,
              backLink(idx),
              request.sessionData.forType
            )
          )
        ),
      formData => {
        given Session = request.sessionData
        for
          newSession <- successful(sessionWithBusinessDetails(formData, idx))
          _          <- repository.saveOrUpdate(newSession)
        // TODO redirect <- redirectToAddressLookup
        yield Redirect(navigator.nextPage(FranchiseTypeDetailsId, newSession).apply(newSession))
      }
    )
  }

  private def sessionWithBusinessDetails(formData: BusinessDetails, idx: Int)(using
    request: SessionRequest[AnyContent]
  ): Session =
    AboutFranchisesOrLettings.updateAboutFranchisesOrLettings { aboutFranchisesOrLettings =>
      if (aboutFranchisesOrLettings.rentalIncome.exists(_.isDefinedAt(idx))) {
        val updatedRentalIncome = aboutFranchisesOrLettings.rentalIncome.map { records =>
          records.updated(
            idx,
            records(idx) match {
              case franchise: FranchiseIncomeRecord       => franchise.copy(businessDetails = Some(formData))
              case concession: Concession6015IncomeRecord => concession.copy(businessDetails = Some(formData))
              case _                                      => throw new IllegalStateException("Unknown income record type")
            }
          )
        }
        aboutFranchisesOrLettings.copy(rentalIncome = updatedRentalIncome, rentalIncomeIndex = idx)
      } else {
        aboutFranchisesOrLettings
      }
    }(request)

  private def backLink(idx: Int)(using request: SessionRequest[AnyContent]): String =
    if request.getQueryString("from") == Some("CYA")
    then routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    else routes.TypeOfIncomeController.show(Some(idx)).url
