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

import form.aboutYourLeaseOrTenure.PropertyUseLeasebackArrangementForm.propertyUseLeasebackArrangementForm
import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class PropertyUseLeasebackArrangementControllerSpec extends TestBaseSpec {
  import TestData._
  def propertyUseLeasebackAgreementController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new PropertyUseLeasebackArrangementController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      propertyUseLeasebackAgreementView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def propertyUseLeasebackAgreementController6020Yes(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new PropertyUseLeasebackArrangementController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      propertyUseLeasebackAgreementView,
      preEnrichedActionRefiner(forType = ForTypes.for6020, aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def propertyUseLeasebackAgreementController6020No(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOneNo)
  ) =
    new PropertyUseLeasebackArrangementController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      propertyUseLeasebackAgreementView,
      preEnrichedActionRefiner(forType = ForTypes.for6020, aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def propertyUseLeasebackAgreementControllerNone = new PropertyUseLeasebackArrangementController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    propertyUseLeasebackAgreementView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = None),
    mockSessionRepo
  )

  "PropertyUseLeasebackArrangementController GET /" should {
    "return 200 with data in the session" in {
      val result = propertyUseLeasebackAgreementController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML with data in the session" in {
      val result = propertyUseLeasebackAgreementController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and html 6020 yes data in the session" in {
      val result = propertyUseLeasebackAgreementController6020Yes().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and html 6020 no data in the session" in {
      val result = propertyUseLeasebackAgreementController6020No().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and html with none in the session" in {
      val result = propertyUseLeasebackAgreementControllerNone.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "PropertyUseLeasebackArrangementController POST /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = propertyUseLeasebackAgreementController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Trading Name own the property form" should {
      "error if areYouThirdParty is missing" in {
        val formData = baseFormData - errorKey.propertyUseLeasebackArrangement
        val form     = propertyUseLeasebackArrangementForm.bind(formData)

        mustContainError(
          errorKey.propertyUseLeasebackArrangement,
          "error.propertyUseLeasebackArrangement.missing",
          form
        )
      }
    }
  }

  object TestData {
    val errorKey = new {
      val propertyUseLeasebackArrangement = "propertyUseLeasebackArrangement"
    }

    val baseFormData: Map[String, String] = Map(
      "propertyUseLeasebackArrangement" -> "yes"
    )
  }
}
