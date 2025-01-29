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
import form.connectiontoproperty.LettingPartOfPropertyRentIncludesForm.lettingPartOfPropertyRentIncludesForm
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.LettingPartOfPropertyItemsIncludedInRentPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.connectiontoproperty.lettingPartOfPropertyRentIncludes

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LettingPartOfPropertyItemsIncludedInRentController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  lettingPartOfPropertyRentIncludesView: lettingPartOfPropertyRentIncludes,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("LettingPartOfPropertyItemsIncludedInRent")

    request.sessionData.stillConnectedDetails
      .flatMap(_.lettingPartOfPropertyDetails.lift(index))
      .fold(
        startRedirect
      ) { currentSection =>
        val rentIncludesForm = lettingPartOfPropertyRentIncludesForm.fill(currentSection.itemsIncludedInRent)

        Ok(
          lettingPartOfPropertyRentIncludesView(
            rentIncludesForm,
            index,
            currentSection.tenantDetails.name,
            calculateBackLink(index),
            request.sessionData.toSummary
          )
        )
      }

  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    (for {
      existingSections <- request.sessionData.stillConnectedDetails.map(_.lettingPartOfPropertyDetails)
      currentSection   <- existingSections.lift(index)
    } yield continueOrSaveAsDraft[List[String]](
      lettingPartOfPropertyRentIncludesForm,
      formWithErrors =>
        BadRequest(
          lettingPartOfPropertyRentIncludesView(
            formWithErrors,
            index,
            currentSection.tenantDetails.name,
            calculateBackLink(index),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedSections = existingSections.updated(
          index,
          currentSection.copy(itemsIncludedInRent = data)
        )
        val updatedSession  = updateStillConnectedDetails(_.copy(lettingPartOfPropertyDetails = updatedSections))
        session.saveOrUpdate(updatedSession).map { _ =>
          val redirectToCYA = navigator.cyaPageVacant.filter(_ => navigator.from(request) == "CYA")
          val nextPage      =
            redirectToCYA.getOrElse(
              navigator.nextPage(LettingPartOfPropertyItemsIncludedInRentPageId, updatedSession).apply(updatedSession)
            )
          Redirect(nextPage)
        }
      }
    )).getOrElse(startRedirect)
  }

  private def calculateBackLink(index: Int)(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => navigator.cyaPageDependsOnSession(request.sessionData).map(_.url).getOrElse("")
      case _     => controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsRentController.show(index).url
    }

  private def startRedirect: Result = Redirect(routes.LettingPartOfPropertyDetailsController.show(None))

}
