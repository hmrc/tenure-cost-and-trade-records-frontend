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

import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import form.aboutfranchisesorlettings.AddAnotherLettingForm.addAnotherLettingForm
import play.api.http.Status
import utils.TestBaseSpec
import utils.FormBindingTestAssertions.mustContainError
import play.api.test.Helpers._
import actions.SessionRequest
import play.api.test.FakeRequest
import scala.language.reflectiveCalls

class AddOrRemoveLettingControllerSpec extends TestBaseSpec {
  import TestData._
  def addOrRemoveLettingController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(
      prefilledAboutFranchiseOrLettingsWith6020LettingsAll
    )
  ) = new AddOrRemoveLettingController(
    stubMessagesControllerComponents(),
    aboutFranchisesOrLettingsNavigator,
    addOrRemoveLettingView,
    genericRemoveConfirmationView,
    preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = forType6020),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = addOrRemoveLettingController().show(0)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = addOrRemoveLettingController().show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = addOrRemoveLettingController().show(0)(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      )
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val result = addOrRemoveLettingController().submitLetting(1)(fakeRequest)
        status(result) shouldBe BAD_REQUEST
      }
      "throw a BAD_REQUEST if an empty form is submitted via CYA" in {
        val result =
          addOrRemoveLettingController().submitLetting(1)(FakeRequest().withFormUrlEncodedBody("from" -> "CYA"))
        status(result) shouldBe BAD_REQUEST
      }
    }

    "REMOVE /" should {
      "return OK " in {
        val result = addOrRemoveLettingController().remove(1)(fakeRequest)
        status(result) shouldBe OK
      }
    }
  }

  "handle form submission with valid data correctly for telco mast" in {
    val validFormData = Map("addAnotherLetting" -> "yes")
    val request       = FakeRequest(POST, "/add-remove-letting-submit/1").withFormUrlEncodedBody(validFormData.toSeq: _*)
    val controller    = addOrRemoveLettingController()

    val result = controller.submitLetting(1)(request)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/type-of-letting?idx=2"
    )
  }

  "handle form submission with valid data correctly for ATM " in {
    val validFormData = Map("addAnotherLetting" -> "yes")
    val request       = FakeRequest(POST, "/add-remove-letting-submit/1").withFormUrlEncodedBody(validFormData.toSeq: _*)
    val controller    = addOrRemoveLettingController()

    val result = controller.submitLetting(0)(request)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/type-of-letting?idx=1"
    )
  }

  "handle form submission with valid data correctly for Advertising Right " in {
    val validFormData = Map("addAnotherLetting" -> "yes")
    val request       = FakeRequest(POST, "/add-remove-letting-submit/1").withFormUrlEncodedBody(validFormData.toSeq: _*)
    val controller    = addOrRemoveLettingController()

    val result = controller.submitLetting(2)(request)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/type-of-letting?idx=3"
    )
  }

  "handle form submission with valid data correctly for Other letting " in {
    val validFormData = Map("addAnotherLetting" -> "yes")
    val request       = FakeRequest(POST, "/add-remove-letting-submit/1").withFormUrlEncodedBody(validFormData.toSeq: _*)
    val controller    = addOrRemoveLettingController()

    val result = controller.submitLetting(3)(request)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/type-of-letting?idx=4"
    )
  }

  "redirect to CYA page if No value for Radio Buttons selected" in {
    val validFormData = Map("addAnotherLetting" -> "no")
    val request       = FakeRequest(POST, "/add-remove-letting-submit/1").withFormUrlEncodedBody(validFormData.toSeq: _*)
    val controller    = addOrRemoveLettingController()
    val result        = controller.submitLetting(1)(request)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some(
      "/send-trade-and-cost-information/check-your-answers-about-franchise-or-lettings"
    )
  }

  "ensure maximum limit for lettings is respected" in {
    val validFormData = Map("addAnotherLetting" -> "yes")
    val request       = FakeRequest(POST, "/add-remove-letting-submit/1").withFormUrlEncodedBody(validFormData.toSeq: _*)
    val controller    = addOrRemoveLettingController(Some(prefilledAboutFranchiseOrLettingsWith6020MaxLettings))
    val result        = controller.submitLetting(10)(request)
    status(result)           shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/max-lettings?src=lettings")
  }

  "Add another letting form" should {
    "error if addAnotherLetting is missing" in {
      val formData = baseFormData - errorKey.addAnotherLetting
      val form     = addAnotherLettingForm.bind(formData)

      mustContainError(
        errorKey.addAnotherLetting,
        "error.addAnotherLetting.required",
        form
      )
    }
  }
  "Remove a letting"         should {
    "render the removal confirmation page on remove" in {
      val controller     = addOrRemoveLettingController()
      val idxToRemove    = 0
      val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6020Session, fakeRequest)
      val result         = controller.remove(idxToRemove)(sessionRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }
  }

  "performRemove" should {

    "redirect to the updated letting list on confirmation of atm removal" in {
      val indexToRemove   = 0
      val fakePostRequest = FakeRequest(POST, "/remove-letting-confirm")
        .withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val controller      = addOrRemoveLettingController()

      val result = controller.performRemove(indexToRemove)(fakePostRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/add-another-letting?idx=2")
    }

    "redirect to the updated letting list on confirmation of telco removal" in {
      val indexToRemove   = 1
      val fakePostRequest = FakeRequest(POST, "/remove-letting-confirm")
        .withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val controller      = addOrRemoveLettingController()

      val result = controller.performRemove(indexToRemove)(fakePostRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/add-another-letting?idx=2")
    }

    "redirect to the updated letting list on confirmation of advert removal" in {
      val indexToRemove   = 2
      val fakePostRequest = FakeRequest(POST, "/remove-letting-confirm")
        .withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val controller      = addOrRemoveLettingController()

      val result = controller.performRemove(indexToRemove)(fakePostRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/add-another-letting?idx=2")
    }

    "redirect to the updated letting list on confirmation of other removal" in {
      val indexToRemove   = 3
      val fakePostRequest = FakeRequest(POST, "/remove-letting-confirm")
        .withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val controller      = addOrRemoveLettingController()

      val result = controller.performRemove(indexToRemove)(fakePostRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some("/send-trade-and-cost-information/add-another-letting?idx=2")
    }
  }

  object TestData {
    val errorKey: Object {
      val addAnotherLetting: String
    } = new {
      val addAnotherLetting: String =
        "addAnotherLetting"
    }

    val baseFormData: Map[String, String] = Map("addAnotherLetting" -> "yes")
  }
}
