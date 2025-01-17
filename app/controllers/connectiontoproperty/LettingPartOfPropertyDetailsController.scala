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
import form.connectiontoproperty.TenantDetailsForm.tenantDetailsForm
import models.audit.ChangeLinkAudit
import models.submissions.connectiontoproperty.{LettingPartOfPropertyDetails, StillConnectedDetails, TenantDetails}
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.LettingPartOfPropertyDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.tenantDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LettingPartOfPropertyDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  tenantDetailsView: tenantDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[TenantDetails] = for {
      requestedIndex                        <- index
      existingLettingPartOfPropertyDetails  <-
        request.sessionData.stillConnectedDetails.map(_.lettingPartOfPropertyDetails)
      // lift turns exception-throwing access by index into an option-returning safe operation
      requestedLettingPartOfPropertyDetails <- existingLettingPartOfPropertyDetails.lift(requestedIndex)
    } yield requestedLettingPartOfPropertyDetails.tenantDetails

    val containCYA = request.uri
    val forType    = request.sessionData.forType

    containCYA match {
      case containsCYA if containsCYA.contains("=CYA") =>
        audit.sendExplicitAudit(
          "cya-change-link",
          ChangeLinkAudit(forType.toString, request.uri, "LettingPartOfPropertyDetails")
        )
      case _                                           =>
        Ok(
          tenantDetailsView(
            existingDetails.fold(tenantDetailsForm)(tenantDetailsForm.fill),
            index,
            getBackLink(index),
            request.sessionData.toSummary
          )
        )
    }
    Ok(
      tenantDetailsView(
        existingDetails.fold(tenantDetailsForm)(tenantDetailsForm.fill),
        index,
        getBackLink(index),
        request.sessionData.toSummary
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[TenantDetails](
      tenantDetailsForm,
      formWithErrors =>
        BadRequest(
          tenantDetailsView(
            formWithErrors,
            index,
            getBackLink(index),
            request.sessionData.toSummary
          )
        ),
      data => {
        val ifLettingPartOfPropertyDetailsEmpty = StillConnectedDetails(lettingPartOfPropertyDetails =
          IndexedSeq(LettingPartOfPropertyDetails(tenantDetails = data))
        )
        val updatedStillConnectedDetails        =
          request.sessionData.stillConnectedDetails.fold(ifLettingPartOfPropertyDetailsEmpty) { stillConnectedDetails =>
            val existingSections                                                 = stillConnectedDetails.lettingPartOfPropertyDetails
            val requestedSection                                                 = index.flatMap(existingSections.lift)
            val updatedSections: (Int, IndexedSeq[LettingPartOfPropertyDetails]) = requestedSection.fold {
              val defaultSection   = LettingPartOfPropertyDetails(data)
              val appendedSections = existingSections.appended(defaultSection)
              appendedSections.indexOf(defaultSection) -> appendedSections
            } { sectionToUpdate =>
              val indexToUpdate = existingSections.indexOf(sectionToUpdate)
              indexToUpdate -> existingSections
                .updated(indexToUpdate, sectionToUpdate.copy(tenantDetails = data))
            }
            stillConnectedDetails
              .copy(
                lettingPartOfPropertyDetailsIndex = updatedSections._1,
                lettingPartOfPropertyDetails = updatedSections._2
              )
          }
        val updatedData                         = updateStillConnectedDetails(_ => updatedStillConnectedDetails)
        session.saveOrUpdate(updatedData).map { _ =>
          val redirectToCYA = navigator.cyaPageVacant.filter(_ => navigator.from(request) == "CYA")
          val nextPage      =
            redirectToCYA
              .getOrElse(navigator.nextPage(LettingPartOfPropertyDetailsPageId, updatedData).apply(updatedData))
          Redirect(nextPage)
        }
      }
    )
  }

  private def getBackLink(mayBeIndex: Option[Int])(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      case _     =>
        mayBeIndex match {
          case Some(index) if index > 0 =>
            controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(index - 1).url
          case _                        => controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
        }
    }
}
