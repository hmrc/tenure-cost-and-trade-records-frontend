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

package controllers.requestReferenceNumber

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.additionalinformation.CheckYourAnswersAdditionalInformationForm.checkYourAnswersAdditionalInformationForm
import form.requestReferenceNumber.CheckYourAnswersRequestReferenceNumberForm
import form.requestReferenceNumber.CheckYourAnswersRequestReferenceNumberForm.checkYourAnswersRequestReferenceNumberForm
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails.updateRequestReferenceNumber
import models.submissions.additionalinformation.CheckYourAnswersAdditionalInformation
import models.submissions.requestReferenceNumber.CheckYourAnswersRequestReferenceNumber
import navigation.AdditionalInformationNavigator
import navigation.identifiers.{CheckYourAnswersAdditionalInformationId, CheckYourAnswersRequestReferenceNumberPageId}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.additionalinformation.checkYourAnswersAdditionalInformation
import views.html.requestReferenceNumber.checkYourAnswersRequestReferenceNumber

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CheckYourAnswersRequestReferenceNumberController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AdditionalInformationNavigator,
  checkYourAnswersRequestReferenceNumberView: checkYourAnswersRequestReferenceNumber,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersRequestReferenceNumberView(
          request.sessionData.requestReferenceNumberDetails.flatMap(_.checkYourAnswersRequestReferenceNumber) match {
            case Some(checkYourAnswersRequestReferenceNumber) =>
              checkYourAnswersRequestReferenceNumberForm.fillAndValidate(checkYourAnswersRequestReferenceNumber)
            case _                                            => checkYourAnswersRequestReferenceNumberForm
          },
          request.sessionData
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    checkYourAnswersRequestReferenceNumberForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future
            .successful(BadRequest(checkYourAnswersRequestReferenceNumberView(formWithErrors, request.sessionData))),
        data => {
          val updatedData = updateRequestReferenceNumber(_.copy(checkYourAnswersRequestReferenceNumber = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(
            Redirect(navigator.nextPage(CheckYourAnswersRequestReferenceNumberPageId, updatedData).apply(updatedData))
          )
        }
      )
  }

}
