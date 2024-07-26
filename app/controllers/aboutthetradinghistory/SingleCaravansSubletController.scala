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

package controllers.aboutthetradinghistory

import actions.WithSessionRefiner
import controllers.aboutthetradinghistory
import models.submissions.aboutthetradinghistory.Caravans.CaravansTradingPage
import models.submissions.aboutthetradinghistory.Caravans.CaravansTradingPage.SingleCaravansSublet
import models.submissions.aboutthetradinghistory.{Caravans, CaravansTrading6045, TurnoverSection6045}
import navigation.AboutTheTradingHistoryNavigator
import play.api.mvc.MessagesControllerComponents
import repositories.SessionRepo
import views.html.aboutthetradinghistory.caravansTrading6045

import javax.inject.{Inject, Named, Singleton}

/**
  * 6045/46 Trading history - single caravans sublet by operator.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class SingleCaravansSubletController @Inject() (
  val caravansTrading6045View: caravansTrading6045,
  val navigator: AboutTheTradingHistoryNavigator,
  val withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
) extends CaravansTrading6045Controller(SingleCaravansSublet, mcc) {

  def getSavedAnswer: TurnoverSection6045 => Option[CaravansTrading6045] =
    _.singleCaravansSublet

  def updateAnswer(
    caravansTrading6045: CaravansTrading6045
  ): TurnoverSection6045 => TurnoverSection6045 =
    _.copy(singleCaravansSublet = Some(caravansTrading6045))

}
