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
import form.aboutYourLeaseOrTenure.ServicePaidSeparatelyListForm.addServicePaidSeparatelyForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.ServicePaidSeparatelyListId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.servicePaidSeparatelyList
import views.html.genericRemoveConfirmation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class ServicePaidSeparatelyListController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  view: servicePaidSeparatelyList,
  genericRemoveConfirmationView: genericRemoveConfirmation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection =
      request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.servicesPaid.lift(index))

    Future.successful(
      Ok(
        view(
          existingSection.flatMap(_.addAnotherPaidService) match {
            case Some(answer) => addServicePaidSeparatelyForm.fill(answer)
            case _            => addServicePaidSeparatelyForm
          },
          index,
          toServicePaidSeparately(index).url,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      addServicePaidSeparatelyForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            index,
            toServicePaidSeparately(index).url,
            request.sessionData.toSummary
          )
        ),
      data =>
        request.sessionData.aboutLeaseOrAgreementPartThree
          .map(_.servicesPaid)
          .filter(_.nonEmpty)
          .fold(
            Redirect(toServicePaidSeparately(index))
          ) { existingServices =>
            val updatedServices = existingServices.updated(
              index,
              existingServices(index).copy(addAnotherPaidService = Some(data))
            )
            val updatedData     = updateAboutLeaseOrAgreementPartThree(
              _.copy(
                servicesPaid = updatedServices
              )
            )
            session.saveOrUpdate(updatedData)
            Redirect(navigator.nextPage(ServicePaidSeparatelyListId, updatedData).apply(updatedData))
          }
    )
  }

  def remove(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutLeaseOrAgreementPartThree
      .flatMap(_.servicesPaid.lift(index))
      .map { servicesPaid =>
        val service = servicesPaid.details.description
        Future.successful(
          Ok(
            genericRemoveConfirmationView(
              confirmableActionForm,
              service,
              "label.section.aboutYourLeaseOrTenure",
              request.sessionData.toSummary,
              index,
              controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.performRemove(index),
              toServicePaidSeparatelyList(index)
            )
          )
        )
      }
      .getOrElse(Redirect(toServicePaidSeparatelyList(0)))
  }

  def performRemove(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        request.sessionData.aboutLeaseOrAgreementPartThree
          .flatMap(_.tradeServices.lift(index))
          .map { services =>
            val description = services.details.description
            Future.successful(
              BadRequest(
                genericRemoveConfirmationView(
                  formWithErrors,
                  description,
                  "label.section.connectionToTheProperty",
                  request.sessionData.toSummary,
                  index,
                  controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.performRemove(index),
                  toServicePaidSeparatelyList(index)
                )
              )
            )
          }
          .getOrElse(Redirect(toServicePaidSeparatelyList(0))),
      {
        case AnswerYes =>
          request.sessionData.aboutLeaseOrAgreementPartThree.map(_.servicesPaid).map { servicesPaid =>
            val updatedServices = servicesPaid.patch(index, Nil, 1)
            session.saveOrUpdate(
              updateAboutLeaseOrAgreementPartThree(
                _.copy(servicesPaidIndex = 0, servicesPaid = updatedServices)
              )
            )
          }
          Redirect(toServicePaidSeparatelyList(0))
        case AnswerNo  =>
          Redirect(toServicePaidSeparatelyList(index))
      }
    )
  }

  private def toServicePaidSeparately(index: Int): Call =
    controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show(Some(index))

  private def toServicePaidSeparatelyList(index: Int): Call =
    controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.show(index)
}
