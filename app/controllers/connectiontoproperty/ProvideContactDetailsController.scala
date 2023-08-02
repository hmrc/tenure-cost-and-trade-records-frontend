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

package controllers.connectiontoproperty

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.connectiontoproperty.ProvideContactDetailsForm.provideContactDetailsForm
import models.Session
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.ProvideContactDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.{AboutYouPageId, ProvideYourContactDetailsPageId}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.provideContactDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class ProvideContactDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  provideContactDetailsView: provideContactDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        provideContactDetailsView(
          request.sessionData.stillConnectedDetails.flatMap(_.provideContactDetails) match {
            case Some(customerDetails) => provideContactDetailsForm.fillAndValidate(customerDetails)
            case _                     => provideContactDetailsForm
          },
          getBackLink(request.sessionData) match {
            case Right(link) => link
            case Left(msg)   =>
              logger.warn(s"Navigation for provide your contact details page reached with error: $msg")
              throw new RuntimeException(s"Navigation for provide your contact details page reached with error $msg")
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ProvideContactDetails](
      provideContactDetailsForm,
      formWithErrors =>
        BadRequest(
          provideContactDetailsView(
            formWithErrors,
            getBackLink(request.sessionData) match {
              case Right(link) => link
              case Left(msg)   =>
                logger.warn(s"Navigation for provide your contact details page reached with error: $msg")
                throw new RuntimeException(s"Navigation for provide your contact details page reached with error $msg")
            },
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(provideContactDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(ProvideYourContactDetailsPageId, updatedData).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session): Either[String, String] =
    answers.stillConnectedDetails.flatMap(_.isAnyRentReceived.map(_.name)) match {
      case Some("yes") =>
        Right(controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0).url)
      case Some("no")  => Right(controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url)
      case _           => Left(s"Unknown connection to property back link")
    }

}
