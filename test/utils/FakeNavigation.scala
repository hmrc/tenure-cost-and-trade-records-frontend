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

import navigation._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.{FakeRequest, Injecting}

trait FakeNavigation { this: Injecting =>

  implicit def implicitRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  lazy val requestReferenceNumberNavigator    = inject[RequestReferenceNumberNavigator]
  lazy val connectedToPropertyNavigator       = inject[ConnectionToPropertyNavigator]
  lazy val removeConnectionNavigator          = inject[RemoveConnectionNavigator]
  lazy val aboutYouAndThePropertyNavigator    = inject[AboutYouAndThePropertyNavigator]
  lazy val aboutYourTradingHistoryNavigator   = inject[AboutTheTradingHistoryNavigator]
  lazy val aboutFranchisesOrLettingsNavigator = inject[AboutFranchisesOrLettingsNavigator]
  lazy val aboutYourLeaseOrTenureNavigator    = inject[AboutYourLeaseOrTenureNavigator]
  lazy val additionalInformationNavigator     = inject[AdditionalInformationNavigator]

}
