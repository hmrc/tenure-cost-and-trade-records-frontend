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

import form.aboutYourLeaseOrTenure.UltimatelyResponsibleOutsideRepairsForm.ultimatelyResponsibleOutsideRepairsForm
import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class UltimatelyResponsibleOutsideRepairsControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  val mockAboutYourLeaseOrTenureNavigator = mock[AboutYourLeaseOrTenureNavigator]

  def ultimatelyResponsibleOutsideRepairsController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new UltimatelyResponsibleOutsideRepairsController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      ultimatelyResponsibleOutsideRepairsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  def ultimatelyResponsibleOutsideRepairsController6020(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new UltimatelyResponsibleOutsideRepairsController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      ultimatelyResponsibleOutsideRepairsView,
      preEnrichedActionRefiner(forType = ForTypes.for6020, aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  def ultimatelyResponsibleOutsideRepairsControllerNone =
    new UltimatelyResponsibleOutsideRepairsController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      ultimatelyResponsibleOutsideRepairsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = None),
      mockSessionRepo
    )

  "UltimatelyResponsibleOutsideRepairsController GET /" should {
    "return 200 and HTML with Ultimately Responsible Outside Repairs in the session" in {
      val result = ultimatelyResponsibleOutsideRepairsController().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML with Ultimately Responsible Outside Repairs 6020 in the session" in {
      val result = ultimatelyResponsibleOutsideRepairsController6020().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when no Ultimately Responsible Outside Repairs in the session" in {
      val result = ultimatelyResponsibleOutsideRepairsControllerNone.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = ultimatelyResponsibleOutsideRepairsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Ultimately Responsible OR form" should {
    "error if Ultimately Responsible OR is missing" in {
      val formData = baseFormData - errorKey.ultimatelyResponsibleOR
      val form     = ultimatelyResponsibleOutsideRepairsForm.bind(formData)

      mustContainError(errorKey.ultimatelyResponsibleOR, "error.outsideRepairs.required", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val ultimatelyResponsibleOR: String
    } = new {
      val ultimatelyResponsibleOR: String = "outsideRepairs"
    }

    val baseFormData: Map[String, String] = Map("outsideRepairs" -> "tenant")
  }
}
