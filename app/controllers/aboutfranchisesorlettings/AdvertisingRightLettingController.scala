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
import form.aboutfranchisesorlettings.AdvertisingRightLettingForm.advertisingRightLettingForm
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, AdvertisingRightLetting, LettingPartOfProperty}
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.advertisingRightLetting

import javax.inject.{Inject, Named}
import scala.concurrent.ExecutionContext

class AdvertisingRightLettingController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  advertisingRightLettingView: advertisingRightLetting,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[AdvertisingRightLetting] = for {
      requestedIndex  <- index
      existingLetting <-
        request.sessionData.aboutFranchisesOrLettings.flatMap(
          _.lettings
        )

      requestedLetting   <- existingLetting.lift(requestedIndex)
      advertRightLetting <- requestedLetting match {
                              case adRightLetting: AdvertisingRightLetting => Some(adRightLetting)
                              case _                                       => None
                            }
    } yield advertRightLetting

    audit.sendChangeLink("AdvertisingRightLetting")
    Ok(
      advertisingRightLettingView(
        existingDetails.fold(advertisingRightLettingForm)(
          advertisingRightLettingForm.fill
        ),
        index,
        getBackLink(index),
        request.sessionData.toSummary
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AdvertisingRightLetting](
      advertisingRightLettingForm,
      formWithErrors =>
        BadRequest(
          advertisingRightLettingView(
            formWithErrors,
            index,
            getBackLink(index),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedSession = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings { about =>
          val updatedLettings = updateOrAddAdvertisingRightLetting(about.lettings, data, index)
          about.copy(lettings = updatedLettings)
        }(request)
        session.saveOrUpdate(updatedSession).map { _ =>
          if (navigator.from == "CYA") {
            Redirect(
              controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
            )
          } else {
            Redirect(controllers.aboutfranchisesorlettings.routes.RentDetailsController.show(index.getOrElse(0)))
          }
        }
      }
    )
  }

  private def updateOrAddAdvertisingRightLetting(
    lettingsOpt: Option[IndexedSeq[LettingPartOfProperty]],
    advertisingRightLetting: AdvertisingRightLetting,
    index: Option[Int]
  ): Option[IndexedSeq[LettingPartOfProperty]] =
    lettingsOpt match {
      case Some(lettings) =>
        index match {
          case Some(idx) if idx < lettings.length =>
            lettings(idx) match {
              case existingOther: AdvertisingRightLetting =>
                Some(
                  lettings.updated(
                    idx,
                    existingOther.copy(
                      advertisingCompanyName = advertisingRightLetting.advertisingCompanyName,
                      descriptionOfSpace = advertisingRightLetting.descriptionOfSpace,
                      correspondenceAddress = advertisingRightLetting.correspondenceAddress
                    )
                  )
                )
              case _                                      =>
                Some(lettings.updated(idx, advertisingRightLetting))
            }
          case _                                  =>
            Some(lettings :+ advertisingRightLetting)
        }
      case None           =>
        Some(IndexedSeq(advertisingRightLetting))
    }

  private def getBackLink(idx: Option[Int])(implicit request: SessionRequest[AnyContent]): String =
    if (navigator.from == "CYA") {
      controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    } else {
      controllers.aboutfranchisesorlettings.routes.TypeOfLettingController.show(idx).url
    }
}
