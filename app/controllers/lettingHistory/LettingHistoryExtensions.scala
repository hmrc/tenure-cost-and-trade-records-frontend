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

import models.Session
import models.submissions.lettingHistory.SessionWrapper
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

extension (repository: SessionRepo)
  def saveOrUpdateSession(
    session: SessionWrapper
  )(using hc: HeaderCarrier, ec: ExecutionContext): Future[SessionWrapper] =
    repository.saveOrUpdate(session.data).map(_ => session)

extension (sessionData: Session)
  def withChangedData(bool: Boolean) =
    SessionWrapper(sessionData, changed = bool)
