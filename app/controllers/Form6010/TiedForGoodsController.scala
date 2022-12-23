/*
 * Copyright 2022 HM Revenue & Customs
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
import form.Form6010.AboutYourTradingHistoryForm.aboutYourTradingHistoryForm
import form.Form6010.TiedForGoodsDetailsForm.tiedForGoodsDetailsForm
import form.Form6010.TiedForGoodsForm.tiedForGoodsForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{aboutYourTradingHistory, tiedForGoods, tiedForGoodsDetails}
import models.submissions.Form6010.{TiedGoodsNo, TiedGoodsYes}
import models.submissions.SectionTwo.updateSectionTwo
import play.api.i18n.I18nSupport
import repositories.SessionRepo
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class TiedForGoodsController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  tiedForGoodsView: tiedForGoods,
  tiedForGoodsDetailsView: tiedForGoodsDetails,
  aboutYourTradingHistoryView: aboutYourTradingHistory,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(Ok(tiedForGoodsView(
      request.sessionData.sectionTwo.flatMap(_.tiedForGoods) match {
        case Some(tiedForGoods) => tiedForGoodsForm.fillAndValidate(tiedForGoods)
        case _ => tiedForGoodsForm
      }
      )))
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    tiedForGoodsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(tiedForGoodsView(formWithErrors))),
        {
          case data@TiedGoodsYes =>
            session.saveOrUpdate(updateSectionTwo(_.copy(tiedForGoods = Some(data))))
            Future.successful(Ok(tiedForGoodsDetailsView(tiedForGoodsDetailsForm)))
          case data@TiedGoodsNo =>
            session.saveOrUpdate(updateSectionTwo(_.copy(tiedForGoods = Some(data))))
            Future.successful(Ok(aboutYourTradingHistoryView(aboutYourTradingHistoryForm)))
          case _ => Future.successful(Ok(login(loginForm)))
        }
      )
  }

}
