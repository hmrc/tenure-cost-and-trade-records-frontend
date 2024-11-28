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
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class CheckYourAnswersAboutFranchiseOrLettingsControllerSpec extends TestBaseSpec {

  import TestData._

  def checkYourAnswersAboutFranchiseOrLettingsController6045(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings6045)
  ) =
    new CheckYourAnswersAboutFranchiseOrLettingsController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      checkYourAnswersAboutFranchiseOrLettings,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = FOR6045),
      mockSessionRepo
    )

  def checkYourAnswersAboutFranchiseOrLettingsController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ) =
    new CheckYourAnswersAboutFranchiseOrLettingsController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      checkYourAnswersAboutFranchiseOrLettings,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  def checkYourAnswersAboutFranchiseOrLettingsController6020(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(
      prefilledAboutFranchiseOrLettingsWith6020LettingsAll
    )
  ) =
    new CheckYourAnswersAboutFranchiseOrLettingsController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      checkYourAnswersAboutFranchiseOrLettings,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = FOR6020),
      mockSessionRepo
    )

  def checkYourAnswersAboutFranchiseOrLettingsControllerNo(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(
      prefilledAboutFranchiseOrLettingsNo
    )
  ) =
    new CheckYourAnswersAboutFranchiseOrLettingsController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      checkYourAnswersAboutFranchiseOrLettings,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = FOR6020),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 6020" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsController6020().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 no" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsControllerNo().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML 6020" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsController6020().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML 6045" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsController6045().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML no" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsControllerNo().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "Render the add-remove-rental-income link correctly for 6045 form" in {
      val result        = checkYourAnswersAboutFranchiseOrLettingsController6045().show(fakeRequest)
      val html          = Jsoup.parse(contentAsString(result))
      val addRemoveLink = html.getElementById("add-remove-rental-income")

      addRemoveLink.attr("href") should include(
        controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(1).url
      )
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = checkYourAnswersAboutFranchiseOrLettingsController().show()(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersAboutFranchiseOrLettingsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "SUBMIT /"                               should {
    "throw a BAD_REQUEST if an empty form is submitted for 6020" in {
      val res = checkYourAnswersAboutFranchiseOrLettingsController6020().submit(
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
