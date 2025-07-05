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
import form.aboutYourLeaseOrTenure.HowIsCurrentRentFixedForm.howIsCurrentRentFixedForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.HowIsCurrentRentFixed
import models.submissions.common.AnswersYesNo.*
import models.ForType.*
import models.Session
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.HowIsCurrentRentFixedId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.howIsCurrentRentFixed

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HowIsCurrentRentFixedController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  howIsCurrentRentFixedView: howIsCurrentRentFixed,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("HowIsCurrentRentFixed")

    Future.successful(
      Ok(
        howIsCurrentRentFixedView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.howIsCurrentRentFixed) match {
            case Some(data) => howIsCurrentRentFixedForm.fill(data)
            case _          => howIsCurrentRentFixedForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[HowIsCurrentRentFixed](
      howIsCurrentRentFixedForm,
      formWithErrors =>
        BadRequest(
          howIsCurrentRentFixedView(formWithErrors, getBackLink(request.sessionData), request.sessionData.toSummary)
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(howIsCurrentRentFixed = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(HowIsCurrentRentFixedId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.forType match {
      case FOR6010                     =>
        answers.aboutLeaseOrAgreementPartTwo.flatMap(_.rentPayableVaryOnQuantityOfBeers) match {
          case Some(AnswerYes) =>
            controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersDetailsController.show().url
          case _               =>
            controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController.show().url
        }
      case FOR6020 | FOR6045 | FOR6046 =>
        answers.aboutLeaseOrAgreementPartOne.flatMap(_.rentOpenMarketValue) match {
          case Some(AnswerYes) => controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show().url
          case _               => controllers.aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController.show().url
        }
      case FOR6048                     =>
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleBuildingInsuranceController.show().url
      case _                           =>
        answers.aboutLeaseOrAgreementPartTwo.flatMap(_.rentPayableVaryAccordingToGrossOrNet) match {
          case Some(AnswerYes) =>
            controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetDetailsController.show().url
          case _               =>
            controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController.show().url
        }
    }
}
