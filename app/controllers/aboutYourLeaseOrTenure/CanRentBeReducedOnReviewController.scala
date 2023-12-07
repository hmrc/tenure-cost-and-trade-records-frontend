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
import form.aboutYourLeaseOrTenure.CanRentBeReducedOnReviewForm.canRentBeReducedOnReviewForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.CanRentBeReducedOnReviewDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.CanRentBeReducedOnReviewId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.canRentBeReducedOnReview

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CanRentBeReducedOnReviewController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  canRentBeReducedOnReviewView: canRentBeReducedOnReview,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        canRentBeReducedOnReviewView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.canRentBeReducedOnReviewDetails) match {
            case Some(data) => canRentBeReducedOnReviewForm.fill(data)
            case _          => canRentBeReducedOnReviewForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CanRentBeReducedOnReviewDetails](
      canRentBeReducedOnReviewForm,
      formWithErrors => BadRequest(canRentBeReducedOnReviewView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(canRentBeReducedOnReviewDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(CanRentBeReducedOnReviewId, updatedData).apply(updatedData))
      }
    )
  }

}
