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
import form.aboutYourLeaseOrTenure.CurrentAnnualRentForm.currentAnnualRentForm
import models.submissions.aboutLeaseOrAgreement.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.{ForTypes, Session}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.CurrentAnnualRentPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutYourLeaseOrTenure.currentAnnualRent

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CurrentAnnualRentController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  currentAnnualRentView: currentAnnualRent,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      currentAnnualRentView(
        request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.annualRent) match {
          case Some(annualRent) => currentAnnualRentForm.fillAndValidate(annualRent)
          case _                => currentAnnualRentForm
        },
        getBackLink(request.sessionData) match {
          case Right(link) => link
          case Left(msg)   =>
            logger.warn(s"Navigation for current annual rent page reached with error: $msg")
            throw new RuntimeException(s"Navigation for current annual rent page reached with error $msg")
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    currentAnnualRentForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              currentAnnualRentView(
                formWithErrors,
                getBackLink(request.sessionData) match {
                  case Right(link) => link
                  case Left(msg)   =>
                    logger.warn(s"Navigation for current annual rent page reached with error: $msg")
                    throw new RuntimeException(s"Navigation for current annual rent page reached with error $msg")
                }
              )
            )
          ),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(annualRent = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(CurrentAnnualRentPageId).apply(updatedData)))
        }
      )
  }

  private def getBackLink(answers: Session): Either[String, String] =
    answers.userLoginDetails.forNumber match {
      case ForTypes.for6010 =>
        Right(controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show().url)
      case ForTypes.for6011 => Right(controllers.aboutYourLeaseOrTenure.routes.AboutYourLandlordController.show().url)
      case _                => Left(s"Unknown form type with current annual rent back link")
    }
}
