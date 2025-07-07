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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.RentIncludeFixtureAndFittingDetailsForm.rentIncludeFixtureAndFittingsDetailsForm
import form.aboutYourLeaseOrTenure.RentIncludeFixtureAndFittingDetailsTextAreaForm.rentIncludeFixtureAndFittingsDetailsTextAreaForm
import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.RentFixtureAndFittingsDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.NumberUtil.zeroBigDecimal
import views.html.aboutYourLeaseOrTenure.{rentIncludeFixtureAndFittingsDetails, rentIncludeFixtureAndFittingsDetailsTextArea}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RentIncludeFixtureAndFittingsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  rentIncludeFixtureAndFittingsDetailsView: rentIncludeFixtureAndFittingsDetails,
  rentIncludeFixtureAndFittingsDetailsTextAreaView: rentIncludeFixtureAndFittingsDetailsTextArea,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  private def forType(implicit request: SessionRequest[?]): ForType = request.sessionData.forType

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("RentIncludeFixtureAndFittingsDetails")

    if (forType == FOR6045 || forType == FOR6046) {
      Future.successful(
        Ok(
          rentIncludeFixtureAndFittingsDetailsTextAreaView(
            request.sessionData.aboutLeaseOrAgreementPartThree
              .flatMap(_.rentIncludeFixtureAndFittingsDetailsTextArea) match {
              case Some(rentIncludeFixtureAndFittingsDetailsTextArea) =>
                rentIncludeFixtureAndFittingsDetailsTextAreaForm.fill(rentIncludeFixtureAndFittingsDetailsTextArea)
              case _                                                  => rentIncludeFixtureAndFittingsDetailsTextAreaForm
            },
            request.sessionData.toSummary
          )
        )
      )
    } else {
      Future.successful(
        Ok(
          rentIncludeFixtureAndFittingsDetailsView(
            rentIncludeFixtureAndFittingsDetailsForm().fill(
              request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.rentIncludeFixturesAndFittingsAmount)
            ),
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
      .flatMap(_.rentIncludeTradeServicesInformation.flatMap(_.sumIncludedInRent))
      .getOrElse(zeroBigDecimal)

    if (forType == FOR6045 || forType == FOR6046) {
      continueOrSaveAsDraft[String](
        rentIncludeFixtureAndFittingsDetailsTextAreaForm,
        formWithErrors =>
          BadRequest(rentIncludeFixtureAndFittingsDetailsTextAreaView(formWithErrors, request.sessionData.toSummary)),
        data => {
          val updatedData =
            updateAboutLeaseOrAgreementPartThree(_.copy(rentIncludeFixtureAndFittingsDetailsTextArea = Some(data)))
          session.saveOrUpdate(updatedData).map { _ =>
            Redirect(navigator.nextPage(RentFixtureAndFittingsDetailsPageId, updatedData).apply(updatedData))
          }
        }
      )
    } else {
      continueOrSaveAsDraft[Option[BigDecimal]](
        rentIncludeFixtureAndFittingsDetailsForm(annualRent, otherIncludedPartsSum),
        formWithErrors =>
          BadRequest(rentIncludeFixtureAndFittingsDetailsView(formWithErrors, request.sessionData.toSummary)),
        data => {
          val updatedData =
            updateAboutLeaseOrAgreementPartOne(_.copy(rentIncludeFixturesAndFittingsAmount = data))
          session.saveOrUpdate(updatedData).map { _ =>
            Redirect(navigator.nextPage(RentFixtureAndFittingsDetailsPageId, updatedData).apply(updatedData))
          }
        }
      )
    }
  }

}
