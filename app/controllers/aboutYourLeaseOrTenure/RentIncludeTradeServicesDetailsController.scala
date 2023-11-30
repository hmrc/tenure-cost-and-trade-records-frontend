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
import form.aboutYourLeaseOrTenure.RentIncludeTradeServicesDetailsForm.rentIncludeTradeServicesDetailsForm
import navigation.AboutYourLeaseOrTenureNavigator
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.RentIncludeTradeServicesInformationDetails
import navigation.identifiers.RentIncludeTradeServicesDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.rentIncludeTradeServicesDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentIncludeTradeServicesDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentIncludeTradeServicesDetailsView: rentIncludeTradeServicesDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentIncludeTradeServicesDetailsView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.rentIncludeTradeServicesInformation) match {
            case Some(rentIncludeTradeServicesInformation) =>
              rentIncludeTradeServicesDetailsForm().fillAndValidate(rentIncludeTradeServicesInformation)
            case _                                         => rentIncludeTradeServicesDetailsForm()
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    val annualRent = request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.annualRent.map(_.amount))
    continueOrSaveAsDraft[RentIncludeTradeServicesInformationDetails](
      rentIncludeTradeServicesDetailsForm(annualRent),
      formWithErrors => BadRequest(rentIncludeTradeServicesDetailsView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(rentIncludeTradeServicesInformation = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(RentIncludeTradeServicesDetailsPageId, updatedData).apply(updatedData))
      }
    )
  }

}
