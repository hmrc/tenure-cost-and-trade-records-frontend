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
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

trait FakeNavigation { this: GuiceOneAppPerSuite =>

  implicit def implicitRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  lazy val requestReferenceNumberNavigator    = app.injector.instanceOf[RequestReferenceNumberNavigator]
  lazy val connectedToPropertyNavigator       = app.injector.instanceOf[ConnectionToPropertyNavigator]
  lazy val removeConnectionNavigator          = app.injector.instanceOf[RemoveConnectionNavigator]
  lazy val aboutYouAndThePropertyNavigator    = app.injector.instanceOf[AboutYouAndThePropertyNavigator]
  lazy val aboutYourTradingHistoryNavigator   = app.injector.instanceOf[AboutTheTradingHistoryNavigator]
  lazy val aboutFranchisesOrLettingsNavigator = app.injector.instanceOf[AboutFranchisesOrLettingsNavigator]
  lazy val aboutYourLeaseOrTenureNavigator    = app.injector.instanceOf[AboutYourLeaseOrTenureNavigator]
  lazy val additionalInformationNavigator     = app.injector.instanceOf[AdditionalInformationNavigator]

}
