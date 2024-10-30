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

package controllers.lettingHistory

import actions.SessionRequest
import models.Session
import models.submissions.lettingHistory.SessionWrapper
import play.api.libs.json.Writes
import play.api.mvc.AnyContent
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

extension (repository: SessionRepo)
  def saveOrUpdateSession(
    session: SessionWrapper
  )(using ws: Writes[Session], hc: HeaderCarrier, ec: ExecutionContext): Future[SessionWrapper] =
    repository.saveOrUpdate(session.data).map(_ => session)

extension (sessionData: Session)
  def withChangedData(bool: Boolean) =
    SessionWrapper(sessionData, changed = bool)

extension (request: SessionRequest[AnyContent])
  def unchangedSession =
    SessionWrapper(request.sessionData, changed = false)

  private def isFrom(pageId: String): Boolean =
    hasFrom(ifEmpty = false)(_.contains(pageId))

  private def hasFrom[T](ifEmpty: T)(fn: String => T) =
    request
      .getQueryString("from")
      .map(fn)
      .getOrElse {
        request.body.asFormUrlEncoded
          .map { submittedData =>
            submittedData
              .get("from")
              .flatMap(_.headOption)
              .map(fn)
              .getOrElse(ifEmpty)
          }
          .getOrElse(ifEmpty)
      }

  def eventualFromFragment: Option[String] =
    hasFrom(ifEmpty = None) { fromValue =>
      if fromValue.nonEmpty
      then
        val splitted = fromValue.split(";")
        if splitted.size > 1 then Some(splitted(1)) else None
      else None
    }

  def isFromCheckYourAnswer: Boolean = isFrom("CYA")

  def isFromTaskList: Boolean = isFrom("TL")
