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

package controllers.aboutYourLeaseOrTenure

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.PayACapitalSumForm.payACapitalSumForm
import models.Session
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.PayACapitalSumDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.PayCapitalSumId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.payACapitalSum

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class PayACapitalSumController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  payACapitalSumView: payACapitalSum,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        payACapitalSumView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.payACapitalSumDetails) match {
            case Some(data) => payACapitalSumForm.fillAndValidate(data)
            case _          => payACapitalSumForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PayACapitalSumDetails](
      payACapitalSumForm,
      formWithErrors =>
        BadRequest(payACapitalSumView(formWithErrors, getBackLink(request.sessionData), request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(payACapitalSumDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(PayCapitalSumId).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.aboutLeaseOrAgreementPartTwo.flatMap(
      _.tenantAdditionsDisregardedDetails.map(_.tenantAdditionalDisregarded.name)
    ) match {
      case Some("yes") =>
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController.show().url
      case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
      case _           =>
        logger.warn(s"Back link for pay capital sum page reached with unknown tenants additions disregarded value")
        controllers.routes.TaskListController.show().url
    }
}
