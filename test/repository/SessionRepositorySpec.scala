/*
 * Copyright 2026 HM Revenue & Customs
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
import repositories.{SensitiveSessionData, SessionRepository}
import test.TCTRMongoSpec
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}

class SessionRepositorySpec extends TCTRMongoSpec[SensitiveSessionData, SessionRepository]:

  given headerCarrier: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("test-session-id")))

  override protected def beforeEach(): Unit =
    super.beforeEach()
    mongoRepository.removeAll().futureValue

  "SessionRepository" should {
    "start by saving or updating data" in {
      mongoRepository.start(baseFilled6010Session).futureValue

      val returnedSessionData = mongoRepository.findSession.futureValue

      returnedSessionData.data.referenceNumber shouldBe baseFilled6010Session.referenceNumber
    }

    "get data from current session" in {
      mongoRepository.start(baseFilled6010Session).futureValue

      val returnedSessionDataOpt = mongoRepository.get.futureValue

      returnedSessionDataOpt.get.referenceNumber shouldBe referenceNumber
    }

    "remove data from current session" in {
      mongoRepository.start(baseFilled6010Session).futureValue
      mongoRepository.remove().futureValue

      val returnedSessionDataOpt = mongoRepository.get.futureValue

      returnedSessionDataOpt shouldBe None
    }
  }
