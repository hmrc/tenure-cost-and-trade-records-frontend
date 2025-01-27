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
import form.aboutyouandtheproperty.CommercialLettingAvailabilityWelshForm.commercialLettingAvailabilityWelshForm
import models.audit.ChangeLinkAudit
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo.updateAboutYouAndThePropertyPartTwo
import models.submissions.aboutyouandtheproperty.{AboutYouAndThePropertyPartTwo, LettingAvailability}
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.CommercialLettingAvailabilityWelshId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.commercialLettingAvailabilityWelsh

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CommercialLettingAvailabilityWelshController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  view: commercialLettingAvailabilityWelsh,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("CommercialLettingAvailabilityWelsh")


    request.sessionData.aboutYouAndThePropertyPartTwo
      .filter(_.commercialLetDate.isDefined)
      .fold(Redirect(routes.CommercialLettingQuestionController.show())) { data =>
        Ok(
          view(
            request.sessionData.aboutYouAndThePropertyPartTwo.flatMap(_.commercialLetAvailabilityWelsh) match {
              case Some(lettingAvailability) =>
                commercialLettingAvailabilityWelshForm(years(data)).fill(lettingAvailability)
              case _                         => commercialLettingAvailabilityWelshForm(years(data))
            },
            calculateBackLink
          )
        )
      }
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutYouAndThePropertyPartTwo
      .filter(_.commercialLetDate.isDefined)
      .fold(Future(Redirect(routes.CommercialLettingQuestionController.show()))) { data =>
        continueOrSaveAsDraft[Seq[LettingAvailability]](
          commercialLettingAvailabilityWelshForm(years(data)),
          formWithErrors =>
            BadRequest(
              view(
                formWithErrors,
                calculateBackLink
              )
            ),
          success => {
            val updatedLettingData =
              (success zip financialYearEndDates(data)).map { case (lettingAvailability, finYearEnd) =>
                lettingAvailability.copy(financialYearEnd = finYearEnd)
              }

            val updatedData =
              updateAboutYouAndThePropertyPartTwo(_.copy(commercialLetAvailabilityWelsh = Some(updatedLettingData)))
            session
              .saveOrUpdate(updatedData)
              .map(_ =>
                Redirect(navigator.nextPage(CommercialLettingAvailabilityWelshId, updatedData).apply(updatedData))
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
      case _     => controllers.aboutyouandtheproperty.routes.CommercialLettingQuestionController.show().url
    }

}
