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
import models.Session
import models.submissions.lettingHistory.IntendedLettings.isAboveThreshold
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.{IntendedLettings, LettingHistory}
import navigation.identifiers.{Identifier as PageIdentifier, *}

trait ForwardNavigation:
  this: Navigator =>

  //
  // Each entry of this map provides the forward navigation logic for each web page,
  // given as a function of:
  //   - the updated session data
  //   - and the navigation data
  //
  // It is used by controllers' actions that, usually after having processed POST requests,
  // need to result with SEE_OTHER location (a.k.a. "redirect after-post" pattern)
  //
  val forwardNavigationMap: NavigationMap = Map(
    HasPermanentResidentsPageId   -> { (session, navigation) =>
      for doesHavePermanentResidents <- hasPermanentResidents(session.data)
      yield
        if doesHavePermanentResidents
        then
          if permanentResidents(session.data).size < MaxNumberOfPermanentResidents
          then routes.ResidentDetailController.show(index = None)
          else routes.ResidentListController.show
        else routes.HasCompletedLettingsController.show
    },
    ResidentDetailPageId          -> { (_, _) =>
      Some(routes.ResidentListController.show)
    },
    ResidentRemovePageId          -> { (_, _) =>
      Some(routes.ResidentListController.show)
    },
    ResidentListPageId            -> { (session, navigation) =>
      for case hasMoreResidents: Boolean <- navigation.get("hasMoreResidents")
      yield
        if hasMoreResidents
        then
          if permanentResidents(session.data).size < MaxNumberOfPermanentResidents
          then routes.ResidentDetailController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "permanentResidents")
        else routes.HasCompletedLettingsController.show
    },
    MaxNumberReachedPageId        -> { (_, navigation) =>
      for kind <- Some(navigation("kind"))
      yield kind match
        case "permanentResidents" => routes.HasCompletedLettingsController.show
        case "temporaryOccupiers" => routes.HowManyNightsController.show
        case "onlineAdvertising"  => routes.CheckYourAnswersController.show
        case _                    => controllers.routes.TaskListController.show().withFragment("lettingHistory")
    },
    HasCompletedLettingsPageId    -> { (session, _) =>
      for doesHaveCompletedLettings <- hasCompletedLettings(session.data)
      yield
        if doesHaveCompletedLettings
        then
          if completedLettings(session.data).size < MaxNumberOfCompletedLettings
          then routes.OccupierDetailController.show(index = None)
          else routes.OccupierListController.show
        else routes.HowManyNightsController.show
    },
    OccupierDetailPageId          -> { (_, navigation) =>
      for case index: Int <- navigation.get("index")
      yield routes.RentalPeriodController.show(index = Some(index))
    },
    RentalPeriodPageId            -> { (_, _) =>
      Some(routes.OccupierListController.show)
    },
    OccupierRemovePageId          -> { (_, _) =>
      Some(routes.OccupierListController.show)
    },
    OccupierListPageId            -> { (session, navigation) =>
      // Note that navigation.isDefinedAt("hadMoreOccupiers") is certainly true!
      // See ResidentListController.submit()
      for case hadMoreOccupiers: Boolean <- navigation.get("hadMoreOccupiers")
      yield
        if hadMoreOccupiers
        then
          if completedLettings(session.data).size < MaxNumberOfCompletedLettings
          then routes.OccupierDetailController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "temporaryOccupiers")
        else routes.HowManyNightsController.show
    },
    HowManyNightsPageId           -> { (session, _) =>
      for meetsCriteria <- doesMeetLettingCriteria(session.data)
      yield
        if meetsCriteria
        then routes.IsYearlyAvailableController.show
        else routes.HasStoppedLettingController.show
    },
    HasStoppedLettingPageId       -> { (_, navigation) =>
      for case hasStopped: Boolean <- navigation.get("hasStopped")
      yield
        if hasStopped
        then routes.WhenWasLastLetController.show
        else routes.IsYearlyAvailableController.show
    },
    WhenWasLastLetPageId          -> { (_, _) =>
      Some(routes.IsYearlyAvailableController.show)
    },
    IsYearlyAvailablePageId       -> { (session, _) =>
      for
        intendedLettings  <- intendedLettings(session.data)
        isYearlyAvailable <- intendedLettings.isYearlyAvailable
      yield
        if isYearlyAvailable
        then routes.AdvertisingOnlineController.show
        else routes.TradingSeasonLengthController.show
    },
    TradingSeasonLengthPageId     -> { (_, _) =>
      Some(routes.AdvertisingOnlineController.show)
    },
    HasOnlineAdvertisingPageId    -> { (session, _) =>
      hasOnlineAdvertising(session.data) match
        case Some(true) => Some(routes.AdvertisingOnlineDetailsController.show(index = None))
        case _          => Some(routes.CheckYourAnswersController.show)
    },
    OnlineAdvertisingDetailPageId -> { (_, _) => Some(routes.AdvertisingListController.show) },
    AdvertisingRemovePageId       -> { (_, _) => Some(routes.AdvertisingListController.show) },
    AdvertisingListPageId         -> { (session, navigation) =>
      for case hasMoreAdvertising: Boolean <- navigation.get("hasMoreAdvertisingDetails")
      yield
        if hasMoreAdvertising
        then
          if onlineAdvertising(session.data).sizeIs < MaxNumberOfAdvertisingOnline
          then routes.AdvertisingOnlineDetailsController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "onlineAdvertising")
        else routes.CheckYourAnswersController.show
    }
    // TODO CheckYourAnswersPageId -> { (_, _) =>
    //   ???
    // }
  )

  private def doesMeetLettingCriteria(session: Session): Option[Boolean] =
    for
      lettingHistory   <- session.lettingHistory
      intendedLettings <- lettingHistory.intendedLettings
      nights           <- intendedLettings.nights
    yield isAboveThreshold(nights, session.isWelsh)
