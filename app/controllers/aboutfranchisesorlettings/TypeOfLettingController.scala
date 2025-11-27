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

package controllers.aboutfranchisesorlettings

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.TypeOfLettingForm.typeOfLettingForm
import models.submissions.aboutfranchisesorlettings.{ATMLetting, AboutFranchisesOrLettings, AdvertisingRightLetting, LettingPartOfProperty, OtherLetting, TelecomMastLetting, TypeOfLetting}
import models.submissions.aboutfranchisesorlettings.TypeOfLetting.*
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import views.html.aboutfranchisesorlettings.typeOfLetting

import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class TypeOfLettingController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  typeOfLettingView: typeOfLetting,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[TypeOfLetting] = for {
      requestedIndex                <- index
      existingAccommodationSections <-
        request.sessionData.aboutFranchisesOrLettings.map(
          _.lettings.getOrElse(IndexedSeq.empty)
        )
      requestedAccommodationSection <- existingAccommodationSections.lift(requestedIndex)
    } yield requestedAccommodationSection.typeOfLetting
    audit.sendChangeLink("TypeOfLetting")

    Ok(
      typeOfLettingView(
        existingDetails.fold(typeOfLettingForm)(
          typeOfLettingForm.fill
        ),
        index,
        request.sessionData.toSummary,
        getBackLink(index)
      )
    )
  }

  def submit(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[TypeOfLetting](
      typeOfLettingForm,
      formWithErrors =>
        BadRequest(
          typeOfLettingView(
            formWithErrors,
            index,
            request.sessionData.toSummary,
            getBackLink(index)
          )
        ),
      data => {
        val newLetting       = createLettingType(data)
        val existingLettings =
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettings).getOrElse(IndexedSeq.empty)

        index match {
          case Some(idx) if idx >= 0 && idx < existingLettings.length =>
            val existingLetting = existingLettings(idx)
            if (existingLetting.getClass == newLetting.getClass && navigator.from == "CYA") {
              Future.successful(
                Redirect(
                  controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
                )
              )
            } else {
              val updatedLettings = existingLettings.updated(idx, newLetting)
              updateSessionAndRedirect(updatedLettings, data, index)
            }
          case _                                                      =>
            val updatedLettings = existingLettings :+ newLetting
            updateSessionAndRedirect(updatedLettings, data, index)
        }
      }
    )
  }

  private def updateSessionAndRedirect(
    updatedLettings: IndexedSeq[LettingPartOfProperty],
    lettingType: TypeOfLetting,
    index: Option[Int]
  )(implicit request: SessionRequest[AnyContent], hc: HeaderCarrier): Future[Result] = {
    val existingFranchisesOrLetting =
      request.sessionData.aboutFranchisesOrLettings.getOrElse(AboutFranchisesOrLettings())
    val updatedSession              = request.sessionData.copy(
      aboutFranchisesOrLettings = Some(
        existingFranchisesOrLetting.copy(lettings = Some(updatedLettings))
      )
    )
    session.saveOrUpdate(updatedSession).map { _ =>
      Redirect(toSpecificController(lettingType, index))
    }
  }
  private def createLettingType(typeOfLetting: TypeOfLetting): LettingPartOfProperty = typeOfLetting match {
    case TypeOfLettingAutomatedTellerMachine => ATMLetting(None, None, None)
    case TypeOfLettingTelecomMast            => TelecomMastLetting(None, None, None, None)
    case TypeOfLettingAdvertisingRight       => AdvertisingRightLetting(None, None, None, None)
    case TypeOfLettingOther                  => OtherLetting(None, None, None, None)
  }

  private def toSpecificController(typeOfLetting: TypeOfLetting, index: Option[Int]): Call = {
    val targetIndex = index.getOrElse(0) // Default to the first index if none provided
    typeOfLetting match {
      case TypeOfLettingAutomatedTellerMachine => routes.AtmLettingController.show(Some(targetIndex))
      case TypeOfLettingTelecomMast            => routes.TelecomMastLettingController.show(Some(targetIndex))
      case TypeOfLettingAdvertisingRight       => routes.AdvertisingRightLettingController.show(Some(targetIndex))
      case TypeOfLettingOther                  => routes.OtherLettingController.show(Some(targetIndex))
    }
  }

  private def getBackLink(idx: Option[Int])(implicit request: SessionRequest[AnyContent]): String =
    if (navigator.from == "CYA") {
      controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    } else {
      idx match {
        case Some(index) =>
          if (index > 0)
            controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController.show(index - 1).url
          else {
            controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url
          }
        case _           => controllers.routes.TaskListController.show().url
      }
    }
}
