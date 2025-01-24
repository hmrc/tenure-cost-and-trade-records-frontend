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
import form.aboutyouandtheproperty.BatteriesCapacityForm.batteriesCapacityForm
import models.audit.ChangeLinkAudit
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo.updateAboutYouAndThePropertyPartTwo
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.BatteriesCapacityId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.batteriesCapacity

import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class BatteriesCapacityController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  view: batteriesCapacity,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    if (request.getQueryString("from").contains("CYA")) {
      audit.sendExplicitAudit(
        "CyaChangeLink",
        ChangeLinkAudit(request.sessionData.forType.toString, request.uri, "BatteriesCapacity")
      )
    }
    Future.successful(
      Ok(
        view(
          request.sessionData.aboutYouAndThePropertyPartTwo.flatMap(_.batteriesCapacity) match {
            case Some(data) => batteriesCapacityForm.fill(data)
            case _          => batteriesCapacityForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[String](
      batteriesCapacityForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutYouAndThePropertyPartTwo(_.copy(batteriesCapacity = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(BatteriesCapacityId, updatedData).apply(updatedData)))
      }
    )
  }
}
