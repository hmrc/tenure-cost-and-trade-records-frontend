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

package controllers.aboutYourLeaseOrTenure

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.UltimatelyResponsibleForm.ultimatelyResponsibleForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.UltimatelyResponsible
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.UltimatelyResponsiblePageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.ultimatelyResponsible

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class UltimatelyResponsibleController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  ultimatelyResponsibleView: ultimatelyResponsible,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        ultimatelyResponsibleView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.ultimatelyResponsible) match {
            case Some(ultimatelyResponsible) => ultimatelyResponsibleForm.fillAndValidate(ultimatelyResponsible)
            case _                           => ultimatelyResponsibleForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[UltimatelyResponsible](
      ultimatelyResponsibleForm,
      formWithErrors => BadRequest(ultimatelyResponsibleView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(ultimatelyResponsible = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(UltimatelyResponsiblePageId, updatedData).apply(updatedData))
      }
    )
  }

}
