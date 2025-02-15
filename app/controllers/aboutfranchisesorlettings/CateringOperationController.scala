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
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.CateringOperationForm.cateringOperationForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.CateringOperationPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CateringOperationController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationOrLettingAccommodationView: cateringOperationOrLettingAccommodation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val backLink = navigator.from match {
      case "TL" => controllers.routes.TaskListController.show().url + "#catering-operation-or-letting-accommodation"
      case _    => controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url
    }

    audit.sendChangeLink("CateringOperation")
    Ok(
      cateringOperationOrLettingAccommodationView(
        request.sessionData.aboutFranchisesOrLettings.flatMap(_.cateringConcessionOrFranchise) match {
          case Some(cateringOperationOrLettingAccommodation) =>
            cateringOperationForm.fill(cateringOperationOrLettingAccommodation)
          case _                                             => cateringOperationForm
        },
        "cateringOperationOrLettingAccommodation",
        backLink,
        request.sessionData.toSummary,
        request.sessionData.forType
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      cateringOperationForm,
      formWithErrors =>
        BadRequest(
          cateringOperationOrLettingAccommodationView(
            formWithErrors,
            "cateringOperationOrLettingAccommodation",
            controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url,
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      data => {
        val isFromCYA   = navigator.from == "CYA"
        val updatedData =
          updateAboutFranchisesOrLettings(_.copy(cateringConcessionOrFranchise = Some(data), fromCYA = Some(isFromCYA)))
        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            navigator.cyaPage
              .filter(_ =>
                isFromCYA && (data == AnswerNo ||
                  request.sessionData.aboutFranchisesOrLettings
                    .flatMap(_.cateringConcessionOrFranchise)
                    .contains(AnswerYes))
              )
              .getOrElse(navigator.nextPage(CateringOperationPageId, updatedData).apply(updatedData))
          }
          .map(Redirect)
      }
    )
  }
}
