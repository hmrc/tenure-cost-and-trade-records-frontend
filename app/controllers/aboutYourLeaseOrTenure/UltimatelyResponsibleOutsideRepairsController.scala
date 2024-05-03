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

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.UltimatelyResponsibleOutsideRepairsForm.ultimatelyResponsibleOutsideRepairsForm
import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.UltimatelyResponsibleOutsideRepairs
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.UltimatelyResponsibleOutsideRepairsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.ultimatelyResponsibleOutsideRepairs

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class UltimatelyResponsibleOutsideRepairsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  ultimatelyResponsibleORView: ultimatelyResponsibleOutsideRepairs,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        ultimatelyResponsibleORView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.ultimatelyResponsibleOutsideRepairs) match {
            case Some(ultimatelyResponsible) => ultimatelyResponsibleOutsideRepairsForm.fill(ultimatelyResponsible)
            case _                           => ultimatelyResponsibleOutsideRepairsForm
          },
          getBackLink
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[UltimatelyResponsibleOutsideRepairs](
      ultimatelyResponsibleOutsideRepairsForm,
      formWithErrors => BadRequest(ultimatelyResponsibleORView(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(ultimatelyResponsibleOutsideRepairs = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(UltimatelyResponsibleOutsideRepairsPageId, updatedData).apply(updatedData))
      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    if (
      request.sessionData.forType == ForTypes.for6020 &&
      request.sessionData.aboutLeaseOrAgreementPartOne
        .flatMap(_.includedInYourRentDetails)
        .exists(_.includedInYourRent contains "vat")
    ) {
      controllers.aboutYourLeaseOrTenure.routes.IsVATPayableForWholePropertyController.show().url
    } else {
      controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController.show().url
    }

}
