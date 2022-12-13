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
import form.Form6010.AboutTheLandlordForm.aboutTheLandlordForm
import form.Form6010.LeaseOrAgreementYearsForm.leaseOrAgreementYearsForm
import form.Form6010.CurrentAnnualRentForm.currentAnnualRentForm
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.Form6010.{aboutYourLandlord, currentAnnualRent, leaseOrAgreementYears}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AboutYourLandlordController @Inject() (
  mcc: MessagesControllerComponents,
  leaseOrAgreementYearsView: leaseOrAgreementYears,
  aboutYourLandlordView: aboutYourLandlord,
  currentAnnualRentView: currentAnnualRent,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)  extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(aboutYourLandlordView(aboutTheLandlordForm)))
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>

    val forNumberRequest = request.sessionData.userLoginDetails.forNumber

    aboutTheLandlordForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(aboutYourLandlordView(formWithErrors))),
        data =>
          if (forNumberRequest == "FOR6011") {
            Future.successful(Ok(currentAnnualRentView(currentAnnualRentForm)))
          } else {
            Future.successful(Ok(leaseOrAgreementYearsView(leaseOrAgreementYearsForm)))
          }
      )
  }

}
