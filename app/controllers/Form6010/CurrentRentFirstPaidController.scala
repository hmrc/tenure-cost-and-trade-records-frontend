/*
 * Copyright 2022 HM Revenue & Customs
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
import form.Form6010.CurrentRentFirstPaidForm.currentRentFirstPaidForm
import form.Form6010.CurrentLeaseOrAgreementBeginForm.currentLeaseOrAgreementBeginForm
import form.Form6011.TenancyLeaseAgreementExpireForm.tenancyLeaseAgreementExpireForm
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{currentLeaseOrAgreementBegin, currentRentFirstPaid, tenancyLeaseAgreementExpire}
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CurrentRentFirstPaidController @Inject() (
  mcc: MessagesControllerComponents,
  currentLeaseOrAgreementBeginView: currentLeaseOrAgreementBegin,
  currentRentFirstPaidView: currentRentFirstPaid,
  tenancyLeaseAgreementExpireView: tenancyLeaseAgreementExpire,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = Action { implicit request =>
    Ok(currentRentFirstPaidView(currentRentFirstPaidForm))
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    val forNumberRequest = request.sessionData.userLoginDetails.forNumber

    currentRentFirstPaidForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(currentRentFirstPaidView(formWithErrors))),
        data =>
          if (forNumberRequest == "FOR6011") {
            Future.successful(Ok(tenancyLeaseAgreementExpireView(tenancyLeaseAgreementExpireForm)))
          } else {
            Future.successful(Ok(currentLeaseOrAgreementBeginView(currentLeaseOrAgreementBeginForm)))
          }
      )
  }
}
