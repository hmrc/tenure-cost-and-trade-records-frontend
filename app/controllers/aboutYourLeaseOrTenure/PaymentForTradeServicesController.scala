/*
 * Copyright 2024 HM Revenue & Customs
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
import form.aboutYourLeaseOrTenure.PaymentForTradeServicesForm.paymentForTradeServicesForm
import models.{ForTypes, Session}
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.PaymentForTradeServices
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.PaymentForTradeServicesId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.paymentForTradeServices

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class PaymentForTradeServicesController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  paymentView: paymentForTradeServices,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        paymentView(
          request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.paymentForTradeServices) match {
            case Some(answers) => paymentForTradeServicesForm.fill(answers)
            case _             => paymentForTradeServicesForm
          },
          getBackLink(request.sessionData, navigator.from) match {
            case Right(link) => link
            case Left(msg)   =>
              logger.warn(s"Navigation for payment for trade services page reached with error: $msg")
              throw new RuntimeException(
                s"Navigation for payment for trade services page reached with error $msg"
              )
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PaymentForTradeServices](
      paymentForTradeServicesForm,
      formWithErrors =>
        BadRequest(
          paymentView(
            formWithErrors,
            getBackLink(request.sessionData, navigator.from) match {
              case Right(link) => link
              case Left(msg)   =>
                logger.warn(s"Navigation for payment for trade services page reached with error: $msg")
                throw new RuntimeException(
                  s"Navigation for payment for trade services page reached with error $msg"
                )
            },
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartThree(_.copy(paymentForTradeServices = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(PaymentForTradeServicesId, updatedData).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session, fromLocation: String): Either[String, String] =
    fromLocation match {
      case "TL" => Right(controllers.routes.TaskListController.show().url + "#payment-for-trade-services")
      case _    =>
        answers.forType match {
          case ForTypes.for6030 =>
            Right(getBackLinkOfrSections(answers))
          case _                =>
            Right(getBackLinkOfrSections(answers))
        }
    }

  private def getBackLinkOfrSections(answers: Session): String =
    answers.aboutLeaseOrAgreementPartThree.flatMap { aboutYourLeaseOrAgreement =>
      aboutYourLeaseOrAgreement.tradeServices.lastOption.map(_ => aboutYourLeaseOrAgreement.tradeServices.size - 1)
    } match {
      case Some(index) =>
        controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(index).url
      case None        =>
        answers.forType match {
          case ForTypes.for6030 =>
            controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
          case _                => controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
        }
    }

}
