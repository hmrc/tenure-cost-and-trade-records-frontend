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

package views.lettingHistory

import actions.SessionRequest
import models.Session
import models.submissions.lettingHistory.LettingHistory.{intendedLettings, *}
import play.api.mvc.AnyContent

object LettingHistoryTaskListHelper:

  def isTaskComplete(taskId: String)(using request: SessionRequest[?]) =
    taskId match
        case "residential-tenants" =>
          (for
            hasPermanentResidents <- hasPermanentResidents(request.sessionData)
            doesNotHavePermanentResidents = !hasPermanentResidents
            permanentResidentList = permanentResidents(request.sessionData)
          yield
            if doesNotHavePermanentResidents then "yes"
            else if permanentResidentList.nonEmpty then "yes"
            else "no"

          ).getOrElse("no")

        case "temporary-occupiers" =>
          (for
            hasCompletedLettings <- hasCompletedLettings(request.sessionData)
            doesNotHaveCompletedLettings = !hasCompletedLettings
            completedLettingsList = completedLettings(request.sessionData)
          yield
            if doesNotHaveCompletedLettings then "yes"
            else if completedLettingsList.nonEmpty then "yes"
            else "no"

            ).getOrElse("no")

        case _ =>
          "no"
