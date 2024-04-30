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
import controllers.FORDataCaptureController
import models.submissions.aboutfranchisesorlettings.{ATMLetting, AboutFranchisesOrLettings, LettingPartOfProperty}
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.atmLetting
import form.aboutfranchisesorlettings.ATMLettingForm.atmLettingForm

import javax.inject.{Inject, Named}
import scala.concurrent.ExecutionContext

class AtmLettingController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  atmLettingView: atmLetting,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[ATMLetting] = for {
      requestedIndex   <- index
      existingLetting  <-
        request.sessionData.aboutFranchisesOrLettings.flatMap(
          _.lettings
        )
      requestedLetting <- existingLetting.lift(requestedIndex)
      atmLetting       <- requestedLetting match {
                            case atm: ATMLetting => Some(atm)
                            case _               => None
                          }
    } yield atmLetting

    Ok(
      atmLettingView(
        existingDetails.fold(atmLettingForm)(
          atmLettingForm.fill
        ),
        index,
        getBackLink(index),
        request.sessionData.toSummary
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ATMLetting](
      atmLettingForm,
      formWithErrors =>
        BadRequest(
          atmLettingView(
            formWithErrors,
            index,
            getBackLink(index),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedSession = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings { about =>
          val updatedLettings = updateOrAddATMLetting(about.lettings, data, index)
          about.copy(lettings = updatedLettings)
        }(request)

        session.saveOrUpdate(updatedSession).map { _ =>
          Redirect(controllers.aboutfranchisesorlettings.routes.RentDetailsController.show(index.getOrElse(0)))
        }
      }
    )
  }

  private def updateOrAddATMLetting(
    lettingsOpt: Option[IndexedSeq[LettingPartOfProperty]],
    atmLetting: ATMLetting,
    index: Option[Int]
  ): Option[IndexedSeq[LettingPartOfProperty]] =
    lettingsOpt match {
      case Some(lettings) =>
        index match {
          case Some(idx) if idx < lettings.length =>
            lettings(idx) match {
              case existingATM: ATMLetting =>
                Some(
                  lettings.updated(
                    idx,
                    existingATM.copy(
                      bankOrCompany = atmLetting.bankOrCompany,
                      correspondenceAddress = atmLetting.correspondenceAddress
                    )
                  )
                )
              case _                       =>
                Some(lettings.updated(idx, atmLetting))
            }
          case _                                  =>
            Some(lettings :+ atmLetting)
        }
      case None           =>
        Some(IndexedSeq(atmLetting))
    }

  private def getBackLink(idx: Option[Int])(implicit request: SessionRequest[AnyContent]): String =
    if (navigator.from == "CYA") {
      controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    } else {
      controllers.aboutfranchisesorlettings.routes.TypeOfLettingController.show(idx).url
    }
}
