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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.RentFreePeriodDetailsForm.rentFreePeriodDetailsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartFour
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartFour.updateAboutLeaseOrAgreementPartFour
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.RentFreePeriodDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.rentFreePeriodDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class RentFreePeriodDetailsController @Inject() (
  rentFreePeriodDetailsView: rentFreePeriodDetails,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("RentFreePeriodDetails")

    Ok(
      rentFreePeriodDetailsView(
        rentFreePeriodDetailsForm.fill(
          leaseOrAgreementPartFour.flatMap(_.rentFreePeriodDetails)
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[Option[String]](
      rentFreePeriodDetailsForm,
      formWithErrors => BadRequest(rentFreePeriodDetailsView(formWithErrors)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartFour(_.copy(rentFreePeriodDetails = data))

        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(RentFreePeriodDetailsId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def leaseOrAgreementPartFour(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutLeaseOrAgreementPartFour] = request.sessionData.aboutLeaseOrAgreementPartFour

}
