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

package controllers.lettingHistory

import models.Session
import play.api.libs.json.Writes
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.Future.{failed, successful}

extension (repository: SessionRepo)
  def saveOrUpdateSession(
    session: Session
  )(using ws: Writes[Session], hc: HeaderCarrier, ec: ExecutionContext): Future[Session] =
    repository.saveOrUpdate(session).map(_ => session)

extension (opt: Option[Boolean])
  def toFuture = opt match
    case Some(b) => successful(b)
    case _       => failed(new Exception("undefined boolean"))
