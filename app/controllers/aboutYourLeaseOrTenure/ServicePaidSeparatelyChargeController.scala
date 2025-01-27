/*
 * Copyright 2024 HM Revenue & Customs
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
import controllers.{FORDataCaptureController, toOpt}
import form.aboutYourLeaseOrTenure.ServicePaidSeparatelyChargeForm.servicePaidSeparatelyChargeForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.ServicePaidSeparatelyCharge
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.ServicePaidSeparatelyChargeId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.servicePaidSeparatelyCharge

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ServicePaidSeparatelyChargeController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  view: servicePaidSeparatelyCharge,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent]   = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("ServicePaidSeparatelyCharge")

    val existingSection = request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.servicesPaid.lift(index))
    existingSection.fold(
      Redirect(controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show(None))
    ) { details =>
      Ok(
        view(
          details.annualCharge.fold(servicePaidSeparatelyChargeForm)(servicePaidSeparatelyChargeForm.fill),
          index,
          request.sessionData.toSummary
        )
      )
    }
  }
  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ServicePaidSeparatelyCharge](
      servicePaidSeparatelyChargeForm,
      formWithErrors =>
        Future.successful(
          BadRequest(
            view(
              formWithErrors,
              index,
              request.sessionData.toSummary
            )
          )
        ),
      data =>
        request.sessionData.aboutLeaseOrAgreementPartThree.fold(
          Future.successful(
            Redirect(controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show(index))
          )
        ) { details =>
          val existingSections = details.servicesPaid
          val updatedDetails   = existingSections
            .updated(index, existingSections(index).copy(annualCharge = Some(data)))
          val updatedData      = updateAboutLeaseOrAgreementPartThree(_.copy(servicesPaid = updatedDetails))
          session
            .saveOrUpdate(updatedData)
            .map(_ => Redirect(navigator.nextPage(ServicePaidSeparatelyChargeId, updatedData).apply(updatedData)))

        }
    )
  }

}
