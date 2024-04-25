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
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.BenefitsGivenDetailsForm.benefitsGivenDetailsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.BenefitsGivenDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.BenefitsGivenDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.benefitsGivenDetails

import javax.inject.{Inject, Named}
import scala.concurrent.ExecutionContext

class BenefitsGivenDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  view: benefitsGivenDetails,
  navigator: AboutYourLeaseOrTenureNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(
      view(
        request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.benefitsGivenDetails) match {
          case Some(data) => benefitsGivenDetailsForm.fill(data)
          case _          => benefitsGivenDetailsForm
        },
        request.sessionData.toSummary
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[BenefitsGivenDetails](
      benefitsGivenDetailsForm,
      formWithErrors => BadRequest(view(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartThree(_.copy(benefitsGivenDetails = Some(data)))
        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(BenefitsGivenDetailsId, updatedData).apply(updatedData))
        }
      }
    )
  }
}
