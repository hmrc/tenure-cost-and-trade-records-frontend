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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.FranchiseOrLettingsTiedToPropertyForm.franchiseOrLettingsTiedToPropertyForm
import models.ForType.*
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.FranchiseOrLettingsTiedToPropertyId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.franchiseOrLettingsTiedToProperty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FranchiseOrLettingsTiedToPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  franchiseOrLettingsTiedToPropertyView: franchiseOrLettingsTiedToProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("FranchiseOrLettingsTiedToProperty")

    Future.successful(
      Ok(
        franchiseOrLettingsTiedToPropertyView(
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty) match {
            case Some(franchisesOrLettingsTiedToProperty) =>
              franchiseOrLettingsTiedToPropertyForm.fill(franchisesOrLettingsTiedToProperty)
            case _                                        => franchiseOrLettingsTiedToPropertyForm
          },
          request.sessionData.forType,
          calculateBacklink,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      franchiseOrLettingsTiedToPropertyForm,
      formWithErrors =>
        BadRequest(
          franchiseOrLettingsTiedToPropertyView(
            formWithErrors,
            request.sessionData.forType,
            calculateBacklink,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutFranchisesOrLettings(_.copy(franchisesOrLettingsTiedToProperty = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            navigator.cyaPage
              .filter(_ =>
                navigator.from == "CYA" && (
                  (data == AnswerNo ||
                    request.sessionData.aboutFranchisesOrLettings
                      .flatMap(_.franchisesOrLettingsTiedToProperty)
                      .contains(AnswerYes)) ||
                    (data == AnswerYes && request.sessionData.forType == FOR6020 &&
                      request.sessionData.aboutFranchisesOrLettings
                        .flatMap(_.lettings.map(_.size > 0))
                        .getOrElse(false))
                )
              )
              .getOrElse(
                navigator.nextWithoutRedirectToCYA(FranchiseOrLettingsTiedToPropertyId, updatedData).apply(updatedData)
              )
          }
          .map(Redirect)
      }
    )
  }

  private def calculateBacklink(implicit request: SessionRequest[AnyContent]): String =
    if (navigator.from == "CYA") {
      controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    } else {
      controllers.routes.TaskListController.show().url + "#franchise-or-lettings-tied-to-property"
    }

}
