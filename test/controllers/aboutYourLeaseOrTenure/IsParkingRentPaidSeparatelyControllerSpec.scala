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

import form.aboutYourLeaseOrTenure.IsParkingRentPaidSeparatelyForm.isParkingRentPaidSeparatelyForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec
import scala.language.reflectiveCalls

/**
  * @author Yuriy Tumakha
  */
class IsParkingRentPaidSeparatelyControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._
  def isParkingRentPaidSeparatelyController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new IsParkingRentPaidSeparatelyController(
    isParkingRentPaidSeparatelyView,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "IsParkingRentPaidSeparatelyController GET /" should {
    "return 200 and HTML with is parking rent paid separately is present in session" in {
      val result = isParkingRentPaidSeparatelyController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IncludedInRentParkingSpacesController.show().url
      )
    }

    "return 200 and HTML is parking rent paid separately is none in session" in {
      val controller = isParkingRentPaidSeparatelyController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.DoesRentIncludeParkingController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = isParkingRentPaidSeparatelyController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }

  }

  "IsParkingRentPaidSeparatelyController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = isParkingRentPaidSeparatelyController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "isParkingRentPaidSeparately" should {
    "error if isParkingRentPaidSeparately answer is missing" in {
      val formData = baseFormData - errorKey.isParkingRentPaidSeparately
      val form     = isParkingRentPaidSeparatelyForm.bind(formData)

      mustContainError(errorKey.isParkingRentPaidSeparately, "error.isParkingRentPaidSeparately.required", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val isParkingRentPaidSeparately: String
    } = new {
      val isParkingRentPaidSeparately: String = "isParkingRentPaidSeparately"
    }

    val baseFormData: Map[String, String] = Map("isParkingRentPaidSeparately" -> "yes")
  }

}
