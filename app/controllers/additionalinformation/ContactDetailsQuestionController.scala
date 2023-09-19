
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
import form.additionalinformation.ContactDetailsQuestionForm.contactDetailsQuestionForm
import models.submissions.additionalinformation.AdditionalInformation.updateAdditionalInformation
import models.submissions.additionalinformation.ContactDetailsQuestion
import models.submissions.common.AnswersYesNo
import navigation.AdditionalInformationNavigator
import navigation.identifiers.ContactDetailsQuestionId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.additionalinformation.contactDetailsQuestion

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class ContactDetailsQuestionController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AdditionalInformationNavigator,
  contactDetailsQuestionView: contactDetailsQuestion,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        contactDetailsQuestionView(
          request.sessionData.additionalInformation.flatMap(_.altDetailsQuestion) match {
            case Some(altDetailsQuestion) =>
              contactDetailsQuestionForm.fillAndValidate(altDetailsQuestion)
            case _                        => contactDetailsQuestionForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ContactDetailsQuestion](
      contactDetailsQuestionForm,
      formWithErrors => BadRequest(contactDetailsQuestionView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAdditionalInformation(_.copy(altDetailsQuestion = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(ContactDetailsQuestionId, updatedData).apply(updatedData))
      }
    )
  }

}
