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

import controllers.LoginController.loginForm
import form.Form6010.RentIncludeFixtureAndFittingsForm.rentIncludeFixturesAndFittingsForm
import form.aboutYourLeaseOrTenure.RentIncludeTradeServicesDetailsForm.rentIncludeTradeServicesDetailsForm
import form.aboutYourLeaseOrTenure.RentIncludeTradeServicesForm.rentIncludeTradeServicesForm
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutYourLeaseOrTenure.{rentIncludeTradeServices, rentIncludeTradeServicesDetails}
import views.html.form.rentIncludeFixtureAndFittings
import views.html.login

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class RentIncludeTradeServicesController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  login: login,
  rentIncludeTradeServicesDetailsView: rentIncludeTradeServicesDetails,
  rentIncludeFixtureAndFittingsView: rentIncludeFixtureAndFittings,
  rentIncludeTradeServicesView: rentIncludeTradeServices
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(rentIncludeTradeServicesView(rentIncludeTradeServicesForm)))
  }

  def submit = Action.async { implicit request =>
    rentIncludeTradeServicesForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(rentIncludeTradeServicesView(formWithErrors))),
        data =>
          // TODO use this code when session added
          // Future.successful(Redirect(navigator.nextPage(RentIncludeTradeServicesPageId).apply(updatedData)))
          data.rentIncludeTradeServices match {
            case AnswerYes =>
              Future.successful(Ok(rentIncludeTradeServicesDetailsView(rentIncludeTradeServicesDetailsForm)))
            case AnswerNo  =>
              Future.successful(Ok(rentIncludeFixtureAndFittingsView(rentIncludeFixturesAndFittingsForm)))
            case _         => Future.successful(Ok(login(loginForm)))
          }
      )
  }
}
