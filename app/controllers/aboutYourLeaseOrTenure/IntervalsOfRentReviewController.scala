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

package controllers.aboutYourLeaseOrTenure

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.IntervalsOfRentReviewForm.intervalsOfRentReviewForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.IntervalsOfRentReview
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.IntervalsOfRentReviewId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.intervalsOfRentReview

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class IntervalsOfRentReviewController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  intervalsOfRentReviewView: intervalsOfRentReview,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("IntervalsOfRentReview")

    Ok(
      intervalsOfRentReviewView(
        request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.intervalsOfRentReview) match {
          case Some(data) => intervalsOfRentReviewForm.fill(data)
          case _          => intervalsOfRentReviewForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[IntervalsOfRentReview](
      intervalsOfRentReviewForm,
      formWithErrors => BadRequest(intervalsOfRentReviewView(formWithErrors)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(intervalsOfRentReview = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(IntervalsOfRentReviewId, updatedData).apply(updatedData)))

      }
    )
  }

}
