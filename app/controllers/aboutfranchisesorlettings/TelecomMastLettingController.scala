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
import form.aboutfranchisesorlettings.TelecomMastLettingForm.telecomMastLettingForm
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, LettingPartOfProperty, TelecomMastLetting}
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.telecomMastLetting

import javax.inject.{Inject, Named}
import scala.concurrent.ExecutionContext

class TelecomMastLettingController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  telecomMastLettingView: telecomMastLetting,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[TelecomMastLetting] = for {
      requestedIndex  <- index
      existingLetting <-
        request.sessionData.aboutFranchisesOrLettings.flatMap(
          _.lettings
        )

      requestedLetting   <- existingLetting.lift(requestedIndex)
      telecomMastLetting <- requestedLetting match {
                              case telco: TelecomMastLetting => Some(telco)
                              case _                         => None
                            }
    } yield telecomMastLetting
    audit.sendChangeLink("TelecomMastLetting")

    Ok(
      telecomMastLettingView(
        existingDetails.fold(telecomMastLettingForm)(
          telecomMastLettingForm.fill
        ),
        index,
        getBackLink(index),
        request.sessionData.toSummary
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    def updateOrAddTelecomMastLetting(
      lettingsOpt: Option[IndexedSeq[LettingPartOfProperty]],
      telecomMastLetting: TelecomMastLetting,
      index: Option[Int]
    ): Option[IndexedSeq[LettingPartOfProperty]] =
      lettingsOpt match {
        case Some(lettings) =>
          index match {
            case Some(idx) if idx < lettings.length =>
              lettings(idx) match {
                case existingOther: TelecomMastLetting =>
                  Some(
                    lettings.updated(
                      idx,
                      existingOther.copy(
                        operatingCompanyName = telecomMastLetting.operatingCompanyName,
                        siteOfMast = telecomMastLetting.siteOfMast,
                        correspondenceAddress = telecomMastLetting.correspondenceAddress
                      )
                    )
                  )
                case _                                 =>
                  Some(lettings.updated(idx, telecomMastLetting))
              }
            case _                                  =>
              Some(lettings :+ telecomMastLetting)
          }
        case None           =>
          Some(IndexedSeq(telecomMastLetting))
      }

    continueOrSaveAsDraft[TelecomMastLetting](
      telecomMastLettingForm,
      formWithErrors =>
        BadRequest(
          telecomMastLettingView(
            formWithErrors,
            index,
            getBackLink(index),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedSession = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings { about =>
          val updatedLettings = updateOrAddTelecomMastLetting(about.lettings, data, index)
          about.copy(lettings = updatedLettings)
        }(using request)
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

  private def getBackLink(idx: Option[Int])(implicit request: SessionRequest[AnyContent]): String =
    if (navigator.from == "CYA") {
      controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    } else {
      controllers.aboutfranchisesorlettings.routes.TypeOfLettingController.show(idx).url
    }
}
