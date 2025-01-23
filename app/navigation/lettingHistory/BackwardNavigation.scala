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

package navigation.lettingHistory

import controllers.lettingHistory.routes
import models.submissions.lettingHistory.LettingHistory
import models.submissions.lettingHistory.LettingHistory.*
import navigation.identifiers.*

trait BackwardNavigation:
  this: Navigator =>

  val backwardNavigationMap: NavigationMap = Map(
    HasPermanentResidentsPageId -> { (_, _) =>
      Some(taskListCall)
    },
    ResidentDetailPageId        -> { (_, _) =>
      Some(routes.HasPermanentResidentsController.show)
    },
    ResidentListPageId          -> { (session, _) =>
      None
    },
    MaxNumberReachedPageId      -> { (_, navigation) =>
      for kind <- navigation.get("kind")
      yield kind match
        case "permanentResidents" => routes.ResidentListController.show
        case "completedLettings"  => routes.OccupierListController.show
        case "onlineAdvertising"  => routes.AdvertisingListController.show
        case _                    => taskListCall
    },
    HasCompletedLettingsPageId  -> { (session, _) =>
      for size <- Some(permanentResidents(session.data).size)
      yield
        if size > 0 then routes.ResidentListController.show
        else
          hasPermanentResidents(session.data) match
            case Some(true) => routes.ResidentDetailController.show(index = None)
            case _          => routes.HasPermanentResidentsController.show
    },
    OccupierDetailPageId        -> { (_, _) =>
      Some(routes.HasCompletedLettingsController.show)
    },
    RentalPeriodPageId          -> { (_, navigation) =>
      for case index: Int <- navigation.get("index")
      yield routes.OccupierDetailController.show(index = Some(index))
    },
    OccupierListPageId          -> { (session, _) =>
      None
    },
    HowManyNightsPageId         -> { (session, _) =>
      for doesHaveCompletedLettings <- hasCompletedLettings(session.data)
      yield
        if doesHaveCompletedLettings
        then routes.OccupierListController.show
        else routes.HasCompletedLettingsController.show
    },
    HasStoppedLettingPageId     -> { (_, _) =>
      Some(routes.HowManyNightsController.show)
    },
    WhenWasLastLetPageId        -> { (_, _) =>
      Some(routes.HasStoppedLettingController.show)
    },
    IsYearlyAvailablePageId     -> { (session, _) =>
      for {
        intendedLettings <- intendedLettings(session.data)
      } yield
        if intendedLettings.hasStopped.isEmpty
        then routes.HowManyNightsController.show
        else
          intendedLettings.hasStopped.map { hasStopped =>
            if hasStopped
            then routes.WhenWasLastLetController.show
            else routes.HasStoppedLettingController.show
          }.get
    },
    TradingSeasonLengthPageId   -> { (_, _) =>
      Some(routes.IsYearlyAvailableController.show)
    },
    HasOnlineAdvertisingPageId  -> { (session, _) =>
      for {
        intendedLettings <- intendedLettings(session.data)
      } yield
        if intendedLettings.isYearlyAvailable.isEmpty
        then routes.HowManyNightsController.show
        else
          intendedLettings.isYearlyAvailable.map { isYearlyAvailable =>
            if isYearlyAvailable
            then routes.IsYearlyAvailableController.show
            else routes.TradingSeasonController.show
          }.get
    },
    AdvertisingDetailPageId     -> { (_, _) =>
      Some(routes.HasOnlineAdvertisingController.show)
    },
    AdvertisingListPageId       -> { (session, _) =>
      None
    },
    CheckYourAnswersPageId      -> { (session, _) =>
      for doesHaveOnlineAdvertising <- hasOnlineAdvertising(session.data)
      yield
        if doesHaveOnlineAdvertising
        then routes.AdvertisingListController.show
        else routes.HasOnlineAdvertisingController.show
    }
  )
