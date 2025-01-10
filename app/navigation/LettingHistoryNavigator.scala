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

package navigation

import actions.SessionRequest
import connectors.Audit
import controllers.lettingHistory.{RentalPeriodSupport, routes}
import models.Session
import models.submissions.lettingHistory.LettingHistory
import models.submissions.lettingHistory.LettingHistory.*
import navigation.identifiers.*
import navigation.identifiers.Identifier as PageIdentifier
import play.api.mvc.Results.Redirect
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject

class LettingHistoryNavigator @Inject() (audit: Audit) extends Navigator(audit) with RentalPeriodSupport:

  private type NavigationData     = Map[String, String | Int | Boolean]
  private type NavigationFunction = (Session, NavigationData) => Option[Call]
  private type NavigationMap      = Map[PageIdentifier, NavigationFunction]

  /*
   * This map specifies the BACK link call as a function of the current page identifier
   * and the current session data. It is used by controllers' actions in need to
   * determine the backLink URL of their template views.
   */
  private val backwardNavigationMap: NavigationMap = Map(
    PermanentResidentsPageId       -> { (_, _) =>
      Some(controllers.routes.TaskListController.show().withFragment("lettingHistory"))
    },
    ResidentDetailPageId           -> { (_, _) =>
      Some(routes.HasPermanentResidentsController.show)
    },
    ResidentListPageId             -> { (currentSession, _) =>
      for size <- Some(permanentResidents(currentSession).size)
      yield
        if size > 0
        then
          if size < MaxNumberOfPermanentResidents
          then routes.ResidentDetailController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "permanentResidents")
        else routes.HasPermanentResidentsController.show
    },
    MaxNumberReachedPageId         -> { (_, navigationData) =>
      for kind <- navigationData.get("kind")
      yield kind match
        case "permanentResidents" => routes.ResidentListController.show
        case "temporaryOccupiers" => routes.OccupierListController.show
        case "advertisingOnline"  => routes.AdvertisingListController.show
        case _                    => controllers.routes.TaskListController.show().withFragment("lettingHistory")
    },
    CompletedLettingsPageId        -> { (currentSession, _) =>
      for size <- Some(permanentResidents(currentSession).size)
      yield
        if size > 0 then routes.ResidentListController.show
        else
          hasPermanentResidents(currentSession) match
            case Some(true) => routes.ResidentDetailController.show(index = None)
            case _          => routes.HasPermanentResidentsController.show
    },
    OccupierDetailPageId           -> { (_, _) =>
      Some(routes.HasCompletedLettingsController.show)
    },
    RentalPeriodPageId             -> { (_, navigationData) =>
      for case index: Int <- navigationData.get("index")
      yield routes.OccupierDetailController.show(index = Some(index))
    },
    OccupierListPageId             -> { (currentSession, _) =>
      for size <- Some(completedLettings(currentSession).size)
      yield
        if size > 0
        then
          if size < MaxNumberOfCompletedLettings
          then routes.OccupierDetailController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "temporaryOccupiers")
        else routes.HasCompletedLettingsController.show
    },
    HowManyNightsPageId            -> { (currentSession, _) =>
      for doesHaveCompletedLettings <- hasCompletedLettings(currentSession)
      yield
        if doesHaveCompletedLettings
        then routes.OccupierListController.show
        else routes.HasCompletedLettingsController.show
    },
    HasStoppedLettingPageId        -> { (_, _) =>
      Some(routes.HowManyNightsController.show)
    },
    WhenWasLastLetPageId           -> { (_, _) =>
      Some(routes.HasStoppedLettingController.show)
    },
    IsYearlyAvailablePageId        -> { (currentSession, _) =>
      for {
        intendedLettings <- intendedLettings(currentSession)
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
    TradingSeasonLengthPageId      -> { (_, _) =>
      Some(routes.IsYearlyAvailableController.show)
    },
    AdvertisingOnlinePageId        -> { (currentSession, _) =>
      for {
        intendedLettings <- intendedLettings(currentSession)
      } yield
        if intendedLettings.isYearlyAvailable.isEmpty
        then routes.HowManyNightsController.show
        else
          intendedLettings.isYearlyAvailable.map { isYearlyAvailable =>
            if isYearlyAvailable
            then routes.IsYearlyAvailableController.show
            else routes.TradingSeasonLengthController.show
          }.get
    },
    AdvertisingOnlineDetailsPageId -> { (_, _) =>
      Some(routes.AdvertisingOnlineController.show)
    },
    AdvertisingListPageId          -> { (currentSession, _) =>
      if (getAdvertisingOnlineDetails(currentSession).sizeIs >= MaxNumberOfAdvertisingOnline)
        Some(routes.MaxNumberReachedController.show(kind = "advertisingOnline"))
      else
        Some(routes.AdvertisingOnlineDetailsController.show(index = None))
    }
  )

  def backLinkUrl(ofPage: PageIdentifier, navigationData: NavigationData = Map.empty)(using
    request: SessionRequest[AnyContent]
  ): Option[String] =
    val call =
      for
        sessionToMaybeCallFunc <- backwardNavigationMap.get(ofPage)
        backwardCall           <- sessionToMaybeCallFunc.apply(request.sessionData, navigationData.withFrom)
      yield backwardCall

    call.map(_.toString)

  // ----------------------------------------------------------------------------------------------------------------

  /*
   * This map specifies the FORWARD route call as a function of the current page identifier
   * and the current session data. It is used by controllers' actions that, usually
   * after having processed POST requests, need to result with SEE_OTHER location
   * (a.k.a. "redirect after-post" pattern)
   */
  private val forwardNavigationMap: NavigationMap = Map(
    PermanentResidentsPageId       -> { (updatedSession, _) =>
      for doesHavePermanentResidents <- hasPermanentResidents(updatedSession)
      yield
        if doesHavePermanentResidents
        then
          if permanentResidents(updatedSession).size < MaxNumberOfPermanentResidents
          then routes.ResidentDetailController.show(index = None)
          else routes.ResidentListController.show
        else routes.HasCompletedLettingsController.show
    },
    ResidentDetailPageId           -> { (_, _) =>
      Some(routes.ResidentListController.show)
    },
    ResidentRemovePageId           -> { (_, _) =>
      Some(routes.ResidentListController.show)
    },
    ResidentListPageId             -> { (updatedSession, navigationData) =>
      // assert(navigationData.isDefinedAt("hasMoreResidents"))
      for case hasMoreResidents: Boolean <- navigationData.get("hasMoreResidents")
      yield
        if hasMoreResidents
        then
          if permanentResidents(updatedSession).size < MaxNumberOfPermanentResidents
          then routes.ResidentDetailController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "permanentResidents")
        else routes.HasCompletedLettingsController.show
    },
    MaxNumberReachedPageId         -> { (_, navigationData) =>
      for kind <- Some(navigationData("kind"))
      yield kind match
        case "permanentResidents" => routes.HasCompletedLettingsController.show
        case "temporaryOccupiers" => routes.HowManyNightsController.show
        // TODO case "advertisingOnline" => ???
        case _                    => controllers.routes.TaskListController.show().withFragment("lettingHistory")
    },
    CompletedLettingsPageId        -> { (updatedSession, _) =>
      for doesHaveCompletedLettings <- hasCompletedLettings(updatedSession)
      yield
        if doesHaveCompletedLettings
        then routes.OccupierDetailController.show(index = None)
        else routes.HowManyNightsController.show
    },
    OccupierDetailPageId           -> { (_, navigationData) =>
      for case index: Int <- navigationData.get("index")
      yield routes.RentalPeriodController.show(index = Some(index))
    },
    RentalPeriodPageId             -> { (_, _) =>
      Some(routes.OccupierListController.show)
    },
    OccupierRemovePageId           -> { (_, _) =>
      Some(routes.OccupierListController.show)
    },
    OccupierListPageId             -> { (updatedSession, navigationData) =>
      // Note that navigationData.isDefinedAt("hadMoreOccupiers") is certainly true!
      // See ResidentListController.submit()
      for case hadMoreOccupiers: Boolean <- navigationData.get("hadMoreOccupiers")
      yield
        if hadMoreOccupiers
        then
          if completedLettings(updatedSession).size < MaxNumberOfCompletedLettings
          then routes.OccupierDetailController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "temporaryOccupiers")
        else routes.HowManyNightsController.show
    },
    HowManyNightsPageId            -> { (currentSession, _) =>
      for meetsCriteria <- doesMeetLettingCriteria(currentSession)
      yield
        if meetsCriteria
        then routes.IsYearlyAvailableController.show
        else routes.HasStoppedLettingController.show
    },
    HasStoppedLettingPageId        -> { (_, navigationData) =>
      // assert navigationData.isDefinedAt("hasStopped"))
      for case hasStopped: Boolean <- navigationData.get("hasStopped")
      yield
        if hasStopped
        then routes.WhenWasLastLetController.show
        else routes.IsYearlyAvailableController.show
    },
    WhenWasLastLetPageId           -> { (_, _) =>
      Some(routes.IsYearlyAvailableController.show)
    },
    IsYearlyAvailablePageId        -> { (currentSession, _) =>
      for
        intendedLettings  <- intendedLettings(currentSession)
        isYearlyAvailable <- intendedLettings.isYearlyAvailable
      yield
        if isYearlyAvailable
        then routes.AdvertisingOnlineController.show
        else routes.TradingSeasonLengthController.show
    },
    TradingSeasonLengthPageId      -> { (_, _) =>
      Some(routes.AdvertisingOnlineController.show)
    },
    AdvertisingOnlinePageId        -> { (currentSession, _) =>
      hasOnlineAdvertising(currentSession) match
        case Some(true) => Some(routes.AdvertisingOnlineDetailsController.show(index = None))
        case _          => Some(controllers.routes.TaskListController.show()) // TODO: CYA
    },
    AdvertisingOnlineDetailsPageId -> { (_, _) => Some(routes.AdvertisingListController.show) },
    AdvertisingRemovePageId        -> { (_, _) => Some(routes.AdvertisingListController.show) },
    AdvertisingListPageId          -> { (updatedSession, navigationData) =>
      for case hasMoreAdvertising: Boolean <- navigationData.get("hasMoreAdvertisingDetails")
      yield
        if hasMoreAdvertising
        then
          if getAdvertisingOnlineDetails(updatedSession).sizeIs < MaxNumberOfAdvertisingOnline
          then routes.AdvertisingOnlineDetailsController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "advertisingOnline")
        else controllers.routes.TaskListController.show() // TODO: CYA
    }
  )

  def redirect(currentPage: PageIdentifier, updatedSession: Session, navigationData: NavigationData = Map.empty)(using
    hc: HeaderCarrier,
    request: SessionRequest[AnyContent]
  ): Result = {
    val nextCall =
      for call <- forwardNavigationMap(currentPage)(updatedSession, navigationData.withFrom)
      yield auditNextUrl(updatedSession)(call)

    nextCall match
      case Some(call) =>
        Redirect(call).withFrom(currentPage)
      case _          =>
        throw new Exception("NavigatorIllegalStage : couldn't determine redirect call")
  }

  // ---------------------------
  //   D E P R E C A T E D
  // ---------------------------

  @deprecated(message =
    "Rather than this route map, maybe easier with 2 separate route maps, such as the `forward` and the `backward` navigation maps"
  )
  override val routeMap: Map[PageIdentifier, Session => Call] = Map.empty

  extension (navigationData: NavigationData)
    @deprecated(message =
      "There may be no need to enrich the navigation data with the page identifier that the HTTP request was sent from."
    )
    def withFrom(using
      request: SessionRequest[AnyContent]
    ): NavigationData =
      request.session.get("from").fold(ifEmpty = navigationData)(from => navigationData + ("from" -> from))

  extension (result: Result)
    @deprecated(message =
      "There may be no need to enrich the result with the page identifier that the HTTP response is going to be sent from."
    )
    def withFrom(currentPage: PageIdentifier)(using request: SessionRequest[AnyContent]) =
      result.withSession(request.session + ("from" -> currentPage.toString))
