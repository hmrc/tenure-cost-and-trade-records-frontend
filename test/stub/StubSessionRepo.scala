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

package stub

import models.Session
import play.api.libs.json.{JsValue, Json}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

/**
  * @author Yuriy Tumakha
  */
case class StubSessionRepo() extends SessionRepo:

  private var session: Option[JsValue] = None

  override def start(data: Session)(implicit hc: HeaderCarrier): Future[Unit] =
    saveOrUpdate(data)

  override def saveOrUpdate(data: Session)(implicit hc: HeaderCarrier): Future[Unit] =
    Future.successful {
      session = Some(Json.toJson(data))
    }

  override def get(implicit hc: HeaderCarrier): Future[Option[Session]] =
    Future.successful(session.map(_.as[Session]))

  override def remove()(implicit hc: HeaderCarrier): Future[Unit] =
    Future.successful {
      session = None
    }
