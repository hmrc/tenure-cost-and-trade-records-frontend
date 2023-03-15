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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.rentIncludeFixtureAndFittings
import form.Form6010.RentIncludeFixtureAndFittingsForm.rentIncludeFixturesAndFittingsForm
import models.Session
import models.submissions.aboutLeaseOrAgreement.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.RentFixtureAndFittingsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import repositories.SessionRepo

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentIncludeFixtureAndFittingsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentIncludeFixtureAndFittingsView: rentIncludeFixtureAndFittings,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentIncludeFixtureAndFittingsView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.rentIncludeFixturesAndFittingsDetails) match {
            case Some(rentIncludeFixturesAndFittingsDetails) =>
              rentIncludeFixturesAndFittingsForm.fillAndValidate(rentIncludeFixturesAndFittingsDetails)
            case _                                           => rentIncludeFixturesAndFittingsForm
          },
          getBackLink(request.sessionData)
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    rentIncludeFixturesAndFittingsForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(rentIncludeFixtureAndFittingsView(formWithErrors, getBackLink(request.sessionData)))
          ),
        data => {
          val updatedData =
            updateAboutLeaseOrAgreementPartOne(_.copy(rentIncludeFixturesAndFittingsDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(RentFixtureAndFittingsPageId).apply(updatedData)))
        }
      )
  }

  private def getBackLink(answers: Session): String =
    answers.aboutLeaseOrAgreementPartOne.flatMap(
      _.rentIncludeTradeServicesDetails.map(_.rentIncludeTradeServices.name)
    ) match {
      case Some("yes") => controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesDetailsController.show().url
      case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
      case _           =>
        logger.warn(s"Back link for fixture and fittings page reached with unknown trade services value")
        controllers.routes.TaskListController.show().url
    }
}
