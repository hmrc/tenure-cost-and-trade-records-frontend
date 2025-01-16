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

package controllers.additionalinformation

import models.submissions.additionalinformation.AdditionalInformation
import connectors.Audit
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class FurtherInformationOrRemarksControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def furtherInformationOrRemarksController(
    additionalInformation: Option[AdditionalInformation] = Some(prefilledAdditionalInformation)
  ) = new FurtherInformationOrRemarksController(
    stubMessagesControllerComponents(),
    mockAudit,
    additionalInformationNavigator,
    furtherInformationOrRemarksView,
    preEnrichedActionRefiner(additionalInformation = additionalInformation),
    mockSessionRepo
  )

  def furtherInformationOrRemarksControllerEmpty(
    additionalInformation: Option[AdditionalInformation] = None
  ) = new FurtherInformationOrRemarksController(
    stubMessagesControllerComponents(),
    mockAudit,
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

  "GET / empty additional info" should {
    "return html with 200" in {
      val result = furtherInformationOrRemarksControllerEmpty().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "return 303 if an empty form is submitted as field is optional" in {
      val result = furtherInformationOrRemarksController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(result) shouldBe SEE_OTHER
    }

    "return 400 if textarea value length is more then 2000" in {
      val maxlengthErrorData = Seq(
        "furtherInformationOrRemarks" -> "x" * 2001
      )
      val result             = furtherInformationOrRemarksController().submit(
        FakeRequest().withFormUrlEncodedBody(maxlengthErrorData*)
      )
      status(result) shouldBe SEE_OTHER
    }

  }
}
