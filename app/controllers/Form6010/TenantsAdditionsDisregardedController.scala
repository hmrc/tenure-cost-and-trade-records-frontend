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
import controllers.LoginController.loginForm
import form.Form6010.TenantsAdditionsDisregardedDetailsForm.tenantsAdditionsDisregardedDetailsForm
import form.Form6010.TenantsAdditionsDisregardedForm.tenantsAdditionsDisregardedForm
import form.Form6010.LegalOrPlanningRestrictionsForm.legalPlanningRestrictionsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.common.{AnswerNo, AnswerYes}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{legalOrPlanningRestrictions, tenantsAdditionsDisregarded, tenantsAdditionsDisregardedDetails}
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class TenantsAdditionsDisregardedController @Inject() (
  mcc: MessagesControllerComponents,
  tenantsAdditionsDisregardedDetailsView: tenantsAdditionsDisregardedDetails,
  tenantsAdditionsDisregardedView: tenantsAdditionsDisregarded,
  legalOrPlanningRestrictionsView: legalOrPlanningRestrictions,
  login: login,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      tenantsAdditionsDisregardedView(
        request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.tenantAdditionsDisregardedDetails) match {
          case Some(data) => tenantsAdditionsDisregardedForm.fillAndValidate(data)
          case _          => tenantsAdditionsDisregardedForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    tenantsAdditionsDisregardedForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(tenantsAdditionsDisregardedView(formWithErrors))),
        data =>
          data.tenantAdditionalDisregarded match {
            case AnswerYes =>
              val updatedData =
                updateAboutLeaseOrAgreementPartTwo(_.copy(tenantAdditionsDisregardedDetails = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(
                Ok(tenantsAdditionsDisregardedDetailsView(tenantsAdditionsDisregardedDetailsForm))
              )
            case AnswerNo  =>
              val updatedData =
                updateAboutLeaseOrAgreementPartTwo(_.copy(tenantAdditionsDisregardedDetails = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(Ok(legalOrPlanningRestrictionsView(legalPlanningRestrictionsForm)))
            case _         => Future.successful(Ok(login(loginForm)))

          }
      )
  }
}
