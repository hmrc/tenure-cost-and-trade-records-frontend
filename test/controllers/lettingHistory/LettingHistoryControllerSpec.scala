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
import models.submissions.lettingHistory.{LettingHistory, ResidentDetail}
import org.mockito.ArgumentCaptor
import play.api.libs.json.Writes
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.Future.successful

class LettingHistoryControllerSpec extends TestBaseSpec:

  val oneResident = List(
    ResidentDetail(name = "Mr. One", address = "Address One")
  )

  val twoResidents = oneResident ++ List(
    ResidentDetail(name = "Mr. Two", address = "Address Two")
  )

  val fiveResidents = twoResidents ++ List(
    ResidentDetail(name = "Miss Three", address = "Address Three"),
    ResidentDetail(name = "Mr. Four", address = "Address Four"),
    ResidentDetail(name = "Mrs. Five", address = "Address Five")
  )

  trait MockRepositoryFixture:
    val repository = mock[SessionRepo]
    val data       = captor[Session]
    when(repository.saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])).thenReturn(successful(()))

  trait SessionCapturingFixture:
    def hasPermanentResidents(session: ArgumentCaptor[Session]) =
      LettingHistory.hasPermanentResidents(session.getValue)

    def permanentResidentAt(session: ArgumentCaptor[Session], index: Integer): Option[ResidentDetail] =
      LettingHistory.permanentResidents(session.getValue).lift(index)

    def permanentResidents(session: ArgumentCaptor[Session]): List[ResidentDetail] =
      LettingHistory.permanentResidents(session.getValue)

    def hasCompletedLettings(session: ArgumentCaptor[Session]) =
      LettingHistory.hasCompletedLettings(session.getValue)
