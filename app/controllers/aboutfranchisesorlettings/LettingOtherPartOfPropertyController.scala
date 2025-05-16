/*
 * Copyright 2025 HM Revenue & Customs
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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.LettingOtherPartOfPropertiesForm.lettingOtherPartOfPropertiesForm
import models.ForType
import models.ForType.*
import models.Session
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.LettingAccommodationPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LettingOtherPartOfPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationOrLettingAccommodationView: cateringOperationOrLettingAccommodation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  private def forType(implicit request: SessionRequest[AnyContent]): ForType =
    request.sessionData.forType

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("LettingOtherPartOfProperty")

    Future.successful(
      Ok(
        cateringOperationOrLettingAccommodationView(
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettingOtherPartOfProperty) match {
            case Some(lettingOtherPartOfProperty) =>
              lettingOtherPartOfPropertiesForm.fill(lettingOtherPartOfProperty)
            case _                                => lettingOtherPartOfPropertiesForm
          },
          "lettingOtherPartOfProperty",
          getBackLink(request.sessionData, navigator.from),
          request.sessionData.toSummary,
          forType
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      lettingOtherPartOfPropertiesForm,
      formWithErrors =>
        BadRequest(
          cateringOperationOrLettingAccommodationView(
            formWithErrors,
            "lettingOtherPartOfProperty",
            getBackLink(request.sessionData, navigator.from),
            request.sessionData.toSummary,
            forType
          )
        ),
      data => {
        val updatedData = updateAboutFranchisesOrLettings(_.copy(lettingOtherPartOfProperty = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            navigator.cyaPage
              .filter(_ =>
                navigator.from == "CYA" && (data == AnswerNo ||
                  request.sessionData.aboutFranchisesOrLettings
                    .flatMap(_.lettingOtherPartOfProperty)
                    .contains(AnswerYes))
              )
              .getOrElse(navigator.nextPage(LettingAccommodationPageId, updatedData).apply(updatedData))
          }
          .map(Redirect)
      }
    )
  }

  private def getBackLink(answers: Session, fromLocation: String): String =
    fromLocation match {
      case "TL" =>
        controllers.routes.TaskListController.show().url + "#letting-other-part-of-property"
      case _    =>
        getBackLinkOfrSections(answers)
    }

  private def getBackLinkOfrSections(answers: Session): String =
    answers.aboutFranchisesOrLettings.flatMap { aboutFranchiseOrLettings =>
      aboutFranchiseOrLettings.cateringOperationSections.lastOption.map(_ =>
        aboutFranchiseOrLettings.cateringOperationSections.size - 1
      )
    } match {
      case Some(index) =>
        controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(index).url
      case None        =>
        answers.forType match {
          case FOR6015 | FOR6016 =>
            controllers.aboutfranchisesorlettings.routes.ConcessionOrFranchiseController.show().url
          case _                 =>
            controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
        }
    }

}
