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

import actions.WithSessionRefiner
import controllers.LoginController.loginForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{rentPayableVaryAccordingToGrossOrNet, rentPayableVaryAccordingToGrossOrNetDetails, rentPayableVaryOnQuantityOfBeers}
import form.Form6010.RentPayableVaryAccordingToGrossOrNetForm.rentPayableVaryAccordingToGrossOrNetForm
import form.Form6010.RentPayableVaryAccordingToGrossOrNetDetailsForm.rentPayableVaryAccordingToGrossOrNetInformationForm
import form.Form6010.RentPayableVaryOnQuantityOfBeersForm.rentPayableVaryOnQuantityOfBeersForm
import models.submissions.common.{AnswerNo, AnswerYes}
import views.html.login
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import play.api.i18n.I18nSupport
import repositories.SessionRepo

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentPayableVaryAccordingToGrossOrNetController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  rentPayableVaryAccordingToGrossOrNetView: rentPayableVaryAccordingToGrossOrNet,
  rentPayableVaryAccordingToGrossOrNetDetailsView: rentPayableVaryAccordingToGrossOrNetDetails,
  rentPayableVaryOnQuantityOfBeersView: rentPayableVaryOnQuantityOfBeers,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
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
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    rentPayableVaryAccordingToGrossOrNetForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(rentPayableVaryAccordingToGrossOrNetView(formWithErrors))),
        data =>
          data.rentPayableVaryAccordingToGrossOrNets match {
            case AnswerYes =>
              val updatedData =
                updateAboutLeaseOrAgreementPartTwo(_.copy(rentPayableVaryAccordingToGrossOrNetDetails = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(
                Ok(rentPayableVaryAccordingToGrossOrNetDetailsView(rentPayableVaryAccordingToGrossOrNetInformationForm))
              )
            case AnswerNo  =>
              val updatedData =
                updateAboutLeaseOrAgreementPartTwo(_.copy(rentPayableVaryAccordingToGrossOrNetDetails = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(Ok(rentPayableVaryOnQuantityOfBeersView(rentPayableVaryOnQuantityOfBeersForm)))

            case _ => Future.successful(Ok(login(loginForm)))
          }
      )
  }
}
