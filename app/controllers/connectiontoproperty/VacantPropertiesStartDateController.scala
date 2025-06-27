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

package controllers.connectiontoproperty

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.connectiontoproperty.VacantPropertyStartDateForm.vacantPropertyStartDateForm
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.PropertyBecomeVacantPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.vacantPropertyStartDate

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VacantPropertiesStartDateController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  vacantPropertyStartDateView: vacantPropertyStartDate,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("VacantPropertiesStartDate")

    Future.successful(
      Ok(
        vacantPropertyStartDateView(
          request.sessionData.stillConnectedDetails.flatMap(_.vacantPropertyStartDate) match {
            case Some(vacantPropertyStartDate) => vacantPropertyStartDateForm.fill(vacantPropertyStartDate)
            case _                             => vacantPropertyStartDateForm
          },
          request.sessionData.toSummary,
          calculateBackLink
        )
      )
    )

  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LocalDate](
      vacantPropertyStartDateForm,
      formWithErrors =>
        BadRequest(
          vacantPropertyStartDateView(
            formWithErrors,
            request.sessionData.toSummary,
            calculateBackLink
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(vacantPropertyStartDate = Some(data)))
        session.saveOrUpdate(updatedData).map { _ =>
          val redirectToCYA = navigator.cyaPageVacant.filter(_ => navigator.from == "CYA")
          val nextPage      =
            redirectToCYA.getOrElse(navigator.nextPage(PropertyBecomeVacantPageId, updatedData).apply(updatedData))
          Redirect(nextPage)
        }
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" =>
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      case _     => controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
    }

}
