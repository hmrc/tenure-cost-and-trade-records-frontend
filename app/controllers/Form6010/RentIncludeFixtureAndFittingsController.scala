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
import views.html.form.{rentIncludeFixtureAndFittings, rentIncludeFixtureAndFittingsDetails, rentOpenMarketValue}
import form.Form6010.RentIncludeFixtureAndFittingsForm.rentIncludeFixturesAndFittingsForm
import form.Form6010.RentIncludeFixtureAndFittingDetailsForm.rentIncludeFixtureAndFittingsDetailsForm
import form.Form6010.RentOpenMarketValueForm.rentOpenMarketValuesForm
import models.submissions.Form6010.{RentIncludeFixturesAndFittingsNo, RentIncludeFixturesAndFittingsYes}
import models.submissions.aboutLeaseOrAgreement.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import play.api.i18n.I18nSupport
import repositories.SessionRepo
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentIncludeFixtureAndFittingsController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  rentIncludeFixtureAndFittingsView: rentIncludeFixtureAndFittings,
  rentIncludeFixtureAndFittingsDetailsView: rentIncludeFixtureAndFittingsDetails,
  rentOpenMarketValueView: rentOpenMarketValue,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentIncludeFixtureAndFittingsView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.rentIncludeFixturesAndFittingsDetails) match {
            case Some(rentIncludeFixturesAndFittingsDetails) => rentIncludeFixturesAndFittingsForm.fillAndValidate(rentIncludeFixturesAndFittingsDetails)
            case _ => rentIncludeFixturesAndFittingsForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    rentIncludeFixturesAndFittingsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(rentIncludeFixtureAndFittingsView(formWithErrors))),
        data =>
          data.rentIncludeFixturesAndFittingsDetails match {
            case RentIncludeFixturesAndFittingsYes =>
              val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(rentIncludeFixturesAndFittingsDetails = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(Ok(rentIncludeFixtureAndFittingsDetailsView(rentIncludeFixtureAndFittingsDetailsForm)))
            case RentIncludeFixturesAndFittingsNo  =>
              val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(rentIncludeFixturesAndFittingsDetails = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(Ok(rentOpenMarketValueView(rentOpenMarketValuesForm)))
            case _                                 => Future.successful(Ok(login(loginForm)))
          }
      )
  }
}
