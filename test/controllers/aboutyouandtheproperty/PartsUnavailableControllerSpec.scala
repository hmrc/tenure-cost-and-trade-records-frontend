/*
 * Copyright 2026 HM Revenue & Customs
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

import connectors.Audit
import form.aboutyouandtheproperty.PartsUnavailableForm.partsUnavailableForm
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import test.ControllerSpec

class PartsUnavailableControllerSpec extends ControllerSpec:

  import TestData.*

  val mockAudit: Audit = mock[Audit]

  def controller(isWelsh: Boolean = false): PartsUnavailableController =
    PartsUnavailableController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYouAndThePropertyNavigator,
      partsUnavailableView,
      preEnrichedActionRefiner(isWelsh = isWelsh),
      mockSessionRepository
    )

  "GET /" should {
    "return 200" in {
      val result = controller().show(getRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = controller().show(getRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = controller().show()(getRequestFromTL)
      contentAsString(result) should include(s"${controllers.routes.TaskListController.show.url}#family-usage")
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = controller().show()(getRequestFromCYA)
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      )
    }

    "return correct backLink when no query param is present fo english property" in {
      val result = controller().show()(getRequest)
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsController.show().url
      )
    }

    "return correct backLink when no query param is present fo welsh property" in {
      val result = controller(isWelsh = true).show()(getRequest)
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsWelshController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = controller().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect to when form data submitted with yes" in {
      val res = controller().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "partsUnavailable" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "redirect when form data submitted with no" in {
      val res = controller().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "partsUnavailable" -> "no"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Parts unavailable form" should {
    "error if answer is missing" in {
      val formData = baseFormData - errorKey.partsUnavailableQuestion
      val form     = partsUnavailableForm.bind(formData)

      mustContainError(errorKey.partsUnavailableQuestion, "error.partsUnavailable.required", form)
    }
  }

  object TestData:
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey:
      val partsUnavailableQuestion: String = "partsUnavailable"

    val baseFormData: Map[String, String] = Map("partsUnavailable" -> "yes")
