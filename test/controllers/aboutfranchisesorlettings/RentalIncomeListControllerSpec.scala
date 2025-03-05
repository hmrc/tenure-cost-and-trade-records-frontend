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

import actions.SessionRequest
import connectors.Audit
import form.aboutfranchisesorlettings.RentalIncomeListForm.rentalIncomeListForm
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, ConcessionIncomeRecord}
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class RentalIncomeListControllerSpec extends TestBaseSpec {
  import TestData._
  val mockAudit: Audit = mock[Audit]
  def controller(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(
      prefilledAboutFranchiseOrLettings6045
    )
  ) = new RentalIncomeListController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutFranchisesOrLettingsNavigator,
    rentalIncomeListView,
    genericRemoveConfirmationView,
    preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = controller().show(0)(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = controller().show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = controller().show(0)(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      )
    }
    "return ConcessionTypeFeesController back link for concession" in {
      val result = controller().show(0)(fakeRequest)
      contentAsString(result) should include(
        controllers.aboutfranchisesorlettings.routes.ConcessionTypeFeesController.show(0).url
      )
    }

    "return IncomeRecordIncludedController back link for letting" in {
      val result = controller().show(1)(fakeRequest)
      contentAsString(result) should include(
        controllers.aboutfranchisesorlettings.routes.RentalIncomeIncludedController.show(1).url
      )
    }

    "return TaskListController back link when no valid record is found" in {
      val result = controller().show(99)(fakeRequest)
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val result = controller().submit(1)(fakeRequest)
        status(result) shouldBe BAD_REQUEST
      }
      "throw a BAD_REQUEST if an empty form is submitted via CYA" in {
        val result =
          controller().submit(1)(FakeRequest().withFormUrlEncodedBody("from" -> "CYA"))
        status(result) shouldBe BAD_REQUEST
      }
    }

    "REMOVE /" should {
      "return OK " in {
        val result = controller().remove(1)(fakeRequest)
        status(result) shouldBe OK
      }
    }
  }

  "handle form submission with valid data correctly" in {
    val validFormData = Map("rentalIncomeList" -> "yes")
    val request       = FakeRequest(POST, "type-of-income?idx=1").withFormUrlEncodedBody(validFormData.toSeq*)

    val result = controller().submit(1)(request)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/type-of-income?idx=2"
    )
  }

  "ensure maximum limit for records is respected" in {

    val rentalIncomeWithMaxEntries = AboutFranchisesOrLettings(
      rentalIncome = Some(
        IndexedSeq(
          ConcessionIncomeRecord(),
          ConcessionIncomeRecord(),
          ConcessionIncomeRecord(),
          ConcessionIncomeRecord(),
          ConcessionIncomeRecord()
        )
      )
    )

    val controllerWithMaxEntries = controller(Some(rentalIncomeWithMaxEntries))
    val formData                 = Map("rentalIncomeList" -> "yes")
    val request                  = FakeRequest(POST, "/send-trade-and-cost-information/type-of-income?idx=5").withFormUrlEncodedBody(
      formData.toSeq*
    )

    val result = controllerWithMaxEntries.submit(5)(request)

    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      controllers.routes.MaxOfLettingsReachedController.show(Some("rentalIncome")).url
    )
  }

  "Add another rental income form" should {
    "error if answer is missing" in {
      val formData = baseFormData - errorKey.rentalIncomeList
      val form     = rentalIncomeListForm.bind(formData)

      mustContainError(
        errorKey.rentalIncomeList,
        "error.rentalIncomeList.required",
        form
      )
    }
  }
  "Remove a letting"               should {
    "render the removal confirmation page on remove" in {
      val idxToRemove    = 0
      val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6045, fakeRequest)
      val result         = controller().remove(idxToRemove)(sessionRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }
  }

  "performRemove" should {

    "redirect to the updated rental income  list on confirmation " in {
      val indexToRemove   = 1
      val fakePostRequest = FakeRequest(POST, "/remove-letting-confirm")
        .withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")

      val result = controller().performRemove(indexToRemove)(fakePostRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/rental-income-list?idx=0")
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val rentalIncomeList: String =
        "rentalIncomeList"
    }

    val baseFormData: Map[String, String] = Map("rentalIncomeList" -> "yes")
  }
}
