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
import views.html.form.{rentIncreaseAnnuallyWithRPI, rentOpenMarketValue, whatIsYourRentBasedOn}
import form.Form6010.RentOpenMarketValueForm.rentOpenMarketValuesForm
import form.Form6010.WhatIsYourCurrentRentBasedOnForm.whatIsYourCurrentRentBasedOnForm
import form.Form6010.RentIncreasedAnnuallyWithRPIForm.rentIncreasedAnnuallyWithRPIDetailsForm
import models.submissions.Form6010.{RentOpenMarketValuesNo, RentOpenMarketValuesYes}
import models.submissions.aboutLeaseOrAgreement.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import play.api.i18n.I18nSupport
import repositories.SessionRepo
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentOpenMarketValueController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  rentOpenMarketValueView: rentOpenMarketValue,
  whatIsYourRentBasedOnView: whatIsYourRentBasedOn,
  rentIncreaseAnnuallyWithRPIView: rentIncreaseAnnuallyWithRPI,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentOpenMarketValueView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.rentOpenMarketValueDetails) match {
            case Some(rentOpenMarketValueDetails) =>
              rentOpenMarketValuesForm.fillAndValidate(rentOpenMarketValueDetails)
            case _                                => rentOpenMarketValuesForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    rentOpenMarketValuesForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(rentOpenMarketValueView(formWithErrors))),
        data =>
          data.rentOpenMarketValues match {
            case RentOpenMarketValuesYes =>
              val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(rentOpenMarketValueDetails = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(Ok(rentIncreaseAnnuallyWithRPIView(rentIncreasedAnnuallyWithRPIDetailsForm)))
            case RentOpenMarketValuesNo  =>
              val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(rentOpenMarketValueDetails = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(Ok(whatIsYourRentBasedOnView(whatIsYourCurrentRentBasedOnForm)))
            case _                       => Future.successful(Ok(login(loginForm)))
          }
      )
  }
}
