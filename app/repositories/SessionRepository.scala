/*
 * Copyright 2022 HM Revenue & Customs
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

package repositories

import com.google.inject.Singleton

import javax.inject.Inject
import org.mongodb.scala.model.IndexModel
import play.api.libs.json._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.NoSessionException
import uk.gov.hmrc.mongo.play.json.{Codecs, PlayMongoRepository}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.play.http.logging.Mdc
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import java.time.Instant
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

@Singleton
class Session @Inject()(mongo: MongoComponent)(implicit executionContext: ExecutionContext)
  extends SessionRepository("Session", mongo)


abstract class SessionRepository @Inject()(formId: String, mongo: MongoComponent)(
  implicit executionContext: ExecutionContext)
  extends PlayMongoRepository[SessionData](
    collectionName = "sessions",
    mongoComponent = mongo,
    domainFormat = SessionData.format,
    indexes =
      Seq(IndexModel(Indexes.ascending("createdAt"), IndexOptions().name("sessionTTL").expireAfter((2L), HOURS)))
  ) with SessionRepo {

  override def start[A](data: A)(implicit wts: Writes[A], hc: HeaderCarrier): Future[Unit] =
    saveOrUpdate[A](data)

  override def saveOrUpdate[A](data: A)(implicit wts: Writes[A], hc: HeaderCarrier): Future[Unit] =
    Mdc.preservingMdc {
      for {
        sessionId <- getSessionId
        _ <- collection
          .findOneAndUpdate(
            filter = Filters.equal("_id", sessionId),
            update = Updates.combine(
              Updates.set(s"data.$formId", Codecs.toBson(data)),
              Updates.setOnInsert("createdAt", Instant.now)
            ),
            options = FindOneAndUpdateOptions().upsert(true)
          )
          .toFuture()
      } yield {
        ()
      }
    }

  override def get[A](implicit rds: Reads[A], hc: HeaderCarrier): Future[Option[A]] =
    Mdc.preservingMdc {
      for {
        sessionId   <- getSessionId
        maybeOption <- collection.find(Filters.equal("_id", sessionId)).headOption()
      } yield {
        maybeOption
          .map(_.data \ formId)
          .flatMap(x =>
            x match {
              case JsDefined(value) => Some(value.as[A])
              case JsUndefined()    => None
            })
      }
    }

  def findFirst(): Future[SessionData] =
    Mdc.preservingMdc {
      collection
        .find()
        .first()
        .toFuture()
    }

  override def remove()(implicit hc: HeaderCarrier): Future[Unit] =
    Mdc.preservingMdc {
      for {
        sessionId <- getSessionId
        -         <- collection.deleteOne(equal("_id", sessionId)).toFuture
      } yield {
        ()
      }
    }

  def removeAll()(implicit hc: HeaderCarrier): Future[Unit] =
    Mdc.preservingMdc {
      for {
        sessionId <- getSessionId
        -         <- collection.deleteMany(equal("_id", sessionId)).toFuture
      } yield {
        ()
      }
    }

  private val noSession = Future.failed[String](NoSessionException)

  private def getSessionId(implicit hc: HeaderCarrier): Future[String] =
    hc.sessionId.fold(noSession)(c => Future.successful(c.value))

}

case class SessionData(_id: String, data: JsValue, createdAt: Instant = Instant.now)

object SessionData {

  implicit val formatInstant: Format[Instant] = MongoJavatimeFormats.instantFormat
  val format = Json.format[SessionData]
}

trait SessionRepo {

  def start[A](data: A)(implicit wts: Writes[A], hc: HeaderCarrier): Future[Unit]

  def saveOrUpdate[A](data: A)(implicit wts: Writes[A], hc: HeaderCarrier): Future[Unit]

  def get[A](implicit rds: Reads[A], hc: HeaderCarrier): Future[Option[A]]

  def remove()(implicit hc: HeaderCarrier): Future[Unit]
}