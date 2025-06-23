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
import connectors.Audit
import form.Errors
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, ContactDetailsQuestion}
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, POST, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import form.aboutyouandtheproperty.RenewablesPlantForm.renewablesPlantForm
import models.submissions.common.AnswersYesNo.*
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class RenewablesPlanControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  val mockAudit: Audit = mock[Audit]
  def renewablesPlantController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new RenewablesPlantController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    renewablesPlantView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def renewablesPlantControllerNone(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(
      prefilledAboutYouAndThePropertyYes.copy(renewablesPlant = None)
    )
  ) = new RenewablesPlantController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    renewablesPlantView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  "Renewables Plan Controller" should {
    "return 200 when renewables plant in the session" in {
      val result = renewablesPlantController().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 when  no renewables plant in the session" in {
      val result = renewablesPlantControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
    "return correct backLink when 'from=TL' query param is present" in {
      val request = FakeRequest(GET, "/path?from=TL")
      val result  = renewablesPlantController().show(request)
      val html    = contentAsString(result)

      html should include(controllers.routes.TaskListController.show().url + "#technology-type")
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val request = FakeRequest(GET, "/path?from=CYA")
      val result  = renewablesPlantController().show(request)
      val html    = contentAsString(result)

      html should include(
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      )
    }
    "return correct backLink when altDetailsQuestion is AnswerYes" in {
      val aboutYouAndThePropertyWithAltDetails =
        prefilledAboutYouAndThePropertyYes.copy(altDetailsQuestion = Some(ContactDetailsQuestion(AnswerYes)))
      val result                               = renewablesPlantController(Some(aboutYouAndThePropertyWithAltDetails)).show(fakeRequest)
      val html                                 = contentAsString(result)

      html should include(controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show().url)
    }

    "return correct backLink when altDetailsQuestion is AnswerNo" in {
      val aboutYouAndThePropertyWithAltDetails =
        prefilledAboutYouAndThePropertyYes.copy(altDetailsQuestion = Some(ContactDetailsQuestion(AnswerNo)))
      val result                               = renewablesPlantController(Some(aboutYouAndThePropertyWithAltDetails)).show(fakeRequest)
      val html                                 = contentAsString(result)

      html should include(controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url)
    }

    "return correct backLink when altDetailsQuestion is None" in {
      val aboutYouAndThePropertyWithAltDetails =
        prefilledAboutYouAndThePropertyYes.copy(altDetailsQuestion = None)
      val result                               = renewablesPlantController(Some(aboutYouAndThePropertyWithAltDetails)).show(fakeRequest)
      val html                                 = contentAsString(result)

      html should include(controllers.routes.TaskListController.show().url)
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = renewablesPlantController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }

      "Redirect when form data submitted" in {
        val res = renewablesPlantController().submit(
          FakeRequest(POST, "/").withFormUrlEncodedBody(
            "renewablesPlant" -> "intermittent"
          )
        )
        status(res) shouldBe SEE_OTHER
      }
    }
  }
  "Form"                       should {
    "error if tiedForGoods is missing" in {
      val formData = baseFormData - errorKey.renewablesPlant
      val form     = renewablesPlantForm.bind(formData)

      mustContainError(errorKey.renewablesPlant, Errors.renewablesPlant, form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val renewablesPlant: String = "renewablesPlant"
    }

    val baseFormData: Map[String, String] = Map("renewablesPlant" -> "baseload")
  }
}
