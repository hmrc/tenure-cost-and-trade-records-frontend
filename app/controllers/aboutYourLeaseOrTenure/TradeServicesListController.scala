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
import form.aboutYourLeaseOrTenure.TradeServicesListForm.addAnotherServiceForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.TradeServicesListId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.tradeServicesList
import views.html.genericRemoveConfirmation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class TradeServicesListController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  tradeServicesListView: tradeServicesList,
  genericRemoveConfirmationView: genericRemoveConfirmation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection =
      request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.tradeServices.lift(index))

    Future.successful(
      Ok(
        tradeServicesListView(
          existingSection.flatMap(_.addAnotherService) match {
            case Some(answer) => addAnotherServiceForm.fill(answer)
            case _            => addAnotherServiceForm
          },
          index,
          controllers.aboutYourLeaseOrTenure.routes.TradeServicesDescriptionController.show(Some(index)).url,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      addAnotherServiceForm,
      formWithErrors =>
        BadRequest(
          tradeServicesListView(
            formWithErrors,
            index,
            controllers.aboutYourLeaseOrTenure.routes.TradeServicesDescriptionController.show(Some(index)).url,
            request.sessionData.toSummary
          )
        ),
      data =>
        request.sessionData.aboutLeaseOrAgreementPartThree
          .map(_.tradeServices)
          .filter(_.nonEmpty)
          .fold(
            Future.successful(
              Redirect(
                if (data == AnswerYes) {
                  routes.TradeServicesDescriptionController.show(Some(index))
                } else {
                  navigator.nextPage(TradeServicesListId, request.sessionData).apply(request.sessionData)
                }
              )
            )
          ) { existingSections =>
            val updatedSections = existingSections.updated(
              index,
              existingSections(index).copy(addAnotherService = Some(data))
            )
            val updatedData     = updateAboutLeaseOrAgreementPartThree(
              _.copy(
                tradeServices = updatedSections
              )
            )
            session.saveOrUpdate(updatedData)
            Redirect(navigator.nextPage(TradeServicesListId, updatedData).apply(updatedData))
          }
    )
  }

  def remove(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutLeaseOrAgreementPartThree
      .flatMap(_.tradeServices.lift(index))
      .map { services =>
        val service = services.details.description
        Future.successful(
          Ok(
            genericRemoveConfirmationView(
              confirmableActionForm,
              service,
              "label.section.aboutYourLeaseOrTenure",
              request.sessionData.toSummary,
              index,
              controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.performRemove(index),
              controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(index)
            )
          )
        )
      }
      .getOrElse(Redirect(controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(0)))
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
                  controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.performRemove(index),
                  controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(index)
                )
              )
            )
          }
          .getOrElse(Redirect(controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(0))),
      {
        case AnswerYes =>
          request.sessionData.aboutLeaseOrAgreementPartThree.map(_.tradeServices).map { services =>
            val updatedSections = services.patch(index, Nil, 1)
            session.saveOrUpdate(
              updateAboutLeaseOrAgreementPartThree(
                _.copy(tradeServicesIndex = 0, tradeServices = updatedSections)
              )
            )
          }
          Redirect(controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(0))
        case AnswerNo  =>
          Redirect(controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(index))
      }
    )
  }
}
