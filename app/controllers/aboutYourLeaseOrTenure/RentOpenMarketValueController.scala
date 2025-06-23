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

package controllers.aboutYourLeaseOrTenure

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.RentOpenMarketValueForm.rentOpenMarketValuesForm
import models.ForType.*
import models.Session
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.RentOpenMarketValueDetails
import models.submissions.common.AnswersYesNo.*
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.RentOpenMarketPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.rentOpenMarketValue

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RentOpenMarketValueController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentOpenMarketValueView: rentOpenMarketValue,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("RentOpenMarketValue")

    Future.successful(
      Ok(
        rentOpenMarketValueView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.rentOpenMarketValueDetails) match {
            case Some(rentOpenMarketValueDetails) =>
              rentOpenMarketValuesForm.fill(rentOpenMarketValueDetails)
            case _                                => rentOpenMarketValuesForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[RentOpenMarketValueDetails](
      rentOpenMarketValuesForm,
      formWithErrors =>
        BadRequest(
          rentOpenMarketValueView(formWithErrors, getBackLink(request.sessionData), request.sessionData.toSummary)
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(rentOpenMarketValueDetails = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(RentOpenMarketPageId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(answers: Session)(implicit request: Request[AnyContent]): String =
    navigator.from match {
      case "TL" => controllers.routes.TaskListController.show().url + "#rent-open-market-value"
      case _    =>
        answers.aboutLeaseOrAgreementPartOne.flatMap(
          _.rentIncludeFixturesAndFittingsDetails.map(_.rentIncludeFixturesAndFittingsDetails)
        ) match {
          case Some(AnswerYes) =>
            answers.forType match {
              case FOR6020 =>
                controllers.aboutYourLeaseOrTenure.routes.IncludedInRent6020Controller.show().url
              case _       =>
                controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsDetailsController.show().url
            }
          case _               =>
            answers.forType match {
              case FOR6020 =>
                controllers.aboutYourLeaseOrTenure.routes.IncludedInRent6020Controller.show().url
              case _       =>
                controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
            }
        }
    }

}
