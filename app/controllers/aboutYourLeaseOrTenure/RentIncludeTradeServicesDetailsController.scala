/*
 * Copyright 2025 HM Revenue & Customs
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

import actions.SessionRequest
import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.RentIncludeTradeServicesDetailsForm.rentIncludeTradeServicesDetailsForm
import form.aboutYourLeaseOrTenure.RentIncludeTradeServicesDetailsTextAreaForm.rentIncludeTradeServicesDetailsTextAreaForm
import models.ForType
import models.ForType.*
import navigation.AboutYourLeaseOrTenureNavigator
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.RentIncludeTradeServicesInformationDetails
import navigation.identifiers.RentIncludeTradeServicesDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.NumberUtil.zeroBigDecimal
import views.html.aboutYourLeaseOrTenure.rentIncludeTradeServicesDetails
import views.html.aboutYourLeaseOrTenure.rentIncludeTradeServicesDetailsTextArea

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RentIncludeTradeServicesDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentIncludeTradeServicesDetailsView: rentIncludeTradeServicesDetails,
  rentIncludeTradeServicesDetailsTextAreaView: rentIncludeTradeServicesDetailsTextArea,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  private def forType(implicit request: SessionRequest[?]): ForType = request.sessionData.forType

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("RentIncludeTradeServicesDetails")

    if (forType == FOR6045 || forType == FOR6046) {
      Future.successful(
        Ok(
          rentIncludeTradeServicesDetailsTextAreaView(
            request.sessionData.aboutLeaseOrAgreementPartThree
              .flatMap(_.rentIncludeTradeServicesDetailsTextArea) match {
              case Some(rentIncludeTradeServicesDetailsTextArea) =>
                rentIncludeTradeServicesDetailsTextAreaForm.fill(rentIncludeTradeServicesDetailsTextArea)
              case _                                             => rentIncludeTradeServicesDetailsTextAreaForm
            }
          )
        )
      )
    } else {
      Future.successful(
        Ok(
          rentIncludeTradeServicesDetailsView(
            request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.rentIncludeTradeServicesInformation) match {
              case Some(rentIncludeTradeServicesInformation) =>
                rentIncludeTradeServicesDetailsForm().fill(rentIncludeTradeServicesInformation)
              case _                                         => rentIncludeTradeServicesDetailsForm()
            },
            request.sessionData.toSummary
          )
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val leaseOrAgreement1     = request.sessionData.aboutLeaseOrAgreementPartOne
    val annualRent            = leaseOrAgreement1.flatMap(_.annualRent)
    val otherIncludedPartsSum = leaseOrAgreement1
      .flatMap(_.rentIncludeFixturesAndFittingsAmount)
      .getOrElse(zeroBigDecimal)

    if (forType == FOR6045 || forType == FOR6046) {
      continueOrSaveAsDraft[String](
        rentIncludeTradeServicesDetailsTextAreaForm,
        formWithErrors =>
          BadRequest(
            rentIncludeTradeServicesDetailsTextAreaView(formWithErrors)
          ),
        data => {
          val updatedData =
            updateAboutLeaseOrAgreementPartThree(_.copy(rentIncludeTradeServicesDetailsTextArea = Some(data)))
          session.saveOrUpdate(updatedData).map { _ =>
            Redirect(navigator.nextPage(RentIncludeTradeServicesDetailsPageId, updatedData).apply(updatedData))
          }
        }
      )
    } else {
      continueOrSaveAsDraft[RentIncludeTradeServicesInformationDetails](
        rentIncludeTradeServicesDetailsForm(annualRent, otherIncludedPartsSum),
        formWithErrors =>
          BadRequest(rentIncludeTradeServicesDetailsView(formWithErrors, request.sessionData.toSummary)),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(rentIncludeTradeServicesInformation = Some(data)))
          session.saveOrUpdate(updatedData).map { _ =>
            Redirect(navigator.nextPage(RentIncludeTradeServicesDetailsPageId, updatedData).apply(updatedData))
          }
        }
      )
    }
  }
}
