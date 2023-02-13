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
import form.aboutYourLeaseOrTenure.CurrentAnnualRentForm.currentAnnualRentForm
import form.aboutYourLeaseOrTenure.CurrentRentPayableWithin12MonthsForm.currentRentPayableWithin12MonthsForm
import form.aboutYourLeaseOrTenure.LeaseOrAgreementYearsForm.leaseOrAgreementYearsForm
import models.submissions.aboutYourLeaseOrTenure.{AgreedReviewedAlteredThreeYearsNo, CommenceWithinThreeYearsNo, RentUnderReviewNegotiatedNo}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutYourLeaseOrTenure.currentAnnualRent
import views.html.aboutYourLeaseOrTenure.{currentRentPayableWithin12Months, leaseOrAgreementYears}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class LeaseOrAgreementYearsController @Inject() (
  mcc: MessagesControllerComponents,
  currentRentPayableWithin12MonthsView: currentRentPayableWithin12Months,
  currentAnnualRentView: currentAnnualRent,
  leaseOrAgreementYearsView: leaseOrAgreementYears,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(leaseOrAgreementYearsView(leaseOrAgreementYearsForm)))
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    leaseOrAgreementYearsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(leaseOrAgreementYearsView(formWithErrors))),
        data =>
          if (
            data.commenceWithinThreeYears.equals(CommenceWithinThreeYearsNo) && data.agreedReviewedAlteredThreeYears
              .equals(AgreedReviewedAlteredThreeYearsNo) && data.rentUnderReviewNegotiated
              .equals(RentUnderReviewNegotiatedNo)
          ) {
            Future.successful(Ok(currentRentPayableWithin12MonthsView(currentRentPayableWithin12MonthsForm)))
          } else {
            Future.successful(
              Ok(
                currentAnnualRentView(
                  currentAnnualRentForm,
                  controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show().url
                )
              )
            )
          }
      )
  }

}
