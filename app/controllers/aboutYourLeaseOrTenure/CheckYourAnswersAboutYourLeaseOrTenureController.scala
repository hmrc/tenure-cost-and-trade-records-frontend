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
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.CheckYourAnswersAboutYourLeaseOrTenureForm.checkYourAnswersAboutYourLeaseOrTenureForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.common.AnswersYesNo.*
import models.ForType.*
import models.Session
import models.submissions.common.AnswersYesNo
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.CheckYourAnswersAboutYourLeaseOrTenureId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.checkYourAnswersAboutYourLeaseOrTenure

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersAboutYourLeaseOrTenureController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  checkYourAnswersAboutYourLeaseOrTenureView: checkYourAnswersAboutYourLeaseOrTenure,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersAboutYourLeaseOrTenureView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.checkYourAnswersAboutYourLeaseOrTenure) match {
            case Some(answer) => checkYourAnswersAboutYourLeaseOrTenureForm.fill(answer)
            case _            => checkYourAnswersAboutYourLeaseOrTenureForm
          },
          navigator.from match {
            case "CYA" =>
              controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show().url
            case _     => getBackLink(request.sessionData)
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      checkYourAnswersAboutYourLeaseOrTenureForm,
      formWithErrors =>
        BadRequest(
          checkYourAnswersAboutYourLeaseOrTenureView(
            formWithErrors,
            navigator.from match {
              case "CYA" =>
                controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show().url
              case _     => getBackLink(request.sessionData)
            },
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData =
          updateAboutLeaseOrAgreementPartOne(_.copy(checkYourAnswersAboutYourLeaseOrTenure = Some(data)))
            .copy(lastCYAPageUrl =
              Some(
                controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show().url
              )
            )
        session.saveOrUpdate(updatedData).flatMap { _ =>
          Future.successful(
            Redirect(navigator.nextPage(CheckYourAnswersAboutYourLeaseOrTenureId, updatedData).apply(updatedData))
          )
        }
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.aboutLeaseOrAgreementPartTwo.flatMap(
      _.legalOrPlanningRestrictions.map(_.legalPlanningRestrictions)
    ) match {
      case Some(AnswerYes) =>
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsDetailsController.show().url
      case Some(AnswerNo)  => controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url
      case _               =>
        (
          answers.aboutLeaseOrAgreementPartOne.flatMap(
            _.leaseOrAgreementYearsDetails.map(_.commenceWithinThreeYears)
          ),
          answers.aboutLeaseOrAgreementPartOne.flatMap(
            _.leaseOrAgreementYearsDetails.map(_.agreedReviewedAlteredThreeYears)
          ),
          answers.aboutLeaseOrAgreementPartOne.flatMap(
            _.leaseOrAgreementYearsDetails.map(_.rentUnderReviewNegotiated)
          )
        ) match {
          case (Some(AnswerNo), Some(AnswerNo), Some(AnswerNo)) =>
            controllers.aboutYourLeaseOrTenure.routes.CurrentRentPayableWithin12MonthsController.show().url
          case _                                                =>
            answers.forType match {
              case FOR6010 =>
                controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show().url
              case FOR6011 =>
                controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController.show().url
              case FOR6076 =>
                controllers.aboutYourLeaseOrTenure.routes.ProvideDetailsOfYourLeaseController.show().url
              case _       =>
                logger.warn(s"Navigation for CYA about lease without correct selection of conditions by controller")
                throw new RuntimeException("Invalid option exception for CYA about lease back link")
            }
        }
    }

}
