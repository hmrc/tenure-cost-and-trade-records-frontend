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

import actions.WithSessionRefiner
import connectors.Audit
import controllers.{FORDataCaptureController, aboutYourLeaseOrTenure}
import form.aboutYourLeaseOrTenure.CurrentRentFirstPaidForm.currentRentFirstPaidForm
import models.ForType.*
import models.Session
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.common.AnswersYesNo.*
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.CurrentRentFirstPaidPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.currentRentFirstPaid

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CurrentRentFirstPaidController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  currentRentFirstPaidView: currentRentFirstPaid,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("CurrentRentFirstPaid")

    Ok(
      currentRentFirstPaidView(
        request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.currentRentFirstPaid) match {
          case Some(currentRentFirstPaid) => currentRentFirstPaidForm.fill(currentRentFirstPaid)
          case _                          => currentRentFirstPaidForm
        },
        getBackLink(request.sessionData),
        request.sessionData.toSummary
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LocalDate](
      currentRentFirstPaidForm,
      formWithErrors =>
        BadRequest(
          currentRentFirstPaidView(formWithErrors, getBackLink(request.sessionData), request.sessionData.toSummary)
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(currentRentFirstPaid = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(CurrentRentFirstPaidPageId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.forType match {
      case FOR6011 => controllers.aboutYourLeaseOrTenure.routes.RentIncludesVatController.show().url
      case FOR6020 =>
        answers.aboutLeaseOrAgreementPartThree.flatMap(_.throughputAffectsRent).map(_.doesRentVaryToThroughput) match {
          case Some(AnswerYes) => aboutYourLeaseOrTenure.routes.ThroughputAffectsRentDetailsController.show().url
          case _               => aboutYourLeaseOrTenure.routes.ThroughputAffectsRentController.show().url
        }
      case _       => controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show().url
    }

}
