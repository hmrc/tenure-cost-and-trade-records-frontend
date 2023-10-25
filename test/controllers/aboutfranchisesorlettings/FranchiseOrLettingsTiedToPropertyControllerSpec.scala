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

package controllers.aboutfranchisesorlettings

import form.aboutfranchisesorlettings.FranchiseOrLettingsTiedToPropertyForm.franchiseOrLettingsTiedToPropertyForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class FranchiseOrLettingsTiedToPropertyControllerSpec extends TestBaseSpec {

  import TestData._

  def franchiseOrLettingsTiedToPropertyController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ) =
    new FranchiseOrLettingsTiedToPropertyController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      franchiseOrLettingsTiedToPropertyView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = franchiseOrLettingsTiedToPropertyController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = franchiseOrLettingsTiedToPropertyController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res =
        franchiseOrLettingsTiedToPropertyController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Franchise or lettings tied to property form" should {
    "error if franchiseOrLettingsTiedToProperty is missing" in {
      val formData = baseFormData - errorKey.franchiseOrLettingsTiedToProperty
      val form     = franchiseOrLettingsTiedToPropertyForm.bind(formData)

      mustContainError(
        errorKey.franchiseOrLettingsTiedToProperty,
        "error.franchiseOrLettings.missing",
        form
      )
    }
  }

  object TestData {
    val errorKey: Object {
      val franchiseOrLettingsTiedToProperty: String
    } = new {
      val franchiseOrLettingsTiedToProperty: String = "franchiseOrLettingsTiedToProperty"
    }

    val baseFormData: Map[String, String] = Map("franchiseOrLettingsTiedToProperty" -> "yes")
  }
}
