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
import form.aboutyouandtheproperty.ThreeYearsConstructedForm.theForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.common.AnswersYesNo
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.ThreeYearsConstructedPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.threeYearsConstructed as ThreeYearsConstructedView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ThreeYearsConstructedController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  theView: ThreeYearsConstructedView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repo: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with ReadOnlySupport
    with I18nSupport
    with Logging:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("ThreeYearsConstructed")
    Ok(
      theView(
        request.sessionData.aboutYouAndTheProperty.flatMap(_.threeYearsConstructed) match {
          case Some(tiedForGoods) => theForm.fill(tiedForGoods)
          case _                  => theForm
        },
        navigator.from,
        request.sessionData.toSummary,
        isReadOnly
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(
            formWithErrors,
            navigator.from,
            request.sessionData.toSummary,
            isReadOnly
          )
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(threeYearsConstructed = Some(data)))
        repo
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(ThreeYearsConstructedPageId, updatedData).apply(updatedData)))
      }
    )
  }
