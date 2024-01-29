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
import form.aboutYourLeaseOrTenure.RentIncludeFixtureAndFittingDetailsForm.rentIncludeFixtureAndFittingsDetailsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.RentIncludeFixturesOrFittingsInformationDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.RentFixtureAndFittingsDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.NumberUtil.zeroBigDecimal
import views.html.aboutYourLeaseOrTenure.rentIncludeFixtureAndFittingsDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RentIncludeFixtureAndFittingsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentIncludeFixtureAndFittingsDetailsView: rentIncludeFixtureAndFittingsDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        rentIncludeFixtureAndFittingsDetailsView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.rentIncludeFixtureAndFittingsDetails) match {
            case Some(rentIncludeFixtureAndFittingsDetails) =>
              rentIncludeFixtureAndFittingsDetailsForm().fill(rentIncludeFixtureAndFittingsDetails)
            case _                                          => rentIncludeFixtureAndFittingsDetailsForm()
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    val leaseOrAgreement1     = request.sessionData.aboutLeaseOrAgreementPartOne
    val annualRent            = leaseOrAgreement1.flatMap(_.annualRent.map(_.amount))
    val otherIncludedPartsSum = leaseOrAgreement1
      .flatMap(_.rentIncludeTradeServicesInformation.flatMap(_.sumIncludedInRent))
      .getOrElse(zeroBigDecimal)

    continueOrSaveAsDraft[RentIncludeFixturesOrFittingsInformationDetails](
      rentIncludeFixtureAndFittingsDetailsForm(annualRent, otherIncludedPartsSum),
      formWithErrors =>
        BadRequest(rentIncludeFixtureAndFittingsDetailsView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData =
          updateAboutLeaseOrAgreementPartOne(_.copy(rentIncludeFixtureAndFittingsDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(RentFixtureAndFittingsDetailsPageId, updatedData).apply(updatedData))
      }
    )
  }

}
