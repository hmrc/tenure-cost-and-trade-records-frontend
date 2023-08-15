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
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartTwo}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import models.submissions.aboutyouandtheproperty._
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import models.submissions.additionalinformation.AdditionalInformation
import models.submissions.connectiontoproperty.StillConnectedDetails
import models.submissions.notconnected.RemoveConnectionDetails
import models.Session
import models.submissions.downloadFORTypeForm.DownloadPDFDetails
import org.mockito.scalatest.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Inside, OptionValues}
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
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits, Injecting}
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
    with Injecting
    with GlobalExecutionContext
    with RepositoryUtils
    with FakeObjects
    with FakeViews
    with FakeNavigation
    with OptionValues {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false,
        "app.username" -> "validUsername",
        "app.password" -> "validPassword"
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

  val preFilledSession: WithSessionRefiner =
    preEnrichedActionRefiner(
      Some(prefilledStillConnectedDetailsYes),
      Some(prefilledRemoveConnection),
      Some(prefilledAboutYouAndThePropertyNo)
    )

  def preEnrichedActionRefiner(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll),
    removeConnectionDetails: Option[RemoveConnectionDetails] = Some(prefilledRemoveConnection),
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo),
    additionalInformation: Option[AdditionalInformation] = Some(prefilledAdditionalInformation),
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutTheTradingHistory),
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings),
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne),
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo),
    downloadPDFDetails: Option[DownloadPDFDetails] = None
  ): WithSessionRefiner =
    new WithSessionRefiner(mockCustomErrorHandler, mockSessionRepository) {

      override def refine[A](request: Request[A]): Future[Either[Result, SessionRequest[A]]] =
        Future.successful(
          Right(
            SessionRequest[A](
              Session(
                "99996010004",
                "FOR6010",
                prefilledAddress,
                "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
                stillConnectedDetails = stillConnectedDetails,
                removeConnectionDetails = removeConnectionDetails,
                aboutYouAndTheProperty = aboutYouAndTheProperty,
                additionalInformation = additionalInformation,
                aboutTheTradingHistory = aboutTheTradingHistory,
                aboutFranchisesOrLettings = aboutFranchisesOrLettings,
                aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne,
                aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo,
                downloadPDFDetails = downloadPDFDetails
              ),
              request = request
            )
          )
        )
    }
}
