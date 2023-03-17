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

package controllers.Form6010

import actions.WithSessionRefiner
import form.Form6010.IncentivesPaymentsConditionsForm.incentivesPaymentsConditionsForm
import form.Form6010.CanRentBeReducedOnReviewForm.canRentBeReducedOnReviewForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{canRentBeReducedOnReview, incentivesPaymentsConditions}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CanRentBeReducedOnReviewController @Inject() (
  mcc: MessagesControllerComponents,
  canRentBeReducedOnReviewView: canRentBeReducedOnReview,
  incentivesPaymentsConditionsView: incentivesPaymentsConditions,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        canRentBeReducedOnReviewView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.canRentBeReducedOnReviewDetails) match {
            case Some(data) => canRentBeReducedOnReviewForm.fillAndValidate(data)
            case _          => canRentBeReducedOnReviewForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    canRentBeReducedOnReviewForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(canRentBeReducedOnReviewView(formWithErrors))),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(canRentBeReducedOnReviewDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Ok(incentivesPaymentsConditionsView(incentivesPaymentsConditionsForm)))
        }
      )
  }
}
