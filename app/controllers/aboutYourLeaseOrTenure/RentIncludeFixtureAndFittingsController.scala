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
import form.aboutYourLeaseOrTenure.RentIncludeFixtureAndFittingsForm.rentIncludeFixturesAndFittingsForm
import models.Session
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.RentIncludeFixturesAndFittingsDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.RentFixtureAndFittingsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.rentIncludeFixtureAndFittings

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentIncludeFixtureAndFittingsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentIncludeFixtureAndFittingsView: rentIncludeFixtureAndFittings,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
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
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[RentIncludeFixturesAndFittingsDetails](
      rentIncludeFixturesAndFittingsForm,
      formWithErrors =>
        BadRequest(
          rentIncludeFixtureAndFittingsView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData =
          updateAboutLeaseOrAgreementPartOne(_.copy(rentIncludeFixturesAndFittingsDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(RentFixtureAndFittingsPageId, updatedData).apply(updatedData))
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
