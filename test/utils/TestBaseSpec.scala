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

package utils

import actions.{SessionRequest, WithSessionRefiner}
import config.{AppConfig, ErrorHandler}
import models.submissions.abouttheproperty._
import models.submissions.aboutyou.{AboutYou, CustomerDetails}
import models.submissions.common.{Address, ContactDetails}
import models.submissions.connectiontoproperty.{AddressConnectionTypeYes, StillConnectedDetails}
import models.{Session, UserLoginDetails}
import org.mockito.scalatest.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Inside}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, Request, Result}
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}
import repositories.SessionRepository
import repository.RepositoryUtils
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

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

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(20, Millis))

  def injector: Injector = app.injector

  def frontendAppConfig: AppConfig = injector.instanceOf[AppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  def messages: Messages = messagesApi.preferred(fakeRequest)

  def servicesConfig: ServicesConfig = app.injector.instanceOf[ServicesConfig]

  implicit def ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("my-session")))
  implicit val clock: Clock      = Clock.fixed(Instant.now(), ZoneId.systemDefault())

  val mockCustomErrorHandler: ErrorHandler     = mock[ErrorHandler]
  val mockSessionRepository: SessionRepository = mock[SessionRepository]
  val testAddress                              = Address("001", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), "BN12 4AX")
  val testUserLoginDetails                     =
    UserLoginDetails("jwtToken", "FOR6010", "123456", Address("13", Some("Street"), Some("City"), "AA11 1AA"))
  val testStillConnectedDetailsYes             = StillConnectedDetails(Some(AddressConnectionTypeYes))
  val testAboutYou                             = AboutYou(Some(CustomerDetails("Tobermory", ContactDetails("12345678909", "test@email.com"))))
  val testAboutThePropertyNo                   = AboutTheProperty(
      Some(PropertyDetails("OccupierName", CurrentPropertyHotel, None)),
      Some(WebsiteForPropertyDetails(BuildingOperationHaveAWebsiteYes, Some("webAddress"))),
      Some(LicensableActivitiesNo), None,
      Some(PremisesLicensesConditionsNo), None,
      Some(EnforcementActionsNo), None,
      Some(TiedGoodsNo), None
    )
  val preFilledSession                         = preEnrichedActionRefiner(testUserLoginDetails, testStillConnectedDetailsYes, testAboutYou, testAboutThePropertyNo)

  def preEnrichedActionRefiner(
    userLoginDetails: UserLoginDetails,
    stillConnectedDetailsYes: StillConnectedDetails,
    aboutYou: AboutYou,
    aboutTheProperty: AboutTheProperty
  ): WithSessionRefiner =
    new WithSessionRefiner(mockCustomErrorHandler, mockSessionRepository) {

      override def refine[A](request: Request[A]): Future[Either[Result, SessionRequest[A]]] =
        Future.successful(
          Right(
            SessionRequest[A](
              Session(
                userLoginDetails = userLoginDetails,
                stillConnectedDetails = Some(stillConnectedDetailsYes),
                aboutYou = Some(aboutYou),
                aboutTheProperty = Some(aboutTheProperty)
              ),
              request = request
            )
          )
        )
    }
}
