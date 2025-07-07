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
import form.aboutYourLeaseOrTenure.BenefitsGivenForm.benefitsGivenForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.common.AnswersYesNo
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.BenefitsGivenId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.benefitsGiven

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class BenefitsGivenController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  view: benefitsGiven,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("BenefitsGiven")

    Ok(
      view(
        request.sessionData.aboutLeaseOrAgreementPartThree.flatMap(_.benefitsGiven) match {
          case Some(data) => benefitsGivenForm.fill(data)
          case _          => benefitsGivenForm
        },
        request.sessionData.toSummary
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      benefitsGivenForm,
      formWithErrors => BadRequest(view(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartThree(_.copy(benefitsGiven = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(BenefitsGivenId, updatedData).apply(updatedData)))

      }
    )
  }

}
