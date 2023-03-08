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
import form.Form6010.RentIncludeFixtureAndFittingDetailsForm.rentIncludeFixtureAndFittingsDetailsForm
import form.Form6010.RentOpenMarketValueForm.rentOpenMarketValuesForm
import models.submissions.aboutLeaseOrAgreement.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{rentIncludeFixtureAndFittingsDetails, rentOpenMarketValue}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentIncludeFixtureAndFittingsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  rentOpenMarketValueView: rentOpenMarketValue,
  rentIncludeFixtureAndFittingsDetailsView: rentIncludeFixtureAndFittingsDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentIncludeFixtureAndFittingsDetailsView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.rentIncludeFixtureAndFittingsDetails) match {
            case Some(rentIncludeFixtureAndFittingsDetails) =>
              rentIncludeFixtureAndFittingsDetailsForm.fillAndValidate(rentIncludeFixtureAndFittingsDetails)
            case _                                          => rentIncludeFixtureAndFittingsDetailsForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    rentIncludeFixtureAndFittingsDetailsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(rentIncludeFixtureAndFittingsDetailsView(formWithErrors))),
        data => {
          val updatedData =
            updateAboutLeaseOrAgreementPartOne(_.copy(rentIncludeFixtureAndFittingsDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Ok(rentOpenMarketValueView(rentOpenMarketValuesForm)))
        }
      )
  }
}
