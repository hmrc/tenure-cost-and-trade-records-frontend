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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.OccupiersDetailsForm.occupiersDetailsForm
import models.submissions.aboutyouandtheproperty.{AboutYouAndThePropertyPartTwo, OccupiersDetails}
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.OccupiersDetailsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.occupiersDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OccupiersDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  view: occupiersDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("OccupiersDetails")

    val existingDetails: Option[OccupiersDetails] =
      for {
        requestedIndex <- index
        occupiers      <-
          request.sessionData.aboutYouAndThePropertyPartTwo.map(_.occupiersList)
        requestedData  <- occupiers.lift(requestedIndex)
      } yield requestedData
    Future.successful(
      Ok(
        view(
          existingDetails.fold(occupiersDetailsForm)(occupiersDetailsForm.fill),
          index,
          getBackLink,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[OccupiersDetails](
      occupiersDetailsForm,
      formWithErrors =>
        Future.successful(
          BadRequest(
            view(
              formWithErrors,
              index,
              getBackLink,
              request.sessionData.toSummary
            )
          )
        ),
      data => {
        val updatedAboutYouAndTheProperty =
          request.sessionData.aboutYouAndThePropertyPartTwo.fold(
            AboutYouAndThePropertyPartTwo(occupiersList = IndexedSeq(data))
          ) { aboutProperty =>
            val existingOccupiersList = aboutProperty.occupiersList

            val updatedList = index match {
              case Some(index) if existingOccupiersList.isDefinedAt(index) =>
                existingOccupiersList.updated(index, data)
              case _                                                       => existingOccupiersList :+ data
            }

            aboutProperty.copy(occupiersList = updatedList, occupiersListIndex = updatedList.length - 1)
          }

        val updatedSessionData =
          request.sessionData.copy(aboutYouAndThePropertyPartTwo = Option(updatedAboutYouAndTheProperty))

        session.saveOrUpdate(updatedSessionData).map { _ =>
          Redirect(navigator.nextPage(OccupiersDetailsId, updatedSessionData).apply(updatedSessionData))
        }
      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      case _     =>
        controllers.aboutyouandtheproperty.routes.PartsUnavailableController.show().url
    }
}
