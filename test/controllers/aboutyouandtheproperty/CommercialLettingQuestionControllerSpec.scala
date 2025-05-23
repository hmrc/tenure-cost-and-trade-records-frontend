/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.aboutyouandtheproperty

import actions.SessionRequest
import connectors.Audit
import form.aboutyouandtheproperty.CommercialLettingQuestionForm.commercialLettingQuestionForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo
import play.api.http.Status
import play.api.http.Status.*
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class CommercialLettingQuestionControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  val mockAudit: Audit = mock[Audit]
  def controller(
    isWelsh: Boolean = false,
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Some(
      prefilledAboutYouAndThePropertyPartTwo6048
    )
  ) = new CommercialLettingQuestionController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    commercialLettingQuestionView,
    preEnrichedActionRefiner(isWelsh = isWelsh, aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo),
    mockSessionRepo
  )

  "Controller - commercial letting question" should {
    "return 200" in {
      val result = controller().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when the session is None" in {
      val controllerNone = controller(aboutYouAndThePropertyPartTwo = None)
      val result         = controllerNone.show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = controller().show()(fakeRequestFromTL)
      contentAsString(result) should include(controllers.routes.TaskListController.show().url + "#about-the-property")
    }
    "return correct backLink when 'from=CYA' query param is present" in {
      val result = controller().show()(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = controller().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = controller().submit(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "commercialLettingQuestion.month" -> "4",
          "commercialLettingQuestion.year"  -> "2021"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "redirect to the next page for Welsh property" in {
      def welshController =
        controller(isWelsh = true, aboutYouAndThePropertyPartTwo = Some(prefilledAboutYouAndThePropertyPartTwo6048))

      val requestWithForm = FakeRequest(POST, "")
        .withFormUrlEncodedBody(baseFormData.toSeq*)

      val sessionRequest = SessionRequest(baseFilled6048WelshSession, requestWithForm)

      val result = welshController.submit(sessionRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(
        controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityWelshController.show().url
      )
    }

    "redirect to the next page for English property" in {
      val requestWithForm = FakeRequest(POST, "")
        .withFormUrlEncodedBody(baseFormData.toSeq*)

      val sessionRequest =
        SessionRequest(
          baseFilled6048Session,
          requestWithForm
        )

      val result = controller().submit(sessionRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(
        controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityController.show().url
      )
    }
  }

  "Form" should {
    "error if first occupy month and year are missing " in {
      val formData = baseFormData - errorKey.occupyMonth - errorKey.occupyYear
      val form     = commercialLettingQuestionForm(using messages).bind(formData)

      mustContainError(errorKey.occupyMonth, "error.date.required", form)
    }

    "error if first occupy month is missing " in {
      val formData = baseFormData - errorKey.occupyMonth
      val form     = commercialLettingQuestionForm(using messages).bind(formData)

      mustContainError(errorKey.occupyMonth, "error.date.mustInclude", form)
    }

    "error if first occupy year is missing" in {
      val formData = baseFormData - errorKey.occupyYear
      val form     = commercialLettingQuestionForm(using messages).bind(formData)

      mustContainError(errorKey.occupyYear, "error.date.mustInclude", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val occupyMonth = "commercialLettingQuestion.month"
      val occupyYear  = "commercialLettingQuestion.year"
    }

    val baseFormData: Map[String, String] = Map(
      "commercialLettingQuestion.month" -> "9",
      "commercialLettingQuestion.year"  -> "2022"
    )

  }
}
