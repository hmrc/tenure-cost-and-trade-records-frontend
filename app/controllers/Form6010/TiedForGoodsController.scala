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

import controllers.LoginController.loginForm
import form.Form6010.TiedForGoodsDetailsForm.tiedForGoodsDetailsForm
import form.Form6010.AboutYourTradingHistoryForm.aboutYourTradingHistoryForm
import form.Form6010.TiedForGoodsForm.tiedForGoodsForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.Form6010.{aboutYourTradingHistory, tiedForGoods, tiedForGoodsDetails}
import models.submissions.Form6010.{TiedGoodsNo, TiedGoodsYes}
import views.html.login

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class TiedForGoodsController @Inject() (
                                         mcc: MessagesControllerComponents,
                                         login: login,
                                         tiedForGoodsView: tiedForGoods,
                                         tiedForGoodsDetailsView: tiedForGoodsDetails,
                                         aboutYourTradingHistoryView: aboutYourTradingHistory
                                       )
    extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(tiedForGoodsView(tiedForGoodsForm)))
  }

  def submit = Action.async { implicit request =>
    tiedForGoodsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(tiedForGoodsView(formWithErrors))),
        data =>
          if (data.tiedGoodsDetails.equals(TiedGoodsYes)) {
            Future.successful(Ok(tiedForGoodsDetailsView(tiedForGoodsDetailsForm)))
          } else if (data.tiedGoodsDetails.equals(TiedGoodsNo)) {
            Future.successful(Ok(aboutYourTradingHistoryView(aboutYourTradingHistoryForm)))
          } else {
            Future.successful(Ok(login(loginForm)))
          }
      )
  }

}
