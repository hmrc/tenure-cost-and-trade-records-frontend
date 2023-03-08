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

package controllers.additionalinformation

import actions.WithSessionRefiner
import form.additionalinformation.FurtherInformationOrRemarksForm.furtherInformationOrRemarksForm
import models.{ForTypes, Session}
import models.submissions.additionalinformation.AdditionalInformation.updateAdditionalInformation
import navigation.AdditionalInformationNavigator
import navigation.identifiers.AdditionalInformationId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.additionalinformation.furtherInformationOrRemarks

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class FurtherInformationOrRemarksController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AdditionalInformationNavigator,
  furtherInformationOrRemarksView: furtherInformationOrRemarks,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        furtherInformationOrRemarksView(
          request.sessionData.additionalInformation.flatMap(_.furtherInformationOrRemarksDetails) match {
            case Some(furtherInformationOrRemarksDetails) =>
              furtherInformationOrRemarksForm.fillAndValidate(furtherInformationOrRemarksDetails)
            case _                                        => furtherInformationOrRemarksForm
          },
          getBackLink(request.sessionData) match {
            case Right(link) => link
            case Left(msg)   =>
              logger.warn(s"Navigation for further information page reached with error: $msg")
              throw new RuntimeException(s"Navigation for further information page reached with error $msg")
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    furtherInformationOrRemarksForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              furtherInformationOrRemarksView(
                formWithErrors,
                getBackLink(request.sessionData) match {
                  case Right(link) => link
                  case Left(msg)   =>
                    logger.warn(s"Navigation for further information page reached with error: $msg")
                    throw new RuntimeException(s"Navigation for further information page reached with error $msg")
                }
              )
            )
          ),
        data => {
          val updatedData = updateAdditionalInformation(_.copy(furtherInformationOrRemarksDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(AdditionalInformationId).apply(updatedData)))
        }
      )
  }

  private def getBackLink(answers: Session): Either[String, String] =
    answers.userLoginDetails.forNumber match {
      case ForTypes.for6010                    => Right(controllers.Form6010.routes.TenantsAdditionsDisregardedController.show().url)
      case ForTypes.for6011                    =>
        Right(controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController.show().url)
      case ForTypes.for6015 | ForTypes.for6016 =>
        Right(controllers.Form6010.routes.LegalOrPlanningRestrictionsController.show().url)
      case _                                   => Left(s"Unknown form type with further information back link")
    }
}
