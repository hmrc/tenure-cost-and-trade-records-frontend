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
import views.html.form.rentIncreaseAnnuallyWithRPI
import form.Form6010.RentIncreasedAnnuallyWithRPIForm.rentIncreasedAnnuallyWithRPIDetailsForm
import models.Session
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.RentIncreaseByRPIPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import repositories.SessionRepo

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentIncreaseAnnuallyWithRPIController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentIncreaseAnnuallyWithRPIView: rentIncreaseAnnuallyWithRPI,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentIncreaseAnnuallyWithRPIView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.rentIncreasedAnnuallyWithRPIDetails) match {
            case Some(rentIncreasedAnnuallyWithRPIDetails) =>
              rentIncreasedAnnuallyWithRPIDetailsForm.fillAndValidate(rentIncreasedAnnuallyWithRPIDetails)
            case None                                      => rentIncreasedAnnuallyWithRPIDetailsForm
          },
          getBackLink(request.sessionData)
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    rentIncreasedAnnuallyWithRPIDetailsForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future
            .successful(BadRequest(rentIncreaseAnnuallyWithRPIView(formWithErrors, getBackLink(request.sessionData)))),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(rentIncreasedAnnuallyWithRPIDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(RentIncreaseByRPIPageId).apply(updatedData)))
        }
      )
  }

  private def getBackLink(answers: Session): String =
    answers.aboutLeaseOrAgreementPartOne.flatMap(_.rentOpenMarketValueDetails.map(_.rentOpenMarketValues.name)) match {
      case Some("yes") => controllers.Form6010.routes.RentOpenMarketValueController.show().url
      case Some("no")  => controllers.Form6010.routes.WhatIsYourRentBasedOnController.show().url
      case _           =>
        logger.warn(s"Back link for rent increase by RPI page reached with unknown open market value")
        controllers.routes.TaskListController.show().url
    }
}
