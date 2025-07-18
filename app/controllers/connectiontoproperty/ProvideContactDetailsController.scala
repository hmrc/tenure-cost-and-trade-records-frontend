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

package controllers.connectiontoproperty

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.connectiontoproperty.ProvideContactDetailsForm.provideContactDetailsForm
import models.submissions.common.AnswersYesNo.*
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.YourContactDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.ProvideYourContactDetailsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.provideContactDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProvideContactDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  provideContactDetailsView: provideContactDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("ProvideContactDetails")

    Future.successful(
      Ok(
        provideContactDetailsView(
          request.sessionData.stillConnectedDetails.flatMap(_.provideContactDetails) match {
            case Some(customerDetails) => provideContactDetailsForm.fill(customerDetails)
            case _                     => provideContactDetailsForm
          },
          getBackLink,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[YourContactDetails](
      provideContactDetailsForm,
      formWithErrors =>
        BadRequest(
          provideContactDetailsView(
            formWithErrors,
            getBackLink,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(provideContactDetails = Some(data)))
        session.saveOrUpdate(updatedData).map { _ =>
          val redirectToCYA = navigator.cyaPageVacant.filter(_ => navigator.from(using request) == "CYA")
          val nextPage      =
            redirectToCYA.getOrElse(navigator.nextPage(ProvideYourContactDetailsPageId, updatedData).apply(updatedData))
          Redirect(nextPage)
        }
      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        navigator.cyaPageDependsOnSession(request.sessionData).map(_.url).getOrElse("")
      case _     =>
        request.sessionData.stillConnectedDetails.flatMap(_.isAnyRentReceived) match {
          case Some(AnswerYes) =>
            request.sessionData.stillConnectedDetails.get.lettingPartOfPropertyDetails.isEmpty match {
              case true  =>
                controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0).url
              case false =>
                controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController
                  .show(request.sessionData.stillConnectedDetails.get.lettingPartOfPropertyDetailsIndex)
                  .url
            }
          case Some(AnswerNo)  => controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
          case _               => s"Unknown connection to property back link"
        }
    }
}
