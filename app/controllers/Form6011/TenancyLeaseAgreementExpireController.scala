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

package controllers.Form6011

import actions.WithSessionRefiner
import form.Form6010.FurtherInformationOrRemarksForm.furtherInformationOrRemarksForm
import form.Form6011.TenancyLeaseAgreementExpireForm.tenancyLeaseAgreementExpireForm
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{furtherInformationOrRemarks, tenancyLeaseAgreementExpire}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TenancyLeaseAgreementExpireController @Inject() (
  mcc: MessagesControllerComponents,
  tenancyLeaseAgreementExpireView: tenancyLeaseAgreementExpire,
  withSessionRefiner: WithSessionRefiner,
  furtherInformationView: furtherInformationOrRemarks,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = Action { implicit request =>
    Ok(tenancyLeaseAgreementExpireView(tenancyLeaseAgreementExpireForm))
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    val forNumberRequest = request.sessionData.userLoginDetails.forNumber

    tenancyLeaseAgreementExpireForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(tenancyLeaseAgreementExpireView(formWithErrors))),
        data => Future.successful(Ok(furtherInformationView(furtherInformationOrRemarksForm)))
      )
  }
}
