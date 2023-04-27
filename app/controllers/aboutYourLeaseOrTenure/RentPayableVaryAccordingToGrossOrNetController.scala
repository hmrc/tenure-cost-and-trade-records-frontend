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
import form.aboutYourLeaseOrTenure.RentPayableVaryAccordingToGrossOrNetForm.rentPayableVaryAccordingToGrossOrNetForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.RentPayableVaryAccordingToGrossOrNetDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.RentPayableVaryAccordingToGrossOrNetId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.rentPayableVaryAccordingToGrossOrNet

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentPayableVaryAccordingToGrossOrNetController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentPayableVaryAccordingToGrossOrNetView: rentPayableVaryAccordingToGrossOrNet,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentPayableVaryAccordingToGrossOrNetView(
          request.sessionData.aboutLeaseOrAgreementPartTwo
            .flatMap(_.rentPayableVaryAccordingToGrossOrNetDetails) match {
            case Some(rentPayableVaryAccordingToGrossOrNetDetails) =>
              rentPayableVaryAccordingToGrossOrNetForm.fillAndValidate(rentPayableVaryAccordingToGrossOrNetDetails)
            case _                                                 => rentPayableVaryAccordingToGrossOrNetForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[RentPayableVaryAccordingToGrossOrNetDetails](
      rentPayableVaryAccordingToGrossOrNetForm,
      formWithErrors =>
        BadRequest(rentPayableVaryAccordingToGrossOrNetView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData =
          updateAboutLeaseOrAgreementPartTwo(_.copy(rentPayableVaryAccordingToGrossOrNetDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(RentPayableVaryAccordingToGrossOrNetId).apply(updatedData))
      }
    )
  }

}
