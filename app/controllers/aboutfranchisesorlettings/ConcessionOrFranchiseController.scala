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

package controllers.aboutfranchisesorlettings

import actions.WithSessionRefiner
import form.aboutfranchisesorlettings.ConcessionOrFranchiseForm.concessionOrFranchiseForm
import models.Session
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import navigation.AboutThePropertyNavigator
import navigation.identifiers.TiedForGoodsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutfranchisesorlettings.concessionOrFranchise

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class ConcessionOrFranchiseController @Inject()(
  mcc: MessagesControllerComponents,
  navigator: AboutThePropertyNavigator,
  concessionOrFranchiseView: concessionOrFranchise,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        concessionOrFranchiseView(
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.concessionOrFranchise) match {
            case Some(concessionOrFranchise) => concessionOrFranchiseForm.fillAndValidate(concessionOrFranchise)
            case _                           => concessionOrFranchiseForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    concessionOrFranchiseForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(concessionOrFranchiseView(formWithErrors))),
        data => {
          val updatedData = updateAboutFranchisesOrLettings(_.copy(concessionOrFranchise = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(TiedForGoodsPageId).apply(updatedData)))
        }
      )
  }
}
