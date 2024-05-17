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
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.GeneratorCapacityForm.generatorCapacityForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo.updateAboutYouAndThePropertyPartTwo
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.GeneratorCapacityId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.generatorCapacity

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class GeneratorCapacityController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  view: generatorCapacity,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        view(
          request.sessionData.aboutYouAndThePropertyPartTwo.flatMap(_.generatorCapacity) match {
            case Some(data) => generatorCapacityForm.fill(data)
            case _          => generatorCapacityForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[String](
      generatorCapacityForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutYouAndThePropertyPartTwo(_.copy(generatorCapacity = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(GeneratorCapacityId, updatedData).apply(updatedData))
      }
    )
  }
}
