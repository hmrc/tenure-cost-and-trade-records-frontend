/*
 * Copyright 2025 HM Revenue & Customs
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
import form.aboutYourLeaseOrTenure.IsParkingRentPaidSeparatelyForm.isParkingRentPaidSeparatelyForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateCarParking
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.IsParkingRentPaidSeparatelyId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.isParkingRentPaidSeparately

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class IsParkingRentPaidSeparatelyController @Inject() (
  isParkingRentPaidSeparatelyView: isParkingRentPaidSeparately,
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
    audit.sendChangeLink("IsParkingRentPaidSeparately")

    Ok(
      isParkingRentPaidSeparatelyView(
        leaseOrAgreementPartThree
          .flatMap(_.carParking)
          .flatMap(_.isRentPaidSeparately)
          .fold(isParkingRentPaidSeparatelyForm)(isParkingRentPaidSeparatelyForm.fill),
        getBackLink,
        request.sessionData.toSummary
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      isParkingRentPaidSeparatelyForm,
      formWithErrors =>
        BadRequest(isParkingRentPaidSeparatelyView(formWithErrors, getBackLink, request.sessionData.toSummary)),
      data => {
        val updatedData = updateCarParking(_.copy(isRentPaidSeparately = Some(data)))

        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(IsParkingRentPaidSeparatelyId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def leaseOrAgreementPartThree(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutLeaseOrAgreementPartThree] = request.sessionData.aboutLeaseOrAgreementPartThree

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "TL" => controllers.routes.TaskListController.show().url
      case _    =>
        if (
          leaseOrAgreementPartThree.flatMap(_.carParking).flatMap(_.doesRentIncludeParkingOrGarage).contains(AnswerYes)
        ) {
          controllers.aboutYourLeaseOrTenure.routes.IncludedInRentParkingSpacesController.show().url
        } else {
          controllers.aboutYourLeaseOrTenure.routes.DoesRentIncludeParkingController.show().url
        }
    }

}
