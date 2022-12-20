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

package utils

import actions.{SessionRequest, WithSessionRefiner}
import config.ErrorHandler
import models.submissions.Form6010.{Address, AddressConnectionTypeYes}
import models.submissions.StillConnectedDetails
import models.{Session, UserLoginDetails}
import org.mockito.scalatest.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Inside}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.{Request, Result}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import repositories.SessionRepository
import repository.RepositoryUtils
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}

import java.time.{Clock, Instant, ZoneId}
import scala.concurrent.{ExecutionContext, Future}

trait TestBaseSpec
    extends AnyWordSpec
    with Matchers
    with FutureAwaits
    with DefaultAwaitTimeout
    with BeforeAndAfterEach
    with MockitoSugar
    with ScalaFutures
    with Inside
    with GuiceOneAppPerSuite
    with GlobalExecutionContext
    with RepositoryUtils {

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(20, Millis))

  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
  implicit val hc: HeaderCarrier    = HeaderCarrier(sessionId = Some(SessionId("my-session")))
  implicit val clock: Clock         = Clock.fixed(Instant.now(), ZoneId.systemDefault())

  val mockCustomErrorHandler: ErrorHandler     = mock[ErrorHandler]
  val mockSessionRepository: SessionRepository = mock[SessionRepository]
  val testUserLoginDetails                     =
    UserLoginDetails("jwtToken", "FOR6010", "123456", Address("13", Some("Street"), Some("City"), "AA11 1AA"))
  val testStillConnectedDetailsYes             = StillConnectedDetails(Some(AddressConnectionTypeYes))
  val preFilledSession                         = preEnrichedActionRefiner(testUserLoginDetails, testStillConnectedDetailsYes)

  def preEnrichedActionRefiner(
    userLoginDetails: UserLoginDetails,
    stillConnectedDetailsYes: StillConnectedDetails
  ): WithSessionRefiner =
    new WithSessionRefiner(mockCustomErrorHandler, mockSessionRepository) {

      override def refine[A](request: Request[A]): Future[Either[Result, SessionRequest[A]]] =
        Future.successful(
          Right(
            SessionRequest[A](
              Session(
                userLoginDetails = userLoginDetails,
                stillConnectedDetails = Some(stillConnectedDetailsYes)
              ),
              request = request
            )
          )
        )
    }
}
