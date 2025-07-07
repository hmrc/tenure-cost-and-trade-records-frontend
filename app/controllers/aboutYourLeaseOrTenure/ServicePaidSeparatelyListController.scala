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

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.ServicePaidSeparatelyListForm.addServicePaidSeparatelyForm
import form.confirmableActionForm.confirmableActionForm
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.ServicePaidSeparatelyListId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.servicePaidSeparatelyList
import views.html.genericRemoveConfirmation as RemoveConfirmationView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class ServicePaidSeparatelyListController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  theListView: servicePaidSeparatelyList,
  theConfirmationView: RemoveConfirmationView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection =
      request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.servicesPaid.lift(index))

    audit.sendChangeLink("ServicePaidSeparatelyList")

    Future.successful(
      Ok(
        theListView(
          existingSection.flatMap(_.addAnotherPaidService) match {
            case Some(answer) => addServicePaidSeparatelyForm.fill(answer)
            case _            => addServicePaidSeparatelyForm
          },
          index
        )
      )
    )
  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      addServicePaidSeparatelyForm,
      formWithErrors =>
        BadRequest(
          theListView(
            formWithErrors,
            index
          )
        ),
      formData =>
        request.sessionData.aboutLeaseOrAgreementPartThree
          .map(_.servicesPaid)
          .filter(_.nonEmpty)
          .fold {
            formData match {
              case AnswerYes =>
                Redirect(controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show())
              case AnswerNo  =>
                request.sessionData.forType match {
                  case FOR6020 =>
                    Redirect(controllers.aboutYourLeaseOrTenure.routes.DoesRentIncludeParkingController.show())
                  case _       =>
                    Redirect(controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show())
                }
            }
          } { existingServices =>
            val updatedServices = existingServices.updated(
              index,
              existingServices(index).copy(addAnotherPaidService = Some(formData))
            )
            val updatedData     = updateAboutLeaseOrAgreementPartThree(
              _.copy(
                servicesPaid = updatedServices
              )
            )
            repository.saveOrUpdate(updatedData)
            Redirect(navigator.nextPage(ServicePaidSeparatelyListId, updatedData).apply(updatedData))
          }
    )
  }

  def remove(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutLeaseOrAgreementPartThree
      .flatMap(_.servicesPaid.lift(index))
      .map { servicesPaid =>
        val service = servicesPaid.details
        Future.successful(
          Ok(
            theConfirmationView(
              confirmableActionForm,
              service,
              controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.performRemove(index),
              navigator.callBackToCYAor(toServicePaidSeparatelyList(index))
            )
          )
        )
      }
      .getOrElse(navigator.redirectBackToCYAor(toServicePaidSeparatelyList(0)))
  }

  def performRemove(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        request.sessionData.aboutLeaseOrAgreementPartThree
          .flatMap(_.tradeServices.lift(index))
          .map { services =>
            val description = services.details
            Future.successful(
              BadRequest(
                theConfirmationView(
                  formWithErrors,
                  description,
                  controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.performRemove(index),
                  navigator.callBackToCYAor(toServicePaidSeparatelyList(index))
                )
              )
            )
          }
          .getOrElse(navigator.redirectBackToCYAor(toServicePaidSeparatelyList(0))),
      {
        case AnswerYes =>
          request.sessionData.aboutLeaseOrAgreementPartThree.map(_.servicesPaid).map { servicesPaid =>
            val updatedServices = servicesPaid.patch(index, Nil, 1)
            repository.saveOrUpdate(
              updateAboutLeaseOrAgreementPartThree(
                _.copy(servicesPaidIndex = 0, servicesPaid = updatedServices)
              )
            )
          }
          navigator.redirectBackToCYAor(toServicePaidSeparatelyList(0))
        case AnswerNo  =>
          navigator.redirectBackToCYAor(toServicePaidSeparatelyList(index))
      }
    )
  }

  private def toServicePaidSeparatelyList(index: Int): Call =
    controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.show(index)
}
