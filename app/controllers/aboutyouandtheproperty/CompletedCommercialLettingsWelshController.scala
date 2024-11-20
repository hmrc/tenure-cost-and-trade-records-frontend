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
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.CompletedCommercialLettingsWelshForm.completedCommercialLettingsWelshForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo.updateAboutYouAndThePropertyPartTwo
import models.submissions.aboutyouandtheproperty.{AboutYouAndThePropertyPartTwo, CompletedLettings, LettingAvailability}
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.CompletedCommercialLettingsWelshId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.completedCommercialLettingsWelsh

import java.time.LocalDate
import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class CompletedCommercialLettingsWelshController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  view: completedCommercialLettingsWelsh,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    request.sessionData.aboutYouAndThePropertyPartTwo
      .filter(_.commercialLetDate.isDefined)
      .fold(Redirect(routes.CommercialLettingQuestionController.show())) { data =>
        Ok(
          view(
            request.sessionData.aboutYouAndThePropertyPartTwo.flatMap(_.completedCommercialLettingsWelsh) match {
              case Some(completedLettings) =>
                completedCommercialLettingsWelshForm(years(data)).fill(completedLettings)
              case _                       => completedCommercialLettingsWelshForm(years(data))
            },
            calculateBackLink
          )
        )
      }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutYouAndThePropertyPartTwo
      .filter(_.commercialLetDate.isDefined)
      .fold(Future.successful(Redirect(routes.CommercialLettingQuestionController.show()))) { data =>
        continueOrSaveAsDraft[Seq[CompletedLettings]](
          completedCommercialLettingsWelshForm(years(data)),
          formWithErrors =>
            BadRequest(
              view(
                formWithErrors,
                calculateBackLink
              )
            ),
          success => {
            val updatedLettingData =
              (success zip financialYearEndDates(data)).map { case (completedLettings, finYearEnd) =>
                completedLettings.copy(financialYearEnd = finYearEnd)
              }
            val updatedData        = updateAboutYouAndThePropertyPartTwo { partTwo =>
              val commercialLetNightsSum = partTwo.commercialLetAvailabilityWelsh
                .getOrElse(Seq.empty)
                .map(_.numberOfNights)
                .sum

              val completedLettingsNightsSum = updatedLettingData.map(_.numberOfNights).sum

              val updatedCanProceed = commercialLetNightsSum >= 252 && completedLettingsNightsSum >= 182
              partTwo.copy(
                completedCommercialLettingsWelsh = Option(updatedLettingData),
                canProceed = Some(updatedCanProceed)
              )
            }
            session
              .saveOrUpdate(updatedData)
              .map(_ =>
                Redirect(navigator.nextPage(CompletedCommercialLettingsWelshId, updatedData).apply(updatedData))
              )
          }
        )
      }
  }

  private def years(data: AboutYouAndThePropertyPartTwo): Seq[String] =
    data.financialEndYearDates.fold(Seq.empty)(_.map(_.getYear.toString))

  private def financialYearEndDates(data: AboutYouAndThePropertyPartTwo): Seq[LocalDate] =
    data.financialEndYearDates.getOrElse(Seq.empty)

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      case _     => controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityWelshController.show().url
    }

}
