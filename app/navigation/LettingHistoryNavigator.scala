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
import play.api.mvc.Results.Redirect
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject

class LettingHistoryNavigator @Inject() (audit: Audit) extends Navigator(audit) with RentalPeriodSupport:

  private type NavigationData     = Map[String, String]
  private type NavigationFunction = (Session, NavigationData) => Option[Call]

  /*
   * This map specifies the BACK link call as a function of the current page identifier
   * and the current session data. It is used by controllers' actions in need to
   * determine the backLink URL of their template views.
   */
  private val backwardNavigationMap = Map[Identifier, NavigationFunction](
    PermanentResidentsPageId -> { (_, _) =>
      Some(controllers.routes.TaskListController.show().withFragment("lettingHistory"))
    },
    ResidentDetailPageId     -> { (_, _) =>
      Some(routes.PermanentResidentsController.show)
    },
    ResidentListPageId       -> { (currentSession, _) =>
      for size <- Some(permanentResidents(currentSession).size)
      yield
        if size > 0
        then
          if size < MaxNumberOfPermanentResidents
          then routes.ResidentDetailController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "permanentResidents")
        else routes.PermanentResidentsController.show
    },
    MaxNumberReachedPageId   -> { (_, navigationData) =>
      for kind <- navigationData.get("kind")
      yield
        if kind == "permanentResidents" then routes.ResidentListController.show
        else if kind == "temporaryOccupiers" then routes.OccupierListController.show
        else controllers.routes.TaskListController.show().withFragment("lettingHistory")
    },
    CompletedLettingsPageId  -> { (currentSession, _) =>
      for size <- Some(permanentResidents(currentSession).size)
      yield
        if size > 0 then routes.ResidentListController.show
        else
          hasPermanentResidents(currentSession) match
            case Some(true) => routes.ResidentDetailController.show(index = None)
            case _          => routes.PermanentResidentsController.show
    },
    OccupierDetailPageId     -> { (_, _) =>
      Some(routes.CompletedLettingsController.show)
    },
    RentalPeriodPageId       -> { (_, navigationData) =>
      Some(routes.OccupierDetailController.show(navigationData.get("index").map(_.toInt)))
    },
    OccupierListPageId       -> { (currentSession, _) =>
      for size <- Some(completedLettings(currentSession).size)
      yield
        if size > 0
        then
          if size < MaxNumberOfCompletedLettings
          then routes.OccupierDetailController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "temporaryOccupiers")
        else routes.CompletedLettingsController.show
    }
    // TODO IntendedLettingNightsPageId ... either CompletedLettings or OccupierList
  )

  def backLinkUrl(ofPage: Identifier, navigationData: Map[String, String] = Map.empty)(using
    request: SessionRequest[AnyContent]
  ): Option[String] =
    val call =
      for
        sessionToMaybeCallFunc <- backwardNavigationMap.get(ofPage)
        backwardCall           <- sessionToMaybeCallFunc.apply(request.sessionData, enriched(navigationData))
      yield backwardCall

    call.map(_.toString)

  private def enriched(navigationData: Map[String, String] = Map.empty)(using
    request: SessionRequest[AnyContent]
  ) =
    request.session.get("from").fold(ifEmpty = navigationData)(from => navigationData + ("from" -> from))

  // ----------------------------------------------------------------------------------------------------------------

  /*
   * This map specifies the FORWARD route call as a function of the current page identifier
   * and the current session data. It is used by controllers' actions that, usually
   * after having processed POST requests, need to result with SEE_OTHER location
   * (a.k.a. "redirect after-post" pattern)
   */
  private val forwardNavigationMap = Map[Identifier, NavigationFunction](
    PermanentResidentsPageId -> { (updatedSession, _) =>
      for answeredYes <- hasPermanentResidents(updatedSession)
      yield
        if answeredYes
        then
          if permanentResidents(updatedSession).size < MaxNumberOfPermanentResidents
          then routes.ResidentDetailController.show(index = None)
          else routes.ResidentListController.show
        else
          // AnswerNo
          routes.CompletedLettingsController.show
    },
    ResidentDetailPageId     -> { (_, _) =>
      Some(routes.ResidentListController.show)
    },
    ResidentRemovePageId     -> { (_, _) =>
      Some(routes.ResidentListController.show)
    },
    ResidentListPageId       -> { (updatedSession, navigationData) =>
      // assert(navigationData.isDefinedAt("hasMoreResidents"))
      for hasMoreResidents <- navigationData.get("hasMoreResidents").map(_.toBoolean)
      yield
        if hasMoreResidents
        then
          if permanentResidents(updatedSession).size < MaxNumberOfPermanentResidents
          then routes.ResidentDetailController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "permanentResidents")
        else
          // AnswerNo
          routes.CompletedLettingsController.show
    },
    MaxNumberReachedPageId   -> { (_, navigationData) =>
      for kind <- Some(navigationData("kind"))
      yield
        if kind == "permanentResidents" then routes.CompletedLettingsController.show
        else if kind == "temporaryOccupiers" then Call("GET", "/path/to/intended-nights")
        else controllers.routes.TaskListController.show().withFragment("lettingHistory")
    },
    CompletedLettingsPageId  -> { (updatedSession, _) =>
      for answeredYes <- hasCompletedLettings(updatedSession)
      yield
        if answeredYes
        then routes.OccupierDetailController.show(index = None)
        else
          // AnswerNo
          // TODO Introduce the HowManyNightsController
          Call("GET", "/path/to/intended-nights")
    },
    OccupierDetailPageId     -> { (_, navigationData) =>
      Some(routes.RentalPeriodController.show(index = Some(navigationData("index").toInt)))
    },
    RentalPeriodPageId       -> { (_, _) =>
      Some(routes.OccupierListController.show)
    },
    OccupierRemovePageId     -> { (_, _) =>
      Some(routes.OccupierListController.show)
    },
    OccupierListPageId       -> { (updatedSession, navigationData) =>
      // Note that navigationData.isDefinedAt("hadMoreOccupiers") is certainly true!
      // See ResidentListController.submit()
      for hadMoreOccupiers <- navigationData.get("hadMoreOccupiers").map(_.toBoolean)
      yield
        if hadMoreOccupiers
        then
          if completedLettings(updatedSession).size < MaxNumberOfCompletedLettings
          then routes.OccupierDetailController.show(index = None)
          else routes.MaxNumberReachedController.show(kind = "temporaryOccupiers")
        else
          // AnswerNo
          // TODO Introduce the IntendedLettingsNightsController
          Call("GET", "/path/to/intended-nights")
    }
  )

  @deprecated(message =
    "Having two navigation maps, such as forward and backward navigation, is better than having just one"
  )
  override val routeMap: Map[Identifier, Session => Call] = Map.empty

  def redirect(currentPage: Identifier, updatedSession: Session, navigationData: Map[String, String] = Map.empty)(using
    hc: HeaderCarrier,
    request: SessionRequest[AnyContent]
  ): Result = {
    val nextCall =
      for call <- forwardNavigationMap(currentPage)(updatedSession, enriched(navigationData))
      yield auditNextUrl(updatedSession)(call)

    nextCall match
      case Some(call) =>
        Redirect(call).withSession(request.session + ("from" -> currentPage.toString))
      case _          =>
        throw new Exception("NavigatorIllegalStage : couldn't determine redirect call")
  }
