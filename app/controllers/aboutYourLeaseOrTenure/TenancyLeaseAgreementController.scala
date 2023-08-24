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
import controllers.LoginController.loginForm
import form.aboutYourLeaseOrTenure.TenancyLeaseAgreementForm.tenancyLeaseAgreementForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.TenancyLeaseAgreementDetails
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.tenancyLeaseAgreement
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class TenancyLeaseAgreementController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  tenancyLeaseAgreementView: tenancyLeaseAgreement,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        tenancyLeaseAgreementView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.tenancyLeaseAgreementDetails) match {
            case Some(data) => tenancyLeaseAgreementForm.fillAndValidate(data)
            case _          => tenancyLeaseAgreementForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[TenancyLeaseAgreementDetails](
      tenancyLeaseAgreementForm,
      formWithErrors => BadRequest(tenancyLeaseAgreementView(formWithErrors)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(tenancyLeaseAgreementDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Ok(login(loginForm))
      }
    )
  }

}