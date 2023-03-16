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
import views.html.form.incentivesPaymentsConditions
import form.Form6010.IncentivesPaymentsConditionsForm.incentivesPaymentsConditionsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.IncentivesPaymentsConditionsId
import play.api.i18n.I18nSupport
import repositories.SessionRepo

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class IncentivesPaymentsConditionsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  incentivesPaymentsConditionsView: incentivesPaymentsConditions,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        incentivesPaymentsConditionsView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.incentivesPaymentsConditionsDetails) match {
            case Some(data) => incentivesPaymentsConditionsForm.fillAndValidate(data)
            case _          => incentivesPaymentsConditionsForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    incentivesPaymentsConditionsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(incentivesPaymentsConditionsView(formWithErrors))),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(incentivesPaymentsConditionsDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(IncentivesPaymentsConditionsId).apply(updatedData)))
        }
      )
  }
}
