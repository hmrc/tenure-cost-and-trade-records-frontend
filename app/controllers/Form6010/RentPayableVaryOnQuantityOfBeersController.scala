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
import views.html.form.{rentPayableVaryOnQuantityOfBeers, rentPayableVaryOnQuantityOfBeersDetails, ultimatelyResponsible}
import form.Form6010.RentPayableVaryOnQuantityOfBeersForm.rentPayableVaryOnQuantityOfBeersForm
import form.Form6010.RentPayableVaryOnQuantityOfBeersDetailsForm.rentPayableVaryOnQuantityOfBeersDetailsForm
import form.Form6010.UltimatelyResponsibleForm.ultimatelyResponsibleForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.common.{AnswerNo, AnswerYes}
import play.api.i18n.I18nSupport
import repositories.SessionRepo
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentPayableVaryOnQuantityOfBeersController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  ultimatelyResponsibleView: ultimatelyResponsible,
  rentPayableVaryOnQuantityOfBeersDetailsView: rentPayableVaryOnQuantityOfBeersDetails,
  rentPayableVaryOnQuantityOfBeersView: rentPayableVaryOnQuantityOfBeers,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentPayableVaryOnQuantityOfBeersView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.rentPayableVaryOnQuantityOfBeersDetails) match {
            case Some(rentPayableVaryOnQuantityOfBeersDetails) =>
              rentPayableVaryOnQuantityOfBeersForm.fillAndValidate(rentPayableVaryOnQuantityOfBeersDetails)
            case _                                             => rentPayableVaryOnQuantityOfBeersForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    rentPayableVaryOnQuantityOfBeersForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(rentPayableVaryOnQuantityOfBeersView(formWithErrors))),
        data =>
          data.rentPayableVaryOnQuantityOfBeersDetails match {
            case AnswerYes =>
              val updatedData =
                updateAboutLeaseOrAgreementPartTwo(_.copy(rentPayableVaryOnQuantityOfBeersDetails = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(
                Ok(rentPayableVaryOnQuantityOfBeersDetailsView(rentPayableVaryOnQuantityOfBeersDetailsForm))
              )
            case AnswerNo  =>
              val updatedData =
                updateAboutLeaseOrAgreementPartTwo(_.copy(rentPayableVaryOnQuantityOfBeersDetails = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(Ok(ultimatelyResponsibleView(ultimatelyResponsibleForm)))
            case _         => Future.successful(Ok(login(loginForm)))
          }
      )
  }
}
