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
import form.Form6010.HowIsCurrentRentFixedForm.howIsCurrentRentFixedForm
import models.{ForTypes, Session}
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.HowIsCurrentRentFixedId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.howIsCurrentRentFixed

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class HowIsCurrentRentFixedController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  howIsCurrentRentFixedView: howIsCurrentRentFixed,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        howIsCurrentRentFixedView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.howIsCurrentRentFixed) match {
            case Some(data) => howIsCurrentRentFixedForm.fill(data)
            case _          => howIsCurrentRentFixedForm
          },
          getBackLink(request.sessionData)
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    howIsCurrentRentFixedForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(howIsCurrentRentFixedView(formWithErrors, getBackLink(request.sessionData)))),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(howIsCurrentRentFixed = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(HowIsCurrentRentFixedId).apply(updatedData)))
        }
      )
  }

  private def getBackLink(answers: Session): String =
    answers.userLoginDetails.forNumber match {
      case ForTypes.for6010 =>
        answers.aboutLeaseOrAgreementPartTwo.flatMap(
          _.rentPayableVaryOnQuantityOfBeersDetails.map(_.rentPayableVaryOnQuantityOfBeersDetails.name)
        ) match {
          case Some("yes") => controllers.Form6010.routes.RentPayableVaryOnQuantityOfBeersDetailsController.show().url
          case Some("no")  => controllers.Form6010.routes.RentPayableVaryOnQuantityOfBeersController.show().url
          case _           =>
            logger.warn(s"Back link for 6010 rent payable vary beer page reached with unknown value")
            controllers.routes.TaskListController.show().url
        }
      case _                =>
        answers.aboutLeaseOrAgreementPartTwo.flatMap(
          _.rentPayableVaryAccordingToGrossOrNetDetails.map(_.rentPayableVaryAccordingToGrossOrNets.name)
        ) match {
          case Some("yes") =>
            controllers.Form6010.routes.RentPayableVaryAccordingToGrossOrNetDetailsController.show().url
          case Some("no")  => controllers.Form6010.routes.RentPayableVaryAccordingToGrossOrNetController.show().url
          case _           =>
            logger.warn(s"Back link for rent increase by RPI page reached with unknown open market value")
            controllers.routes.TaskListController.show().url
        }
    }
}
