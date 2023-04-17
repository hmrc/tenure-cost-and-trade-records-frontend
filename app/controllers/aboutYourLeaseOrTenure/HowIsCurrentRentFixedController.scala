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
import form.aboutYourLeaseOrTenure.HowIsCurrentRentFixedForm.howIsCurrentRentFixedForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.HowIsCurrentRentFixed
import models.{ForTypes, Session}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.HowIsCurrentRentFixedId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.howIsCurrentRentFixed

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class HowIsCurrentRentFixedController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  howIsCurrentRentFixedView: howIsCurrentRentFixed,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
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
    continueOrSaveAsDraft[HowIsCurrentRentFixed](
      howIsCurrentRentFixedForm,
      formWithErrors => BadRequest(howIsCurrentRentFixedView(formWithErrors, getBackLink(request.sessionData))),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(howIsCurrentRentFixed = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(HowIsCurrentRentFixedId).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.forType match {
      case ForTypes.for6010 =>
        answers.aboutLeaseOrAgreementPartTwo.flatMap(
          _.rentPayableVaryOnQuantityOfBeersDetails.map(_.rentPayableVaryOnQuantityOfBeersDetails.name)
        ) match {
          case Some("yes") =>
            controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersDetailsController.show().url
          case Some("no")  =>
            controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController.show().url
          case _           =>
            logger.warn(s"Back link for 6010 rent payable vary beer page reached with unknown value")
            controllers.routes.TaskListController.show().url
        }
      case _                =>
        answers.aboutLeaseOrAgreementPartTwo.flatMap(
          _.rentPayableVaryAccordingToGrossOrNetDetails.map(_.rentPayableVaryAccordingToGrossOrNets.name)
        ) match {
          case Some("yes") =>
            controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetDetailsController.show().url
          case Some("no")  =>
            controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController.show().url
          case _           =>
            logger.warn(s"Back link for rent increase by RPI page reached with unknown open market value")
            controllers.routes.TaskListController.show().url
        }
    }
}
