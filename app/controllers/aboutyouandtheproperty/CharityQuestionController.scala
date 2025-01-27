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

package controllers.aboutyouandtheproperty

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.CharityQuestionForm.charityQuestionForm
import models.audit.ChangeLinkAudit
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.common.AnswersYesNo
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.CharityQuestionPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.charityQuestion

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CharityQuestionController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  charityQuestionView: charityQuestion,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("CharityQuestion")

    Future.successful(
      Ok(
        charityQuestionView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.charityQuestion) match {
            case Some(answer) =>
              charityQuestionForm.fill(answer)
            case _            => charityQuestionForm
          },
          request.sessionData.toSummary,
          navigator.from
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      charityQuestionForm,
      formWithErrors => BadRequest(charityQuestionView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(charityQuestion = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(CharityQuestionPageId, updatedData).apply(updatedData)))
      }
    )
  }

}
