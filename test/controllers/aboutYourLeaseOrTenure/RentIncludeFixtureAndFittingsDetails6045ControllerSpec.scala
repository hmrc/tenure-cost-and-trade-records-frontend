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

package controllers.aboutYourLeaseOrTenure

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class RentIncludeFixtureAndFittingsDetails6045ControllerSpec extends TestBaseSpec {

  def rentIncludeFixtureAndFittingsDetails6045Controller(
    forType: String = forType6045,
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new RentIncludeFixtureAndFittingsDetailsController(
    stubMessagesControllerComponents(),
    inject[AboutYourLeaseOrTenureNavigator],
    rentIncludeFixtureAndFittingsDetailsView,
    rentIncludeFixtureAndFittingsDetailsTextAreaView,
    preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo
  )

  "RentIncludeTradeServicesDetailsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = rentIncludeFixtureAndFittingsDetails6045Controller().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
