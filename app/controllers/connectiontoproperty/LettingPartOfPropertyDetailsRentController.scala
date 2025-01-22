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

package controllers.connectiontoproperty

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.connectiontoproperty.LettingPartOfPropertyRentForm.lettingPartOfPropertyRentForm
import models.audit.ChangeLinkAudit
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.LettingPartOfPropertyRentDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.LettingPartOfPropertyRentDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.lettingPartOfPropertyRentDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LettingPartOfPropertyDetailsRentController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  lettingPartOfPropertyRentDetailsView: lettingPartOfPropertyRentDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingSection = request.sessionData.stillConnectedDetails.flatMap(_.lettingPartOfPropertyDetails.lift(index))
    existingSection.fold(Redirect(routes.LettingPartOfPropertyDetailsController.show(None))) {
      lettingPartOfPropertyDetails =>
        val lettingDetailsForm = lettingPartOfPropertyDetails.lettingPartOfPropertyRentDetails.fold(
          lettingPartOfPropertyRentForm
        )(lettingPartOfPropertyRentForm.fill)

        if (request.getQueryString("from").contains("CYA")) {
          audit.sendExplicitAudit(
            "cya-change-link",
            ChangeLinkAudit(request.sessionData.forType.toString, request.uri, "LettingPartOfPropertyDetailsRent")
          )
        }

        Ok(
          lettingPartOfPropertyRentDetailsView(
            lettingDetailsForm,
            index,
            existingSection.get.tenantDetails.name,
            calculateBackLink(index),
            request.sessionData.toSummary
          )
        )
    }

  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection = request.sessionData.stillConnectedDetails.map(_.lettingPartOfPropertyDetails).get(index)

    continueOrSaveAsDraft[LettingPartOfPropertyRentDetails](
      lettingPartOfPropertyRentForm,
      formWithErrors =>
        Future.successful(
          BadRequest(
            lettingPartOfPropertyRentDetailsView(
              formWithErrors,
              index,
              existingSection.tenantDetails.name,
              calculateBackLink(index),
              request.sessionData.toSummary
            )
          )
        ),
      data =>
        request.sessionData.stillConnectedDetails.fold(
          Future.successful(
            Redirect(routes.LettingPartOfPropertyDetailsController.show(Some(index)))
          )
        ) { stillConnectedDetails =>
          val existingSections = stillConnectedDetails.lettingPartOfPropertyDetails
          val updatedSections  = existingSections
            .updated(index, existingSections(index).copy(lettingPartOfPropertyRentDetails = Some(data)))
          val updatedData      = updateStillConnectedDetails(_.copy(lettingPartOfPropertyDetails = updatedSections))
          session.saveOrUpdate(updatedData).map { _ =>
            val redirectToCYA = navigator.cyaPageVacant.filter(_ => navigator.from(request) == "CYA")
            val nextPage      =
              redirectToCYA
                .getOrElse(navigator.nextPage(LettingPartOfPropertyRentDetailsPageId, updatedData).apply(updatedData))
            Redirect(nextPage)
          }
        }
    )
  }

  private def calculateBackLink(index: Int)(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => navigator.cyaPageDependsOnSession(request.sessionData).map(_.url).getOrElse("")
      case _     => controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show(Some(index)).url
    }

}
