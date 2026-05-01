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

package test

import actions.{SessionRequest, WithSessionRefiner}
import config.AppConfig
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartFour, AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne}
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo}
import models.submissions.accommodation.AccommodationDetails
import models.submissions.additionalinformation.AdditionalInformation
import models.submissions.connectiontoproperty.StillConnectedDetails
import models.submissions.lettingHistory.LettingHistory
import models.submissions.notconnected.RemoveConnectionDetails
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails
import models.{ForType, Session}
import play.api.mvc.{Request, Result}
import repositories.SessionRepository
import uk.gov.hmrc.vo.unit.test.BaseAppSpec

import scala.concurrent.Future

/**
  * @author Yuriy Tumakha
  */
abstract class TCTRAppSpec extends BaseAppSpec with TCTRApplicationFactory with InjectedViews:

  val appConfig: AppConfig = inject[AppConfig]

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

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
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(prefilledAboutLeaseOrAgreementPartThree),
    aboutLeaseOrAgreementPartFour: Option[AboutLeaseOrAgreementPartFour] = Some(prefilledAboutLeaseOrAgreementPartFour),
    requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = Some(prefilledRequestRefNumCYA),
    lettingHistory: Option[LettingHistory] = None,
    accommodationDetails: Option[AccommodationDetails] = None
  ): WithSessionRefiner =
    new WithSessionRefiner(mockSessionRepository):
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
                lettingHistory = lettingHistory,
                accommodationDetails = accommodationDetails
              ),
              request = request
            )
          )
        )
