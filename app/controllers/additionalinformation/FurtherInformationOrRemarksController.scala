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
import connectors.Audit
import controllers.FORDataCaptureController
import form.additionalinformation.FurtherInformationOrRemarksForm.furtherInformationOrRemarksForm
import models.audit.ChangeLinkAudit
import models.submissions.additionalinformation.AdditionalInformation.updateAdditionalInformation
import models.submissions.additionalinformation.FurtherInformationOrRemarksDetails
import navigation.AdditionalInformationNavigator
import navigation.identifiers.FurtherInformationId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.additionalinformation.furtherInformationOrRemarks

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FurtherInformationOrRemarksController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AdditionalInformationNavigator,
  furtherInformationOrRemarksView: furtherInformationOrRemarks,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
      audit.sendChangeLink("FurtherInformationOrRemarks")

    Future.successful(
      Ok(
        furtherInformationOrRemarksView(
          request.sessionData.additionalInformation.flatMap(_.furtherInformationOrRemarksDetails) match {
            case Some(furtherInformationOrRemarksDetails) =>
              furtherInformationOrRemarksForm.fill(furtherInformationOrRemarksDetails)
            case _                                        => furtherInformationOrRemarksForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[FurtherInformationOrRemarksDetails](
      furtherInformationOrRemarksForm,
      formWithErrors => BadRequest(furtherInformationOrRemarksView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAdditionalInformation(_.copy(furtherInformationOrRemarksDetails = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ =>
            Redirect(
              navigator
                .nextPage(FurtherInformationId, updatedData)
                .apply(updatedData)
            )
          )
      }
    )
  }

}
