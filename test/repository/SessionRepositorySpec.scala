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

package repository

import play.api.libs.json.{Json, Reads, Writes}
import models.Session
import models.submissions.Form6010.AddressConnectionTypeYes
import repositories.SessionData
import repositories.{Session => SessionRepo}
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import utils.TestBaseSpec

class SessionRepositorySpec extends TestBaseSpec {

  lazy val repository   = app.injector.instanceOf[SessionRepo]
  val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("my-session")))
  val writes            = implicitly[Writes[Session]]
  val reads             = implicitly[Reads[Session]]

  "session repository" should {

    "start by saving or updating data" in {
      val session = Session(AddressConnectionTypeYes)

      repository.start(session)(writes, hc).futureValue

      val returnedSessionData: SessionData = repository.findFirst.futureValue // shouldBe session

      inside(returnedSessionData) { case SessionData(_, data, createdAt) =>
        (data \ "session" \ "areYouStillConnected").as[String] shouldBe session.areYouStillConnected.name
      }
    }

    "get data from current session" in {
      val session = Session(AddressConnectionTypeYes)
      repository.start(session)(writes, hc).futureValue

      val returnedSessionData: Option[Session] = repository.get[Session](reads, hc).futureValue

      inside(returnedSessionData) { case Some(Session(areYouStillConnected, None, None)) =>
        areYouStillConnected.name shouldBe AddressConnectionTypeYes.name
      }

    }

    "remove data from current session" in {
      val session = Session(AddressConnectionTypeYes)
      repository.start(session)(writes, hc).futureValue
      repository.remove()(hc).futureValue

      val returnedSessionData: Option[Session] = repository.get[Session](reads, hc).futureValue

      returnedSessionData shouldBe None

    }
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    repository.removeAll()(hc).futureValue
  }

}
