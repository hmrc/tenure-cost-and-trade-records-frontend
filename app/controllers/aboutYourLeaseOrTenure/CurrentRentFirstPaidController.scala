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

package controllers.aboutYourLeaseOrTenure

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.CurrentRentFirstPaidForm.currentRentFirstPaidForm
import models.{ForTypes, Session}
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.CurrentRentFirstPaid
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.CurrentRentFirstPaidPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.currentRentFirstPaid

import javax.inject.{Inject, Named, Singleton}

@Singleton
class CurrentRentFirstPaidController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  currentRentFirstPaidView: currentRentFirstPaid,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      currentRentFirstPaidView(
        request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.currentRentFirstPaid) match {
          case Some(currentRentFirstPaid) => currentRentFirstPaidForm.fillAndValidate(currentRentFirstPaid)
          case _                          => currentRentFirstPaidForm
        },
        getBackLink(request.sessionData),
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CurrentRentFirstPaid](
      currentRentFirstPaidForm,
      formWithErrors =>
        BadRequest(
          currentRentFirstPaidView(formWithErrors, getBackLink(request.sessionData), request.sessionData.toSummary)
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(currentRentFirstPaid = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(CurrentRentFirstPaidPageId, updatedData).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.forType match {
      case ForTypes.for6011 => controllers.aboutYourLeaseOrTenure.routes.RentIncludesVatController.show().url
      case _                => controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show().url
    }

}
