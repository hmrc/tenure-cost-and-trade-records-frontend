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

package controllers

import actions.WithSessionRefiner
import models.ForType.*
import models.{DeclarationSection, DeclarationTask, FormOfReturn}
import models.DeclarationTaskState.*
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import views.html.improvedTaskList as TaskListView

import javax.inject.{Inject, Singleton}

@Singleton
class ImprovedTaskListController @Inject()(
  mcc: MessagesControllerComponents,
  theView: TaskListView,
  withSessionRefiner: WithSessionRefiner
) extends FORDataCaptureController(mcc)
  with I18nSupport:


  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val formOfReturn =
      request.sessionData.forType match
        case FOR6048 =>
          FormOfReturn(
            sections = List(
              DeclarationSection(
                id = "connection-to-the-property",
                messageKey = "label.section.connectionToTheProperty",
                tasks = List(
                  DeclarationTask(
                    id = "are-you-still-connected",
                    messageKey = "checkYourAnswersConnectionToProperty.connectionStatus.heading",
                    state = CannotStartYet,
                    call = controllers.connectiontoproperty.routes.AreYouStillConnectedController.show()
                  )
                )
              )
            )
          )
        case _ =>
          ???

    Ok(theView(formOfReturn))
  }