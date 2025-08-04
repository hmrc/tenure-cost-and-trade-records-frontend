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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.CanRentBeReducedOnReviewForm.canRentBeReducedOnReviewForm
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.CanRentBeReducedOnReviewId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.canRentBeReducedOnReview

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CanRentBeReducedOnReviewController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  canRentBeReducedOnReviewView: canRentBeReducedOnReview,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("CanRentBeReducedOnReview")

    Future.successful(
      Ok(
        canRentBeReducedOnReviewView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.canRentBeReducedOnReview) match {
            case Some(data) => canRentBeReducedOnReviewForm.fill(data)
            case _          => canRentBeReducedOnReviewForm
          },
          getBackLink
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      canRentBeReducedOnReviewForm,
      formWithErrors => BadRequest(canRentBeReducedOnReviewView(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(canRentBeReducedOnReview = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(CanRentBeReducedOnReviewId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    request.sessionData.forType match {
      case FOR6020                                         =>
        if (
          request.sessionData.aboutLeaseOrAgreementPartTwo
            .flatMap(_.intervalsOfRentReview)
            .exists(_.intervalsOfRentReview.isDefined)
        ) {
          controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show().url
        } else {
          controllers.aboutYourLeaseOrTenure.routes.IsRentUnderReviewController.show().url
        }
      case FOR6010 | FOR6011 | FOR6015 | FOR6016 | FOR6030 =>
        request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.isRentReviewPlanned) match {
          case Some(AnswerYes) => controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show().url
          case _               => controllers.aboutYourLeaseOrTenure.routes.IsRentReviewPlannedController.show().url
        }
      case _                                               => controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show().url
    }
