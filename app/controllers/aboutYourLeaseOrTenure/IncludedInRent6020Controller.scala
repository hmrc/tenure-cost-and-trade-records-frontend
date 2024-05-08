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
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.IncludedInRent6020Form.includedInRent6020Form
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, DoesTheRentPayable}
import models.submissions.common.AnswerYes
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.IncludedInRent6020Id
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.includedInRent6020

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class IncludedInRent6020Controller @Inject() (
  includedInRent6020View: includedInRent6020,
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
      includedInRent6020View(
        leaseOrAgreementPartOne
          .flatMap(_.doesTheRentPayable)
          .fold(includedInRent6020Form)(includedInRent6020Form.fill),
        getBackLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[DoesTheRentPayable](
      includedInRent6020Form,
      formWithErrors => BadRequest(includedInRent6020View(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(doesTheRentPayable = Some(data)))

        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(IncludedInRent6020Id, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def leaseOrAgreementPartOne(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutLeaseOrAgreementPartOne] = request.sessionData.aboutLeaseOrAgreementPartOne

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    if (
      request.sessionData.aboutLeaseOrAgreementPartOne
        .flatMap(_.rentIncludeFixturesAndFittingsDetails)
        .map(_.rentIncludeFixturesAndFittingsDetails)
        .contains(AnswerYes)
    ) {
      controllers.aboutYourLeaseOrTenure.routes.RentedEquipmentDetailsController.show().url
    } else {
      controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
    }

}
