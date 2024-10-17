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

import form.aboutfranchisesorlettings.CheckYourAnswersAboutFranchiseOrLettingsForm.checkYourAnswersAboutFranchiseOrLettingsForm
import models.ForType.*
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class CheckYourAnswersAboutFranchiseOrLettings6016ControllerSpec extends TestBaseSpec {

  import TestData._

  def checkYourAnswersAboutFranchiseOrLettingsController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings6016)
  ) =
    new CheckYourAnswersAboutFranchiseOrLettingsController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      checkYourAnswersAboutFranchiseOrLettings,
      preEnrichedActionRefiner(
        referenceNumber = "99996016004",
        forType = FOR6016,
        aboutFranchisesOrLettings = aboutFranchisesOrLettings
      ),
      mockSessionRepo
    )

  def checkYourAnswersAboutFranchiseOrLettingsControllerNo(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettingsNo6016)
  ) =
    new CheckYourAnswersAboutFranchiseOrLettingsController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      checkYourAnswersAboutFranchiseOrLettings,
      preEnrichedActionRefiner(
        referenceNumber = "99996016004",
        forType = FOR6016,
        aboutFranchisesOrLettings = aboutFranchisesOrLettings
      ),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 No" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsControllerNo().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML No" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsControllerNo().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersAboutFranchiseOrLettingsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "throw a BAD_REQUEST if an empty form is submitted 6016" in {
      val res = checkYourAnswersAboutFranchiseOrLettingsControllerNo().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Add another letting accommodation form" should {
    "error if addAnotherCateringOperationOrLettingAccommodation is missing" in {
      val formData = baseFormData - errorKey.checkYourAnswersAboutFranchiseOrLettings
      val form     = checkYourAnswersAboutFranchiseOrLettingsForm.bind(formData)

      mustContainError(errorKey.checkYourAnswersAboutFranchiseOrLettings, "error.checkYourAnswersRadio.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val checkYourAnswersAboutFranchiseOrLettings: String =
        "checkYourAnswersAboutFranchiseOrLettings"
    }

    val baseFormData: Map[String, String] = Map("checkYourAnswersAboutFranchiseOrLettings" -> "yes")
  }

}
