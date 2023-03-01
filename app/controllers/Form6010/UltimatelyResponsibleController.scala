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
import form.Form6010.UltimatelyResponsibleForm.ultimatelyResponsibleForm
import form.Form6010.SharedResponsibilitiesForm.sharedResponsibilitiesForm
import form.Form6010.IntervalsOfRentReviewForm.intervalsOfRentReviewForm
import models.submissions.Form6010.{BuildingInsurancesBoth, InsideRepairsBoth, OutsideRepairsBoth}
import models.submissions.aboutLeaseOrAgreement.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{intervalsOfRentReview, sharedResponsibilities, ultimatelyResponsible}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class UltimatelyResponsibleController @Inject() (
  mcc: MessagesControllerComponents,
  ultimatelyResponsibleView: ultimatelyResponsible,
  sharedResponsibilitiesView: sharedResponsibilities,
  intervalsOfRentReviewView: intervalsOfRentReview,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        ultimatelyResponsibleView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.ultimatelyResponsible) match {
            case Some(ultimatelyResponsible) => ultimatelyResponsibleForm.fillAndValidate(ultimatelyResponsible)
            case _                           => ultimatelyResponsibleForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    ultimatelyResponsibleForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(ultimatelyResponsibleView(formWithErrors))),
        data =>
          if (
            data.insideRepairs.equals(InsideRepairsBoth) || data.outsideRepairs
              .equals(OutsideRepairsBoth) || data.buildingInsurance.equals(BuildingInsurancesBoth)
          ) {
            val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(ultimatelyResponsible = Some(data)))
            session.saveOrUpdate(updatedData)
            Future.successful(Ok(sharedResponsibilitiesView(sharedResponsibilitiesForm)))
          } else {
            val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(ultimatelyResponsible = Some(data)))
            session.saveOrUpdate(updatedData)
            Future.successful(Ok(intervalsOfRentReviewView(intervalsOfRentReviewForm)))
          }
      )
  }

}
