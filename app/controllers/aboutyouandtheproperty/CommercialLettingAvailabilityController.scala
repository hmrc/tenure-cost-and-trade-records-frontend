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

package controllers.aboutyouandtheproperty

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.CommercialLettingAvailabilityForm.commercialLettingAvailabilityForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo.updateAboutYouAndThePropertyPartTwo
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.CommercialLettingAvailabilityId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.commercialLettingAvailability

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CommercialLettingAvailabilityController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  view: commercialLettingAvailability,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("CommercialLettingAvailability")

    Ok(
      view(
        request.sessionData.aboutYouAndThePropertyPartTwo.flatMap(_.commercialLetAvailability) match {
          case Some(data) => commercialLettingAvailabilityForm.fill(data)
          case _          => commercialLettingAvailabilityForm
        },
        calculateBackLink,
        request.sessionData.toSummary
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[Int](
      commercialLettingAvailabilityForm,
      formWithErrors => BadRequest(view(formWithErrors, calculateBackLink, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutYouAndThePropertyPartTwo(_.copy(commercialLetAvailability = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(CommercialLettingAvailabilityId, updatedData).apply(updatedData)))
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      case _     => controllers.aboutyouandtheproperty.routes.CommercialLettingQuestionController.show().url

    }
}
