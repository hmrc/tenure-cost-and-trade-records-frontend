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
import form.aboutYourLeaseOrTenure.MethodToFixCurrentRentForm.methodToFixCurrentRentForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo.updateAboutLeaseOrAgreementPartTwo
import models.submissions.aboutYourLeaseOrTenure.MethodToFixCurrentRent
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.MethodToFixCurrentRentsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.methodToFixCurrentRent

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MethodToFixCurrentRentController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  methodToFixCurrentRentView: methodToFixCurrentRent,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("MethodToFixCurrentRent")

    Future.successful(
      Ok(
        methodToFixCurrentRentView(
          request.sessionData.aboutLeaseOrAgreementPartTwo.flatMap(_.methodToFixCurrentRentDetails) match {
            case Some(data) => methodToFixCurrentRentForm.fill(data)
            case _          => methodToFixCurrentRentForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[MethodToFixCurrentRent](
      methodToFixCurrentRentForm,
      formWithErrors => BadRequest(methodToFixCurrentRentView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartTwo(_.copy(methodToFixCurrentRentDetails = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(MethodToFixCurrentRentsId, updatedData).apply(updatedData)))

      }
    )
  }

}
