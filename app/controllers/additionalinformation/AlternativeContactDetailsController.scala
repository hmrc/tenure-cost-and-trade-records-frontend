/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.additionalinformation

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.additionalinformation.AlternativeContactDetailsForm.alternativeContactDetailsForm
import models.submissions.additionalinformation.AdditionalInformation.updateAdditionalInformation
import models.submissions.additionalinformation.AlternativeContactDetails
import navigation.AdditionalInformationNavigator
import navigation.identifiers.AlternativeContactDetailsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.additionalinformation.alternativeContactDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AlternativeContactDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AdditionalInformationNavigator,
  alternativeContactDetailsView: alternativeContactDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        alternativeContactDetailsView(
          request.sessionData.additionalInformation.flatMap(_.altContactInformation) match {
            case Some(altContactInformation) => alternativeContactDetailsForm.fill(altContactInformation)
            case _                           => alternativeContactDetailsForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AlternativeContactDetails](
      alternativeContactDetailsForm,
      formWithErrors => BadRequest(alternativeContactDetailsView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAdditionalInformation(_.copy(altContactInformation = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(AlternativeContactDetailsId, updatedData).apply(updatedData))
      }
    )
  }

}
