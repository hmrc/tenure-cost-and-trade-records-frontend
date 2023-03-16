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

package controllers.Form6010

import form.Form6010.RentPayableVaryAccordingToGrossOrNetDetailsForm.rentPayableVaryAccordingToGrossOrNetInformationForm
import actions.WithSessionRefiner
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.RentPayableVaryAccordingToGrossOrNetDetailsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.rentPayableVaryAccordingToGrossOrNetDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentPayableVaryAccordingToGrossOrNetDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentPayableVaryAccordingToGrossOrNetDetailsView: rentPayableVaryAccordingToGrossOrNetDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentPayableVaryAccordingToGrossOrNetDetailsView(
          request.sessionData.aboutLeaseOrAgreementPartTwo
            .flatMap(_.rentPayableVaryAccordingToGrossOrNetInformationDetails) match {
            case Some(rentPayableVaryAccordingToGrossOrNetInformationDetails) =>
              rentPayableVaryAccordingToGrossOrNetInformationForm.fillAndValidate(
                rentPayableVaryAccordingToGrossOrNetInformationDetails
              )
            case _                                                            => rentPayableVaryAccordingToGrossOrNetInformationForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    rentPayableVaryAccordingToGrossOrNetInformationForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(rentPayableVaryAccordingToGrossOrNetDetailsView(formWithErrors))),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartTwo(
            _.copy(rentPayableVaryAccordingToGrossOrNetInformationDetails = Some(data))
          )
          session.saveOrUpdate(updatedData)
          Future
            .successful(Redirect(navigator.nextPage(RentPayableVaryAccordingToGrossOrNetDetailsId).apply(updatedData)))
        }
      )
  }

}
