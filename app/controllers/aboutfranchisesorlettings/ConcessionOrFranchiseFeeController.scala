/*
 * Copyright 2024 HM Revenue & Customs
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
import form.aboutfranchisesorlettings.ConcessionOrFranchiseFeeForm.concessionOrFranchiseFeeForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.ConcessionOrFranchiseFeePageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ConcessionOrFranchiseFeeController @Inject() (
  mcc: MessagesControllerComponents,
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
    Ok(
      cateringOperationOrLettingAccommodationView(
        request.sessionData.aboutFranchisesOrLettings.flatMap(_.cateringOrFranchiseFee) match {
          case Some(cateringOrFranchiseFee) =>
            concessionOrFranchiseFeeForm.fill(cateringOrFranchiseFee)
          case _                            => concessionOrFranchiseFeeForm
        },
        "concessionOrFranchiseFee",
        backLink,
        request.sessionData.toSummary,
        request.sessionData.forType
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      concessionOrFranchiseFeeForm,
      formWithErrors =>
        BadRequest(
          cateringOperationOrLettingAccommodationView(
            formWithErrors,
            "concessionOrFranchiseFee",
            controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url,
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      data => {
        val isFromCYA   = navigator.from == "CYA"
        val updatedData =
          updateAboutFranchisesOrLettings(_.copy(cateringOrFranchiseFee = Some(data), fromCYA = Some(isFromCYA)))
        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            navigator.cyaPage
              .filter(_ =>
                isFromCYA && (data == AnswerNo ||
                  request.sessionData.aboutFranchisesOrLettings
                    .flatMap(_.cateringOrFranchiseFee)
                    .contains(AnswerYes))
              )
              .orElse(
                Option.when(isFromCYA)(
                  controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(0)
                )
              )
              .getOrElse(
                navigator.nextWithoutRedirectToCYA(ConcessionOrFranchiseFeePageId, updatedData).apply(updatedData)
              )
          }
          .map(Redirect)
      }
    )
  }

}
