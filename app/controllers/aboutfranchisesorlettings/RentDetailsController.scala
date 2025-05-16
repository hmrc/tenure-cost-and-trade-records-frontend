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

package controllers.aboutfranchisesorlettings

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.rentDetails
import form.aboutfranchisesorlettings.RentDetailsForm.rentDetailsForm
import models.submissions.aboutfranchisesorlettings.{ATMLetting, AboutFranchisesOrLettings, AdvertisingRightLetting, LettingPartOfProperty, OtherLetting, RentDetails, TelecomMastLetting}

import javax.inject.{Inject, Named}
import scala.concurrent.ExecutionContext

class RentDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  rentDetailsView: rentDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[LettingPartOfProperty] = for {
      existingAccommodationSections <-
        request.sessionData.aboutFranchisesOrLettings.map(
          _.lettings.getOrElse(IndexedSeq.empty)
        )
      requestedAccommodationSection <- existingAccommodationSections.lift(idx)
    } yield requestedAccommodationSection
    val rentDetails: Option[RentDetails]               = existingDetails.flatMap(_.rentalDetails)
    val operatorName: String                           = getOperatorName(existingDetails)
    val rentalDetailForm                               = rentDetails.fold(rentDetailsForm)(rentDetailsForm.fill)
    audit.sendChangeLink("RentDetails")

    Ok(
      rentDetailsView(
        rentalDetailForm,
        idx,
        operatorName,
        getBackLink(existingDetails, Some(idx)),
        request.sessionData.toSummary
      )
    )
  }

  private def getOperatorName(existingDetails: Option[LettingPartOfProperty]) = {
    val operatorName = existingDetails match {
      case Some(atm: ATMLetting)                      => atm.bankOrCompany.getOrElse("")
      case Some(telecomMast: TelecomMastLetting)      => telecomMast.operatingCompanyName.getOrElse("")
      case Some(advertRight: AdvertisingRightLetting) => advertRight.advertisingCompanyName.getOrElse("")
      case Some(otherLetting: OtherLetting)           => otherLetting.tenantName.getOrElse("")
      case _                                          => "Unknown operator"
    }
    operatorName
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    val existingDetails = request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettings.get.lift(index))
    continueOrSaveAsDraft[RentDetails](
      rentDetailsForm,
      formWithErrors =>
        BadRequest(
          rentDetailsView(
            formWithErrors,
            index,
            getOperatorName(existingDetails),
            getBackLink(existingDetails, Some(index)),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedLetting                   = existingDetails.map {
          case atm: ATMLetting                      => atm.copy(rentalDetails = Some(data))
          case telecom: TelecomMastLetting          => telecom.copy(rentalDetails = Some(data))
          case advertising: AdvertisingRightLetting => advertising.copy(rentalDetails = Some(data))
          case other: OtherLetting                  => other.copy(rentalDetails = Some(data))
        }
        val updatedAboutFranchisesOrLettings = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings { about =>
          val existingLettings = about.lettings.getOrElse(IndexedSeq.empty)
          val updatedLettings  = existingLettings.updated(index, updatedLetting.getOrElse(existingLettings(index)))
          about.copy(lettings = Some(updatedLettings))
        }
        session.saveOrUpdate(updatedAboutFranchisesOrLettings).map { _ =>
          val aboutFranchisesOrLettings =
            request.sessionData.aboutFranchisesOrLettings.getOrElse(AboutFranchisesOrLettings())
          val lettings                  = aboutFranchisesOrLettings.lettings.getOrElse(IndexedSeq.empty)
          if (index >= 0 && index < lettings.length) {
            if (navigator.from == "CYA") {
              Redirect(routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())
            } else {
              Redirect(routes.AddOrRemoveLettingController.show(lettings.length - 1))
            }
          } else {
            Redirect(routes.AddOrRemoveLettingController.show(0))
          }
        }
      }
    )
  }

  private def getBackLink(existingDetails: Option[LettingPartOfProperty], idx: Option[Int])(implicit
    request: SessionRequest[AnyContent]
  ): String =
    if (navigator.from == "CYA") {
      routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    } else {
      getUrlByType(existingDetails, idx)
    }

  private def getUrlByType(existingDetails: Option[LettingPartOfProperty], idx: Option[Int]) =
    existingDetails match {
      case Some(_: ATMLetting)              => routes.AtmLettingController.show(idx).url
      case Some(_: TelecomMastLetting)      => routes.TelecomMastLettingController.show(idx).url
      case Some(_: AdvertisingRightLetting) => routes.AdvertisingRightLettingController.show(idx).url
      case Some(_: OtherLetting)            => routes.OtherLettingController.show(idx).url
      case _                                => routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    }

}
