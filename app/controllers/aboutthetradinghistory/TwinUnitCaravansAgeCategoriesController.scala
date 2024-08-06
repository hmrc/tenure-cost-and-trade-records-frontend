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

import actions.{SessionRequest, WithSessionRefiner}
import models.submissions.aboutthetradinghistory.Caravans.CaravanUnitType.Twin
import models.submissions.aboutthetradinghistory.{Caravans, CaravansAge}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TwinCaravansAgeCategoriesId
import play.api.mvc.{AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.caravansAgeCategories

import javax.inject.{Inject, Named, Singleton}

/**
  * 6045/6046 Trading history - How old are the twin-unit static caravans on site.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class TwinUnitCaravansAgeCategoriesController @Inject() (
  val caravansAgeCategoriesView: caravansAgeCategories,
  val navigator: AboutTheTradingHistoryNavigator,
  val withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
) extends CaravansAgeCategoriesController(TwinCaravansAgeCategoriesId, Twin, mcc) {

  def savedAnswer(implicit
    request: SessionRequest[AnyContent]
  ): Option[CaravansAge] = request.sessionData.aboutTheTradingHistoryPartOne
    .flatMap(_.caravans)
    .flatMap(_.twinUnitCaravansAge)

  def updateAnswer(caravansAge: CaravansAge): Caravans => Caravans =
    _.copy(twinUnitCaravansAge = Some(caravansAge))

}
