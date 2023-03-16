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
import form.aboutYourLeaseOrTenure.CurrentLeaseOrAgreementBeginForm.currentLeaseOrAgreementBeginForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.CurrentLeaseBeginPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutYourLeaseOrTenure.currentLeaseOrAgreementBegin

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CurrentLeaseOrAgreementBeginController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  currentLeaseOrAgreementBeginView: currentLeaseOrAgreementBegin,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      currentLeaseOrAgreementBeginView(
        request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.currentLeaseOrAgreementBegin) match {
          case Some(currentLeaseOrAgreementBegin) =>
            currentLeaseOrAgreementBeginForm.fillAndValidate(currentLeaseOrAgreementBegin)
          case _                                  => currentLeaseOrAgreementBeginForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    currentLeaseOrAgreementBeginForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(currentLeaseOrAgreementBeginView(formWithErrors))),
        data => {
          val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(currentLeaseOrAgreementBegin = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(CurrentLeaseBeginPageId).apply(updatedData)))
        }
      )
  }

}
