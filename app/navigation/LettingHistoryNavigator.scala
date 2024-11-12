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
import controllers.lettingHistory.routes
import models.Session
import models.submissions.common.AnswerYes
import models.submissions.lettingHistory.LettingHistory
import models.submissions.lettingHistory.LettingHistory.{MaxNumberOfPermanentResidents, hasPermanentResidents, permanentResidents}
import navigation.identifiers.{Identifier, PermanentResidentsPageId, ResidentDetailPageId, ResidentListPageId}
import play.api.mvc.Results.Redirect
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject

class LettingHistoryNavigator @Inject() (audit: Audit) extends Navigator(audit):

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
      if permanentResidents(currentSession).size < MaxNumberOfPermanentResidents
      then Some(routes.ResidentDetailController.show(index = None))
      else Some(routes.PermanentResidentsController.show)
    }
  )

  def backLinkUrl(ofPage: Identifier, navigationData: Map[String, String] = Map.empty)(using
    request: SessionRequest[AnyContent]
  ): Option[String] =
    val navigationDataAndFromPage =
      request.session
        .get("from")
        .fold(ifEmpty = navigationData)(from => navigationData + ("from" -> from))

    val call =
      for
        sessionToMaybeCallFunc <- backwardNavigationMap.get(ofPage)
        backwardCall           <- sessionToMaybeCallFunc.apply(request.sessionData, navigationDataAndFromPage)
      yield backwardCall

    call.map(_.toString)

  // ----------------------------------------------------------------------------------------------------------------

  /*
   * This map specifies the FORWARD route call as a function of the current page identifier
   * and the current session data. It is used by controllers' actions that, usually
   * after having processed POST requests, need to result with SEE_OTHER location
   * (a.k.a. "redirect after-post" pattern)
   */
  private val forwardNavigationMap = Map[Identifier, NavigationFunction](
    PermanentResidentsPageId -> { (updatedSession, _) =>
      for answer <- hasPermanentResidents(updatedSession)
      yield
        if answer == AnswerYes
        then
          if permanentResidents(updatedSession).size < MaxNumberOfPermanentResidents
          then routes.ResidentDetailController.show(index = None)
          else routes.ResidentListController.show
        else
          // AnswerNo
          // TODO Introduce the controllers.lettingHistory.CompletedLettingsController
          Call("GET", "/path/to/completed-lettings")
    },
    ResidentDetailPageId     -> { (_, _) =>
      Some(routes.ResidentListController.show)
    },
    ResidentListPageId       -> { (updatedSession, navigationData) =>
      // Note that navigationData.isDefinedAt("hasMoreResidents") is certainly true! See ResidentListController.submit()
      if navigationData("hasMoreResidents") == AnswerYes.toString
      then
        if permanentResidents(updatedSession).size < MaxNumberOfPermanentResidents
        then Some(routes.ResidentDetailController.show())
        else
          // TODO Introduce the controllers.lettingHistory.MaxPermanentResidentsController
          Some(Call("GET", "/path/to/max-permanent-residents"))
      else
        // AnswerNo
        // TODO Introduce the controllers.lettingHistory.CompletedLettingsController
        Some(Call("GET", "/path/to/completed-lettings"))
    }
  )

  @deprecated
  override val routeMap: Map[Identifier, Session => Call] = Map.empty

  def redirect(fromPage: Identifier, updatedSession: Session, navigationData: Map[String, String] = Map.empty)(using
    hc: HeaderCarrier,
    request: SessionRequest[AnyContent]
  ): Result = {
    val nextCall =
      for call <- forwardNavigationMap(fromPage)(updatedSession, navigationData)
      yield auditNextUrl(updatedSession)(call)

    nextCall match
      case Some(call) =>
        Redirect(call).withSession(request.session + ("from" -> fromPage.toString))
      case _          =>
        throw new Exception("NavigatorIllegalStage : couldn't determine redirect call")
  }
