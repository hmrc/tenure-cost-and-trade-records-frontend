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

package controllers.aboutfranchisesorlettings

import form.aboutfranchisesorlettings.CateringOperationForm.cateringOperationForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class CateringOperationControllerSpec extends TestBaseSpec {

  import TestData._

  def cateringOperationOrLettingAccommodationController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ) =
    new CateringOperationController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      cateringOperationView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  def cateringOperationOrLettingAccommodationController6015(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ) =
    new CateringOperationController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      cateringOperationView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = cateringOperationOrLettingAccommodationController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = cateringOperationOrLettingAccommodationController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 6015" in {
      val result = cateringOperationOrLettingAccommodationController6015().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML 6015" in {
      val result = cateringOperationOrLettingAccommodationController6015().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = cateringOperationOrLettingAccommodationController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Catering operation or letting accommodation form" should {
    "error if cateringOperationOrLettingAccommodation is missing" in {
      val formData = baseFormData - errorKey.cateringOperationOrLettingAccommodation
      val form     = cateringOperationForm.bind(formData)

      mustContainError(
        errorKey.cateringOperationOrLettingAccommodation,
        "error.cateringOperationOrLettingAccommodation.missing",
        form
      )
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val cateringOperationOrLettingAccommodation: String = "cateringOperationOrLettingAccommodation"
    }

    val baseFormData: Map[String, String] = Map("cateringOperationOrLettingAccommodation" -> "yes")
  }
}
