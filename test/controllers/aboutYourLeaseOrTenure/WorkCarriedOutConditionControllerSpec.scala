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
import form.aboutYourLeaseOrTenure.WorkCarriedOutConditionForm.workCarriedOutConditionForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class WorkCarriedOutConditionControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def workCarriedOutConditionController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new WorkCarriedOutConditionController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      workCarriedOutConditionView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  def workCarriedOutConditionControllerNo(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThreeNo
    )
  ) =
    new WorkCarriedOutConditionController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      workCarriedOutConditionView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  def workCarriedOutConditionControllerEmpty(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = None
  ) = new WorkCarriedOutConditionController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    workCarriedOutConditionView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = workCarriedOutConditionController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = workCarriedOutConditionController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 No" in {
      val result = workCarriedOutConditionControllerNo().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML No" in {
      val result = workCarriedOutConditionControllerNo().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 for empty session" in {
      val result = workCarriedOutConditionControllerEmpty().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = workCarriedOutConditionController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "error if choice is missing" in {
      val formData = baseFormData - errorKey.workCarriedOutCondition
      val form     = workCarriedOutConditionForm.bind(formData)

      mustContainError(
        errorKey.workCarriedOutCondition,
        "error.workCarriedOutCondition.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: Object {
      val workCarriedOutCondition: String
    } = new {
      val workCarriedOutCondition: String = "workCarriedOutCondition"
    }

    val baseFormData: Map[String, String] = Map(
      "workCarriedOutCondition" -> "Test content"
    )
  }
}
