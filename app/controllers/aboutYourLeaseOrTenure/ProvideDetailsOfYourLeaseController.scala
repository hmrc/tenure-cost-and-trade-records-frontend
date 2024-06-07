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
import form.aboutYourLeaseOrTenure.ProvideDetailsOfYourLeaseForm.provideDetailsOfYourLeaseForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.ProvideDetailsOfYourLease
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.ProvideDetailsOfYourLeasePageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.provideDetailsOfYourLease

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class ProvideDetailsOfYourLeaseController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  provideDetailsOfYourLeaseView: provideDetailsOfYourLease,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        provideDetailsOfYourLeaseView(
          request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.provideDetailsOfYourLease) match {
            case Some(provideDetailsOfYourLease) =>
              provideDetailsOfYourLeaseForm.fill(provideDetailsOfYourLease)
            case _                               => provideDetailsOfYourLeaseForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ProvideDetailsOfYourLease](
      provideDetailsOfYourLeaseForm,
      formWithErrors => BadRequest(provideDetailsOfYourLeaseView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartThree(_.copy(provideDetailsOfYourLease = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(ProvideDetailsOfYourLeasePageId, updatedData).apply(updatedData))
      }
    )
  }

}
