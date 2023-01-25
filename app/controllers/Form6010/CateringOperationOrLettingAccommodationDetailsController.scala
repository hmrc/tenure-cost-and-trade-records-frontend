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

package controllers.Form6010

import actions.WithSessionRefiner
import form.Form6010.CateringOperationOrLettingAccommodationForm.cateringOperationOrLettingAccommodationForm
import form.Form6010.CateringOperationOrLettingAccommodationRentForm.cateringOperationOrLettingAccommodationRentForm
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{cateringOperationOrLettingAccommodationDetails, cateringOperationOrLettingAccommodationRentDetails}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.CateringOperationOrLettingAccommodationSection

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CateringOperationOrLettingAccommodationDetailController @Inject() (
  mcc: MessagesControllerComponents,
  cateringOperationOrLettingAccommodationDetailsView: cateringOperationOrLettingAccommodationDetails,
  cateringOperationOrLettingAccommodationRentDetailsView: cateringOperationOrLettingAccommodationRentDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc) with I18nSupport{

  def show(index: Int): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(
      Ok(cateringOperationOrLettingAccommodationDetailsView(cateringOperationOrLettingAccommodationForm))
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    cateringOperationOrLettingAccommodationForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(cateringOperationOrLettingAccommodationDetailsView(formWithErrors))),
        data =>{
          val cateringOperationOrLettingAccommodationSection = request.sessionData.aboutFranchisesOrLettings.flatMap(_.cateringOperationOrLettingAccommodationSections)
          val section = cateringOperationOrLettingAccommodationSection.size match {
            case 0 => CateringOperationOrLettingAccommodationSection(1, data)
            case 1 => CateringOperationOrLettingAccommodationSection(2, data)
          }
        }
    updateAboutFranchisesOrLettings(_.copy(cateringOperationOrLettingAccommodationSections = ))
          Future.successful(
            Ok(cateringOperationOrLettingAccommodationRentDetailsView(cateringOperationOrLettingAccommodationRentForm))
          )
      )
  }

}
