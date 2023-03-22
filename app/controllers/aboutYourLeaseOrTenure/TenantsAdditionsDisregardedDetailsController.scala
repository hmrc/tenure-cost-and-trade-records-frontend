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
import form.aboutYourLeaseOrTenure.TenantsAdditionsDisregardedDetailsForm.tenantsAdditionsDisregardedDetailsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.TenantsAdditionsDisregardedDetailsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutYourLeaseOrTenure.tenantsAdditionsDisregardedDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class TenantsAdditionsDisregardedDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  tenantsAdditionsDisregardedDetailsView: tenantsAdditionsDisregardedDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      tenantsAdditionsDisregardedDetailsView(
        request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.tenantsAdditionsDisregardedDetails) match {
          case Some(data) => tenantsAdditionsDisregardedDetailsForm.fillAndValidate(data)
          case _          => tenantsAdditionsDisregardedDetailsForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    tenantsAdditionsDisregardedDetailsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(tenantsAdditionsDisregardedDetailsView(formWithErrors))),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(tenantsAdditionsDisregardedDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(TenantsAdditionsDisregardedDetailsId).apply(updatedData)))
        }
      )
  }
}
