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

package repositories

import com.google.inject.Singleton
import crypto.MongoCrypto
import models.{SensitiveSession, Session}
import org.mongodb.scala.SingleObservableFuture
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.*
import play.api.libs.json.*
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.mongo.play.json.{Codecs, PlayMongoRepository}
import uk.gov.hmrc.play.http.logging.Mdc

import java.time.Instant
import javax.inject.Inject
import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SessionRepository @Inject() (mongo: MongoComponent)(implicit
  executionContext: ExecutionContext,
  crypto: MongoCrypto
) extends PlayMongoRepository[SensitiveSessionData](
      collectionName = "sessions",
      mongoComponent = mongo,
      domainFormat = SensitiveSessionData.format,
      indexes =
        Seq(IndexModel(Indexes.ascending("createdAt"), IndexOptions().name("sessionTTL").expireAfter(2L, HOURS))),
      extraCodecs = Seq(
        Codecs.playFormatCodec(MongoJavatimeFormats.instantFormat)
      )
    )
    with SessionRepo {

  def start(data: Session)(implicit wts: Writes[Session], hc: HeaderCarrier): Future[Unit]        =
    saveOrUpdate(data)
  def saveOrUpdate(data: Session)(implicit wts: Writes[Session], hc: HeaderCarrier): Future[Unit] =
    Mdc.preservingMdc {
      for {
        sessionId <- getSessionId
        _         <- collection
                       .findOneAndUpdate(
                         filter = Filters.equal("_id", sessionId),
                         update = Updates.combine(
                           Updates.set(s"data", Codecs.toBson(SensitiveSession(data))),
                           Updates.set("createdAt", Instant.now)
                         ),
                         options = FindOneAndUpdateOptions().upsert(true)
                       )
                       .toFuture()
      } yield (
      )
    }

  def get(implicit rds: Reads[Session], hc: HeaderCarrier) =
    Mdc.preservingMdc {
      for {
        sessionId   <- getSessionId
        maybeOption <- collection.find(Filters.equal("_id", sessionId)).headOption()
      } yield maybeOption.map(
        _.data.decryptedValue
      )
    }

  def findSession(implicit hc: HeaderCarrier): Future[SessionData] =
    Mdc.preservingMdc {
      for {
        sessionId <- getSessionId
        session   <- collection.find(Filters.equal("_id", sessionId)).first().toFuture()
      } yield session.decryptedValue
    }

  def remove()(implicit hc: HeaderCarrier): Future[Unit] =
    Mdc.preservingMdc {
      for {
        sessionId <- getSessionId
        _         <- collection.deleteOne(equal("_id", sessionId)).toFuture()
      } yield ()
    }

  def removeAll()(implicit hc: HeaderCarrier): Future[Unit] =
    Mdc.preservingMdc {
      for {
        sessionId <- getSessionId
        _         <- collection.deleteMany(equal("_id", sessionId)).toFuture()
      } yield ()
    }

  private val noSession = Future.failed[String](NoSessionException)

  private def getSessionId(implicit hc: HeaderCarrier): Future[String] =
    hc.sessionId.fold(noSession)(c => Future.successful(c.value))

}

case object NoSessionException extends Exception("Could not find sessionId in HeaderCarrier")

case class SessionData(_id: String, data: Session, createdAt: Instant = Instant.now)

object SessionData {

  implicit val formatInstant: Format[Instant] = MongoJavatimeFormats.instantFormat
  val format: OFormat[SessionData]            = Json.format
}

case class SensitiveSessionData(_id: String, data: SensitiveSession, createdAt: Instant = Instant.now)
    extends Sensitive[SessionData] {
  override def decryptedValue: SessionData = SessionData(
    _id,
    data.decryptedValue,
    createdAt
  )
}

object SensitiveSessionData {

  implicit val formatInstant: Format[Instant]                                      = MongoJavatimeFormats.instantFormat
  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveSessionData] = Json.format

  def apply(sessionData: SessionData): SensitiveSessionData = SensitiveSessionData(
    sessionData._id,
    SensitiveSession(sessionData.data),
    sessionData.createdAt
  )
}

trait SessionRepo {

  def start(data: Session)(implicit wts: Writes[Session], hc: HeaderCarrier): Future[Unit]

  def saveOrUpdate(data: Session)(implicit wts: Writes[Session], hc: HeaderCarrier): Future[Unit]

  def get(implicit rds: Reads[Session], hc: HeaderCarrier): Future[Option[Session]]

  def remove()(implicit hc: HeaderCarrier): Future[Unit]
}
