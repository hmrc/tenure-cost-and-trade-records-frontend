/*
 * Copyright 2023 HM Revenue & Customs
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
import form.aboutYourLeaseOrTenure.CheckYourAnswersAboutYourLeaseOrTenureForm.checkYourAnswersAboutFranchiseOrLettingsForm
import models.{ForTypes, Session}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.CheckYourAnswersAboutYourLeaseOrTenureId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.checkYourAnswersAboutYourLeaseOrTenure

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CheckYourAnswersAboutYourLeaseOrTenureController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  checkYourAnswersAboutYourLeaseOrTenureView: checkYourAnswersAboutYourLeaseOrTenure,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersAboutYourLeaseOrTenureView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.checkYourAnswersAboutYourLeaseOrTenure) match {
            case Some(checkYourAnswersAboutYourLeaseOrTenureView) =>
              checkYourAnswersAboutFranchiseOrLettingsForm.fillAndValidate(checkYourAnswersAboutYourLeaseOrTenureView)
            case _                                                => checkYourAnswersAboutFranchiseOrLettingsForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft(
      Redirect(navigator.nextPage(CheckYourAnswersAboutYourLeaseOrTenureId, request.sessionData).apply(request.sessionData))
    )
  }

  private def getBackLink(answers: Session): String =
    answers.aboutLeaseOrAgreementPartTwo.flatMap(
      _.legalOrPlanningRestrictions.map(_.legalPlanningRestrictions.name)
    ) match {
      case Some("yes") =>
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsDetailsController.show().url
      case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url
      case _           =>
        (
          answers.aboutLeaseOrAgreementPartOne.flatMap(
            _.leaseOrAgreementYearsDetails.map(_.commenceWithinThreeYears.name)
          ),
          answers.aboutLeaseOrAgreementPartOne.flatMap(
            _.leaseOrAgreementYearsDetails.map(_.agreedReviewedAlteredThreeYears.name)
          ),
          answers.aboutLeaseOrAgreementPartOne.flatMap(
            _.leaseOrAgreementYearsDetails.map(_.rentUnderReviewNegotiated.name)
          )
        ) match {
          case (Some("no"), Some("no"), Some("no")) =>
            controllers.aboutYourLeaseOrTenure.routes.CurrentRentPayableWithin12MonthsController.show().url
          case _                                    =>
            answers.forType match {
              case ForTypes.for6011 =>
                controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController.show().url
              case _                =>
                logger.warn(s"Navigation for CYA about lease without correct selection of conditions by controller")
                throw new RuntimeException("Invalid option exception for CYA about lease back link")
            }
        }
    }
}
