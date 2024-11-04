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

import connectors.Audit
import models.Session
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import models.submissions.lettingHistory.LettingHistory
import navigation.identifiers.{Identifier, PermanentResidentsPageId}
import play.api.mvc.Call

import javax.inject.Inject

class LettingHistoryNavigator @Inject() (audit: Audit) extends Navigator(audit):

  override val routeMap: Map[Identifier, Session => Call] = Map(
    PermanentResidentsPageId -> { session =>
      LettingHistory.isPermanentResidence(session) match
        case Some(AnswerYes) =>
          Call("GET", "/path/to/residents-details")
        // TODO Introduce the controllers.lettingHistory.ResidentsDetailsController

        case Some(AnswerNo) =>
          Call("GET", "/path/to/completed-commercial-lettings")
        // TODO Introduce the controllers.lettingHistory.CompletedCommercialLettingsController

        case _ =>
          // As long as this navigator gets invoked AFTER the session got copied with letting history data
          // this case should never ever match
          throw new RuntimeException(
            "InvalidNavigation: session.lettingHistory.isPermanentResidence was expected to be defined"
          )
    }
  )
