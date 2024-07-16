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

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.IncludedInYourRentForm.includedInYourRentForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.IncludedInYourRentDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.IncludedInYourRentPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.includedInYourRent

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class IncludedInYourRentController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  includedInYourRentView: includedInYourRent,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  private def forType(implicit request: SessionRequest[?]): String = request.sessionData.forType

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        includedInYourRentView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.includedInYourRentDetails) match {
            case Some(includedInYourRentDetails) => includedInYourRentForm(forType).fill(includedInYourRentDetails)
            case _                               => includedInYourRentForm(forType)
          },
          request.sessionData.toSummary,
          request.sessionData.forType,
          navigator.from
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[IncludedInYourRentDetails](
      includedInYourRentForm(forType),
      formWithErrors =>
        BadRequest(
          includedInYourRentView(
            formWithErrors,
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(includedInYourRentDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(IncludedInYourRentPageId, updatedData).apply(updatedData))
      }
    )
  }

}
