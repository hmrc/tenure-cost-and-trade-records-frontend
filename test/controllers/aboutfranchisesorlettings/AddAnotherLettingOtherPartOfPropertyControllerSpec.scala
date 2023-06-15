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

import form.Errors
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import form.aboutfranchisesorlettings.AddAnotherLettingOtherPartOfPropertyForm.addAnotherLettingForm
import play.api.http.Status
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class AddAnotherLettingOtherPartOfPropertyControllerSpec extends TestBaseSpec {

  import TestData._

  def addAnotherLettingOtherPartOfPropertyController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ) =
    new AddAnotherLettingOtherPartOfPropertyController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      addAnotherOperationConcessionFranchise,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = addAnotherLettingOtherPartOfPropertyController().show(0)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = addAnotherLettingOtherPartOfPropertyController().show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val result = addAnotherLettingOtherPartOfPropertyController().submit(1)(fakeRequest)
        status(result) shouldBe BAD_REQUEST
      }
    }
  }

  "Add another letting accommodation form" should {
    "error if addAnotherCateringOperationOrLettingAccommodation is missing" in {
      val formData = baseFormData - errorKey.addAnotherLettingOtherPartOfProperty
      val form     = addAnotherLettingForm.bind(formData)

      mustContainError(errorKey.addAnotherLettingOtherPartOfProperty, Errors.booleanMissing, form)
    }
  }

  object TestData {
    val errorKey: Object {
      val addAnotherLettingOtherPartOfProperty: String
    } = new {
      val addAnotherLettingOtherPartOfProperty: String =
        "addAnotherLettingOtherPartOfProperty"
    }

    val baseFormData: Map[String, String] = Map("addAnotherLettingOtherPartOfProperty" -> "yes")
  }
}
