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
import controllers.{FORDataCaptureController, aboutYourLeaseOrTenure}
import form.aboutYourLeaseOrTenure.IsGivenRentFreePeriodForm.isGivenRentFreePeriodForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartFour
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartFour.updateAboutLeaseOrAgreementPartFour
import models.submissions.common.AnswersYesNo
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.IsGivenRentFreePeriodId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.isGivenRentFreePeriod

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class IsGivenRentFreePeriodController @Inject() (
  isGivenRentFreePeriodView: isGivenRentFreePeriod,
  navigator: AboutYourLeaseOrTenureNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(
      isGivenRentFreePeriodView(
        leaseOrAgreementPartFour
          .flatMap(_.isGivenRentFreePeriod)
          .fold(isGivenRentFreePeriodForm)(isGivenRentFreePeriodForm.fill)
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      isGivenRentFreePeriodForm,
      formWithErrors => BadRequest(isGivenRentFreePeriodView(formWithErrors)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartFour(_.copy(isGivenRentFreePeriod = Some(data)))

        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(IsGivenRentFreePeriodId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def leaseOrAgreementPartFour(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutLeaseOrAgreementPartFour] = request.sessionData.aboutLeaseOrAgreementPartFour

}
