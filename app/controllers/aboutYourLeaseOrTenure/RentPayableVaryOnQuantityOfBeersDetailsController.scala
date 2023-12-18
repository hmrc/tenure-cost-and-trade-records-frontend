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
import form.aboutYourLeaseOrTenure.RentPayableVaryOnQuantityOfBeersDetailsForm.rentPayableVaryOnQuantityOfBeersDetailsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.RentPayableVaryOnQuantityOfBeersInformationDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.rentVaryQuantityOfBeersDetailsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.rentPayableVaryOnQuantityOfBeersDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentPayableVaryOnQuantityOfBeersDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentPayableVaryOnQuantityOfBeersDetailsView: rentPayableVaryOnQuantityOfBeersDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentPayableVaryOnQuantityOfBeersDetailsView(
          request.sessionData.aboutLeaseOrAgreementPartTwo
            .flatMap(_.rentPayableVaryOnQuantityOfBeersInformationDetails) match {
            case Some(rentPayableVaryOnQuantityOfBeersInformationDetails) =>
              rentPayableVaryOnQuantityOfBeersDetailsForm.fill(
                rentPayableVaryOnQuantityOfBeersInformationDetails
              )
            case _                                                        => rentPayableVaryOnQuantityOfBeersDetailsForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[RentPayableVaryOnQuantityOfBeersInformationDetails](
      rentPayableVaryOnQuantityOfBeersDetailsForm,
      formWithErrors =>
        BadRequest(rentPayableVaryOnQuantityOfBeersDetailsView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData =
          updateAboutLeaseOrAgreementPartTwo(_.copy(rentPayableVaryOnQuantityOfBeersInformationDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(rentVaryQuantityOfBeersDetailsId, updatedData).apply(updatedData))
      }
    )
  }

}
