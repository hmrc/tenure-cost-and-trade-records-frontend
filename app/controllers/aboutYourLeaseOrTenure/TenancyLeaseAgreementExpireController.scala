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
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.TenancyLeaseAgreementExpireForm.tenancyLeaseAgreementExpireForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.TenancyLeaseAgreementExpirePageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.tenancyLeaseAgreementExpire

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TenancyLeaseAgreementExpireController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  tenancyLeaseAgreementExpireView: tenancyLeaseAgreementExpire,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("TenancyLeaseAgreementExpire")

    Future.successful(
      Ok(
        tenancyLeaseAgreementExpireView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.tenancyLeaseAgreementExpire) match {
            case Some(tenancyLeaseAgreementExpire) =>
              tenancyLeaseAgreementExpireForm.fill(tenancyLeaseAgreementExpire)
            case _                                 => tenancyLeaseAgreementExpireForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LocalDate](
      tenancyLeaseAgreementExpireForm,
      formWithErrors => BadRequest(tenancyLeaseAgreementExpireView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(tenancyLeaseAgreementExpire = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ =>
            Redirect(
              navigator.nextPage(TenancyLeaseAgreementExpirePageId, request.sessionData).apply(request.sessionData)
            )
          )

      }
    )
  }

}
