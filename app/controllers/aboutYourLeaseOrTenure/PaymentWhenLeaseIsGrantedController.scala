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
import form.aboutYourLeaseOrTenure.PaymentWhenLeaseIsGrantedForm.paymentWhenLeaseIsGrantedForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.PaymentWhenLeaseIsGrantedDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.PayWhenLeaseGrantedId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.paymentWhenLeaseIsGranted

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class PaymentWhenLeaseIsGrantedController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  paymentWhenLeaseIsGrantedView: paymentWhenLeaseIsGranted,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        paymentWhenLeaseIsGrantedView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.paymentWhenLeaseIsGrantedDetails) match {
            case Some(data) => paymentWhenLeaseIsGrantedForm.fillAndValidate(data)
            case _          => paymentWhenLeaseIsGrantedForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PaymentWhenLeaseIsGrantedDetails](
      paymentWhenLeaseIsGrantedForm,
      formWithErrors => BadRequest(paymentWhenLeaseIsGrantedView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(paymentWhenLeaseIsGrantedDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(PayWhenLeaseGrantedId).apply(updatedData))
      }
    )
  }

}
