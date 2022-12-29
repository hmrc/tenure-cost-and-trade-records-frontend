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

package controllers.Form6010

import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.form.{enforcementActionBeenTaken, premisesLicenseConditions}

class PremisesLicenseConditionsControllerSpec extends TestBaseSpec {

  val mockPremisesLicenseConditionsView = mock[premisesLicenseConditions]
  when(mockPremisesLicenseConditionsView.apply(any)(any, any)).thenReturn(HtmlFormat.empty)

  val premisesLicenseConditionsController = new PremisesLicenseConditionsController(
    stubMessagesControllerComponents(),
    mock[enforcementActionBeenTaken],
    mockPremisesLicenseConditionsView,
    preFilledSession,
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = premisesLicenseConditionsController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = premisesLicenseConditionsController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }
}
