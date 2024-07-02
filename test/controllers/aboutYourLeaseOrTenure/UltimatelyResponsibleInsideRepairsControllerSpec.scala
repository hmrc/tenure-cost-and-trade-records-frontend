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

package controllers.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.UltimatelyResponsibleInsideRepairsForm.ultimatelyResponsibleInsideRepairsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class UltimatelyResponsibleInsideRepairsControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  val mockAboutYourLeaseOrTenureNavigator = mock[AboutYourLeaseOrTenureNavigator]

  def ultimatelyResponsibleInsideRepairsController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new UltimatelyResponsibleInsideRepairsController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      ultimatelyResponsibleInsideRepairsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  def ultimatelyResponsibleInsideRepairsControllerNone =
    new UltimatelyResponsibleInsideRepairsController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      ultimatelyResponsibleInsideRepairsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = None),
      mockSessionRepo
    )

  "UltimatelyResponsibleInsideRepairsController GET /" should {
    "return 200 and HTML with Ultimately Responsible Inside Repairs in the session" in {
      val result = ultimatelyResponsibleInsideRepairsController().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when no Ultimately Responsible Inside Repairs in the session" in {
      val result = ultimatelyResponsibleInsideRepairsControllerNone.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = ultimatelyResponsibleInsideRepairsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Ultimately Responsible IR form" should {
    "error if Ultimately Responsible IR is missing" in {
      val formData = baseFormData - errorKey.ultimatelyResponsibleIR
      val form     = ultimatelyResponsibleInsideRepairsForm.bind(formData)

      mustContainError(errorKey.ultimatelyResponsibleIR, "error.insideRepairs.required", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val ultimatelyResponsibleIR: String
    } = new {
      val ultimatelyResponsibleIR: String = "insideRepairs"
    }

    val baseFormData: Map[String, String] = Map("insideRepairs" -> "tenant")
  }
}
