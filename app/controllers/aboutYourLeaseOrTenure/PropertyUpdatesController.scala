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
import form.aboutYourLeaseOrTenure.PropertyUpdatesForm.propertyUpdatesForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.PropertyUpdates
import models.submissions.common.AnswersYesNo.*
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.PropertyUpdatesId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import play.api.Logging
import views.html.aboutYourLeaseOrTenure.propertyUpdates
import models.ForType.*
import models.Session
import controllers.toOpt

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PropertyUpdatesController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  view: propertyUpdates,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("PropertyUpdates")

    Future.successful(
      Ok(
        view(
          request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.propertyUpdates) match {
            case Some(data) => propertyUpdatesForm.fill(data)
            case _          => propertyUpdatesForm
          },
          request.sessionData.toSummary,
          backLink(request.sessionData)
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PropertyUpdates](
      propertyUpdatesForm,
      formWithErrors => BadRequest(view(formWithErrors, request.sessionData.toSummary, backLink(request.sessionData))),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartThree(_.copy(propertyUpdates = Some(data)))
        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(PropertyUpdatesId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def backLink(answers: Session): String =
    answers.forType match {
      case FOR6045 | FOR6046 =>
        answers.aboutLeaseOrAgreementPartTwo.flatMap(
          _.tenantAdditionsDisregardedDetails.flatMap(_.tenantAdditionalDisregarded)
        ) match {
          case Some(AnswerYes) =>
            controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController.show().url
          case _               => controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
        }
      case _                 => controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show().url
    }

}
