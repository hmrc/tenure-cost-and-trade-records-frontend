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
import form.Form6010.IntervalsOfRentReviewForm.intervalsOfRentReviewForm
import form.Form6010.IncentivesPaymentsConditionsForm.incentivesPaymentsConditionsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{incentivesPaymentsConditions, intervalsOfRentReview}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class IntervalsOfRentReviewController @Inject() (
  mcc: MessagesControllerComponents,
  incentivesPaymentsConditionsView: incentivesPaymentsConditions,
  intervalsOfRentReviewView: intervalsOfRentReview,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      intervalsOfRentReviewView(
        request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.intervalsOfRentReview) match {
          case Some(data) => intervalsOfRentReviewForm.fillAndValidate(data)
          case _          => intervalsOfRentReviewForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    intervalsOfRentReviewForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(intervalsOfRentReviewView(formWithErrors))),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(intervalsOfRentReview = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Ok(incentivesPaymentsConditionsView(incentivesPaymentsConditionsForm)))
        }
      )
  }

}
