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

package utils

import actions.{SessionRequest, WithSessionRefiner}
import config.AppConfig
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartFour, AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import models.submissions.aboutyouandtheproperty.*
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne}
import models.submissions.additionalinformation.AdditionalInformation
import models.submissions.connectiontoproperty.StillConnectedDetails
import models.submissions.notconnected.RemoveConnectionDetails
import models.ForType.*
import models.{ForType, Session}
import models.submissions.downloadFORTypeForm.DownloadPDFDetails
import models.submissions.lettingHistory.LettingHistory
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails
import org.scalatest.{Inside, OptionValues}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.request.RequestTarget
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
    with CustomMatchers
    with FutureAwaits
    with DefaultAwaitTimeout
    with MockitoExtendedSugar
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
        "metrics.jvm"                         -> false,
        "metrics.enabled"                     -> false,
        "create-internal-auth-token-on-start" -> false,
        "urls.tctrFrontend"                   -> "someUrl"
      )
      .build()

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(20, Millis))

  def frontendAppConfig: AppConfig = inject[AppConfig]

  def messagesApi: MessagesApi = inject[MessagesApi]

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  val fakeGetRequest = fakeRequest

  val fakePostRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "/")

  val fakeRequestFromCYA: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withTarget(
    RequestTarget("", "", Map("from" -> Seq("CYA")))
  )

  val fakeRequestFromTL: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withTarget(
    RequestTarget("", "", Map("from" -> Seq("TL")))
  )

  val fakeRequestFromIES: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withTarget(
    RequestTarget("", "", Map("from" -> Seq("IES")))
  )

  def messages: Messages = messagesApi.preferred(fakeRequest)

  def servicesConfig: ServicesConfig = inject[ServicesConfig]

  def requestWithQueryParam[A](fakeRequest: FakeRequest[A], queryParam: String): FakeRequest[A] =
    FakeRequest(fakeRequest.method, s"${fakeRequest.uri}?$queryParam", fakeRequest.headers, fakeRequest.body)

  implicit def ec: ExecutionContext = inject[ExecutionContext]

  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("my-session")))
  implicit val clock: Clock      = Clock.fixed(Instant.now(), ZoneId.systemDefault())

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  val preFilledSession: WithSessionRefiner =
    preEnrichedActionRefiner(
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYes),
      removeConnectionDetails = Some(prefilledRemoveConnection),
      aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo)
    )

  val preFilledSession6015: WithSessionRefiner =
    preEnrichedActionRefiner(
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYes),
      removeConnectionDetails = Some(prefilledRemoveConnection),
      aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo),
      forType = FOR6015
    )

  val preFilledSession6020: WithSessionRefiner =
    preEnrichedActionRefiner(
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYes),
      removeConnectionDetails = Some(prefilledRemoveConnection),
      aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo),
      forType = FOR6020
    )

  val preFilledSession6076: WithSessionRefiner =
    preEnrichedActionRefiner(
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYes),
      removeConnectionDetails = Some(prefilledRemoveConnection),
      aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo),
      forType = FOR6076
    )

  val preFilledSessionNone: WithSessionRefiner =
    preEnrichedActionRefiner(
      stillConnectedDetails = None,
      removeConnectionDetails = None,
      aboutYouAndTheProperty = None
    )

  val preFilledSession6045: WithSessionRefiner =
    preEnrichedActionRefiner(
      referenceNumber = "99996045004",
      aboutTheTradingHistory = prefilledAboutYourTradingHistory6045,
      aboutTheTradingHistoryPartOne = prefilledAboutTheTradingHistoryPartOneCYA6045,
      forType = FOR6045
    )

  val preFilledSession6048: WithSessionRefiner =
    preEnrichedActionRefiner(
      referenceNumber = "99996048004",
      aboutTheTradingHistory = prefilledAboutYourTradingHistory6048,
      aboutTheTradingHistoryPartOne = prefilledAboutTheTradingHistoryPartOneCYA6048,
      forType = FOR6048
    )

  def preEnrichedActionRefiner(
    referenceNumber: String = "99996010004",
    forType: ForType = FOR6010,
    isWelsh: Boolean = false,
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll),
    removeConnectionDetails: Option[RemoveConnectionDetails] = Some(prefilledRemoveConnection),
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo),
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Some(prefilledAboutYouAndThePropertyPartTwo),
    additionalInformation: Option[AdditionalInformation] = Some(prefilledAdditionalInformation),
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutTheTradingHistory),
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(prefilledAboutTheTradingHistoryPartOne),
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings),
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne),
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo),
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    ),
    aboutLeaseOrAgreementPartFour: Option[AboutLeaseOrAgreementPartFour] = Some(
      prefilledAboutLeaseOrAgreementPartFour
    ),
    requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = Some(prefilledRequestRefNumCYA),
    downloadPDFDetails: Option[DownloadPDFDetails] = None,
    lettingHistory: Option[LettingHistory] = None
  ): WithSessionRefiner =
    new WithSessionRefiner(mockSessionRepository) {

      override def refine[A](request: Request[A]): Future[Either[Result, SessionRequest[A]]] =
        Future.successful(
          Right(
            SessionRequest[A](
              Session(
                referenceNumber,
                forType,
                prefilledAddress,
                "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
                isWelsh = isWelsh,
                stillConnectedDetails = stillConnectedDetails,
                removeConnectionDetails = removeConnectionDetails,
                aboutYouAndTheProperty = aboutYouAndTheProperty,
                aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo,
                additionalInformation = additionalInformation,
                aboutTheTradingHistory = aboutTheTradingHistory,
                aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne,
                aboutFranchisesOrLettings = aboutFranchisesOrLettings,
                aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne,
                aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo,
                aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree,
                aboutLeaseOrAgreementPartFour = aboutLeaseOrAgreementPartFour,
                requestReferenceNumberDetails = requestReferenceNumberDetails,
                downloadPDFDetails = downloadPDFDetails,
                lettingHistory = lettingHistory
              ),
              request = request
            )
          )
        )
    }
}
