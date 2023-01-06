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

package controllers.Form6010

import actions.WithSessionRefiner
import controllers.LoginController.loginForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{aboutYourTradingHistory, enforcementActionBeenTaken, enforcementActionBeenTakenDetails, tiedForGoods}
import form.Form6010.EnforcementActionForm.enforcementActionForm
import form.Form6010.EnforcementActionDetailsForm.enforcementActionDetailsForm
import form.Form6010.TiedForGoodsForm.tiedForGoodsForm
import form.Form6010.AboutYourTradingHistoryForm.aboutYourTradingHistoryForm
import models.submissions.Form6010.{EnforcementActionsNo, EnforcementActionsYes}
import models.submissions.abouttheproperty.AboutTheProperty.updateAboutTheProperty
import play.api.i18n.I18nSupport
import repositories.SessionRepo
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnforcementActionBeenTakenController @Inject() (
  mcc: MessagesControllerComponents,
  enforcementActionBeenTakenDetailsView: enforcementActionBeenTakenDetails,
  tiedForGoodsView: tiedForGoods,
  login: login,
  enforcementActionBeenTakenView: enforcementActionBeenTaken,
  aboutYourTradingHistoryView: aboutYourTradingHistory,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(enforcementActionBeenTakenView(enforcementActionForm)))
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    enforcementActionForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(enforcementActionBeenTakenView(formWithErrors))),
        {
          case data @ EnforcementActionsYes =>
            session.saveOrUpdate(updateAboutTheProperty(_.copy(enforcementAction = Some(data))))
            Future.successful(Ok(enforcementActionBeenTakenDetailsView(enforcementActionDetailsForm)))
          case data @ EnforcementActionsNo  =>
            //TODO - this should map to a forType with a string name associated, not a string
            if (request.sessionData.userLoginDetails.forNumber == "FOR6011") {
              session.saveOrUpdate(updateAboutTheProperty(_.copy(enforcementAction = Some(data))))
              Future.successful(Ok(aboutYourTradingHistoryView(aboutYourTradingHistoryForm)))
            } else {
              session.saveOrUpdate(updateAboutTheProperty(_.copy(enforcementAction = Some(data))))
              Future.successful(Ok(tiedForGoodsView(tiedForGoodsForm)))
            }
          case _                            => Future.successful(Ok(login(loginForm)))
        }
      )
  }
}
