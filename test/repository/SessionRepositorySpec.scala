/*
 * Copyright 2023 HM Revenue & Customs
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

import models.Session
import repositories.SessionData
import repositories.{SessionRepository => SessionRepo}
import utils.TestBaseSpec

class SessionRepositorySpec extends TestBaseSpec {

  lazy val repository = app.injector.instanceOf[SessionRepo]

  "session repository" should {

    "start by saving or updating data" in {

      repository.start(baseFilled6010Session).futureValue

      val returnedSessionData: SessionData = repository.findFirst.futureValue // shouldBe session

      inside(returnedSessionData) { case SessionData(_, data, createdAt) =>
        (data.referenceNumber) shouldBe baseFilled6010Session.referenceNumber
      }
    }

    "get data from current session" in {
      repository.start(baseFilled6010Session).futureValue

      val returnedSessionData: Option[Session] = repository.get.futureValue

      inside(returnedSessionData) { case Some(session) =>
        session.referenceNumber shouldBe referenceNumber
      }

    }

    "remove data from current session" in {
      repository.start(baseFilled6010Session).futureValue
      repository.remove().futureValue

      val returnedSessionData = repository.get.futureValue

      returnedSessionData shouldBe None

    }
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    repository.removeAll().futureValue
  }

}
