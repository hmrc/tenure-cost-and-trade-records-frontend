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

package controllers.aboutYourLeaseOrTenure

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.CurrentRentPayableWithin12MonthsForm.currentRentPayableWithin12MonthsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.CurrentRentPayableWithin12Months
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.CurrentRentPayableWithin12monthsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.currentRentPayableWithin12Months

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CurrentRentPayableWithin12MonthsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  currentRentPayableWithin12MonthsView: currentRentPayableWithin12Months,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("CurrentRentPayableWithin12Months")

    Ok(
      currentRentPayableWithin12MonthsView(
        request.sessionData.aboutLeaseOrAgreementPartOne
          .flatMap(_.currentRentPayableWithin12Months)
          .fold(currentRentPayableWithin12MonthsForm)(currentRentPayableWithin12MonthsForm.fill),
        request.sessionData.toSummary
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CurrentRentPayableWithin12Months](
      currentRentPayableWithin12MonthsForm,
      formWithErrors => BadRequest(currentRentPayableWithin12MonthsView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(currentRentPayableWithin12Months = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ =>
            Redirect(navigator.nextPage(CurrentRentPayableWithin12monthsPageId, updatedData).apply(updatedData))
          )

      }
    )
  }

}
