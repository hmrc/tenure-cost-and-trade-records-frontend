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

package controllers.aboutyouandtheproperty

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.TiedForGoodsForm.tiedForGoodsForm
import models.Session
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.TiedForGoodsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.tiedForGoods

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TiedForGoodsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  tiedForGoodsView: tiedForGoods,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("TiedForGoods")

    Future.successful(
      Ok(
        tiedForGoodsView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.tiedForGoods) match {
            case Some(tiedForGoods) => tiedForGoodsForm.fill(tiedForGoods)
            case _                  => tiedForGoodsForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      tiedForGoodsForm,
      formWithErrors =>
        BadRequest(
          tiedForGoodsView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(tiedForGoods = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(TiedForGoodsPageId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.aboutYouAndTheProperty.flatMap(_.enforcementAction) match {
      case Some(AnswerYes) =>
        controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenDetailsController.show().url
      case _               => controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show().url
    }

}
