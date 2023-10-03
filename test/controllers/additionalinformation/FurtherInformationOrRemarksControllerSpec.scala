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

package controllers.additionalinformation

import models.submissions.additionalinformation.AdditionalInformation
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class FurtherInformationOrRemarksControllerSpec extends TestBaseSpec {

  def furtherInformationOrRemarksController(
    additionalInformation: Option[AdditionalInformation] = Some(prefilledAdditionalInformation)
  ) = new FurtherInformationOrRemarksController(
    stubMessagesControllerComponents(),
    additionalInformationNavigator,
    furtherInformationOrRemarksView,
    preEnrichedActionRefiner(additionalInformation = additionalInformation),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = furtherInformationOrRemarksController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = furtherInformationOrRemarksController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "return 303 if an empty form is submitted as field is optional" in {
      val result = furtherInformationOrRemarksController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(result) shouldBe SEE_OTHER
    }
  }
}
