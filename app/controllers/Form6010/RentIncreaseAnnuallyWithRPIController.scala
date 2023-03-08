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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{rentIncreaseAnnuallyWithRPI, rentPayableVaryAccordingToGrossOrNet}
import form.Form6010.RentIncreasedAnnuallyWithRPIForm.rentIncreasedAnnuallyWithRPIDetailsForm
import form.Form6010.RentPayableVaryAccordingToGrossOrNetForm.rentPayableVaryAccordingToGrossOrNetForm
import models.submissions.aboutLeaseOrAgreement.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import play.api.i18n.I18nSupport
import repositories.SessionRepo

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentIncreaseAnnuallyWithRPIController @Inject() (
  mcc: MessagesControllerComponents,
  rentPayableVaryAccordingToGrossOrNetView: rentPayableVaryAccordingToGrossOrNet,
  rentIncreaseAnnuallyWithRPIView: rentIncreaseAnnuallyWithRPI,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentIncreaseAnnuallyWithRPIView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.rentIncreasedAnnuallyWithRPIDetails) match {
            case Some(rentIncreasedAnnuallyWithRPIDetails) =>
              rentIncreasedAnnuallyWithRPIDetailsForm.fillAndValidate(rentIncreasedAnnuallyWithRPIDetails)
            case None                                      => rentIncreasedAnnuallyWithRPIDetailsForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    rentIncreasedAnnuallyWithRPIDetailsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(rentIncreaseAnnuallyWithRPIView(formWithErrors))),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(rentIncreasedAnnuallyWithRPIDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Ok(rentPayableVaryAccordingToGrossOrNetView(rentPayableVaryAccordingToGrossOrNetForm)))
        }
      )
  }
}
