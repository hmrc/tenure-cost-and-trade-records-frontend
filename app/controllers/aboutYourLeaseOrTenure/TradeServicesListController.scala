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
import form.aboutYourLeaseOrTenure.TradeServicesListForm.theForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.TradeServicesListId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.tradeServicesList as TradeServicesListView
import views.html.genericRemoveConfirmation as RemoveConfirmationView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TradeServicesListController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  theListView: TradeServicesListView,
  theConfirmationView: RemoveConfirmationView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection =
      request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.tradeServices.lift(index))

    audit.sendChangeLink("TradeServicesList")

    Future.successful(
      Ok(
        theListView(
          existingSection.flatMap(_.addAnotherService) match {
            case Some(answer) => theForm.fill(answer)
            case _            => theForm
          },
          index
        )
      )
    )
  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      formWithErrors =>
        BadRequest(
          theListView(
            formWithErrors,
            index
          )
        ),
      formData =>
        request.sessionData.aboutLeaseOrAgreementPartThree
          .map(_.tradeServices)
          .filter(_.nonEmpty)
          .fold(
            Future.successful(
              Redirect(
                if (formData == AnswerYes) {
                  routes.TradeServicesDescriptionController.show(Some(index))
                } else {
                  navigator.nextPage(TradeServicesListId, request.sessionData).apply(request.sessionData)
                }
              )
            )
          ) { existingSections =>
            val updatedSections = existingSections.updated(
              index,
              existingSections(index).copy(addAnotherService = Some(formData))
            )
            val updatedData     = updateAboutLeaseOrAgreementPartThree(
              _.copy(
                tradeServices = updatedSections
              )
            )
            repository
              .saveOrUpdate(updatedData)
              .map(_ => Redirect(navigator.nextPage(TradeServicesListId, updatedData).apply(updatedData)))

          }
    )
  }

  def remove(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutLeaseOrAgreementPartThree
      .flatMap(_.tradeServices.lift(index))
      .map { services =>
        val service = services.details.description
        Future.successful(
          Ok(
            theConfirmationView(
              confirmableActionForm,
              service,
              controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.performRemove(index),
              navigator.callBackToCYAor(controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(index))
            )
          )
        )
      }
      .getOrElse(navigator.redirectBackToCYAor(controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(0)))
  }

  def performRemove(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        request.sessionData.aboutLeaseOrAgreementPartThree
          .flatMap(_.tradeServices.lift(index))
          .map { services =>
            val description = services.details.description
            Future.successful(
              BadRequest(
                theConfirmationView(
                  formWithErrors,
                  description,
                  controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.performRemove(index),
                  navigator.callBackToCYAor(controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(index))
                )
              )
            )
          }
          .getOrElse(navigator.redirectBackToCYAor(controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(0))),
      {
        case AnswerYes =>
          request.sessionData.aboutLeaseOrAgreementPartThree.map(_.tradeServices).map { services =>
            val updatedSections = services.patch(index, Nil, 1)
            repository.saveOrUpdate(
              updateAboutLeaseOrAgreementPartThree(
                _.copy(tradeServicesIndex = 0, tradeServices = updatedSections)
              )
            )
          }
          navigator.redirectBackToCYAor(controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(0))
        case AnswerNo  =>
          navigator.redirectBackToCYAor(
            controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController
              .show(request.sessionData.aboutLeaseOrAgreementPartThree.map(_.tradeServices.size - 1).getOrElse(0))
          )
      }
    )
  }
}
