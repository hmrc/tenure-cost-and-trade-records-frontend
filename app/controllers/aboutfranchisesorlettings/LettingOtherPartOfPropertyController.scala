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

package controllers.aboutfranchisesorlettings

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.LettingOtherPartOfPropertiesForm.lettingOtherPartOfPropertiesForm
import models.{ForTypes, Session}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.common.AnswersYesNo
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.LettingAccommodationPageId
import play.api.i18n.I18nSupport
import play.api.i18n.Lang.logger
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class LettingOtherPartOfPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationOrLettingAccommodationView: cateringOperationOrLettingAccommodation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        cateringOperationOrLettingAccommodationView(
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettingOtherPartOfProperty) match {
            case Some(lettingOtherPartOfProperty) =>
              lettingOtherPartOfPropertiesForm.fillAndValidate(lettingOtherPartOfProperty)
            case _                                => lettingOtherPartOfPropertiesForm
          },
          "lettingOtherPartOfProperties",
          getBackLink(request.sessionData) match {
            case Right(link) => link
            case Left(msg)   =>
              logger.warn(s"Navigation for catering operation details page reached with error: $msg")
              throw new RuntimeException(
                s"Navigation for catering operation details page reached with error $msg"
              )
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      lettingOtherPartOfPropertiesForm,
      formWithErrors =>
        BadRequest(
          cateringOperationOrLettingAccommodationView(
            formWithErrors,
            "lettingOtherPartOfProperties",
            getBackLink(request.sessionData) match {
              case Right(link) => link
              case Left(msg)   =>
                logger.warn(s"Navigation for letting other part of property page reached with error: $msg")
                throw new RuntimeException(
                  s"Navigation for letting other part of property page reached with error $msg"
                )
            },
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutFranchisesOrLettings(_.copy(lettingOtherPartOfProperty = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(LettingAccommodationPageId).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session): Either[String, String] =
    answers.forType match {
      case ForTypes.for6015 | ForTypes.for6016 =>
        Right(controllers.aboutfranchisesorlettings.routes.ConcessionOrFranchiseController.show().url)
      case _                                   =>
        Right(controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url)
    }
}
