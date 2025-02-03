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

package navigation

import connectors.Audit
import controllers.accommodation
import models.Session
import navigation.identifiers.*
import play.api.mvc.Call

import javax.inject.Inject

class AccommodationNavigator @Inject() (audit: Audit) extends Navigator(audit):

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AccommodationUnitPageId           -> (_ => accommodation.routes.AvailableRooms6048Controller.show),
    AvailableRoomsPageId              -> (_ => accommodation.routes.AccommodationLettingHistory6048Controller.show),
    AccommodationLettingHistoryPageId -> (_ => accommodation.routes.HighSeasonTariff6048Controller.show),
    HighSeasonTariffPageId            -> (_ => accommodation.routes.IncludedTariffItems6048Controller.show),
    IncludedTariffItemsPageId         -> (_ => accommodation.routes.AccommodationUnitList6048Controller.show),
    AccommodationUnitListPageId       -> (_ =>
      controllers.routes.TaskListController.show().withFragment("accommodation-details")
    ) // TODO: CYA Accommodation details
  )
