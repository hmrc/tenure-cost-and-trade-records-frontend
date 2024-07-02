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

import form.aboutYourLeaseOrTenure.PropertyUpdatesForm.propertyUpdatesForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class PropertyUpdatesControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def propertyUpdateController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new PropertyUpdatesController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      propertyUpdatesView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  "PropertyUpdatesController GET /" should {
    "return 200 and HTML with property updates in the session" in {
      val result = propertyUpdateController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show().url
      )
    }

    "return 200 and HTML property updates with none in the session" in {
      val controller = propertyUpdateController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show().url
      )
    }
  }

  "PropertyUpdatesController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = propertyUpdateController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Property updates form" should {
    "error an answer is missing" in {
      val formData = baseFormData - errorKey.propertyUpdates
      val form     = propertyUpdatesForm.bind(formData)

      mustContainError(errorKey.propertyUpdates, "error.propertyUpdates.missing", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val propertyUpdates: String
    } = new {
      val propertyUpdates: String = "propertyUpdates"
    }

    val baseFormData: Map[String, String] = Map("propertyUpdates" -> "yes")
  }
}
