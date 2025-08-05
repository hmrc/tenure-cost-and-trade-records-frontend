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

package controllers.aboutyouandtheproperty

import connectors.Audit
import form.aboutyouandtheproperty.ThreeYearsConstructedForm.theForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status._
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{POST, contentAsString, contentType, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HttpVerbs.GET
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class ThreeYearsConstructedControllerSpec extends TestBaseSpec {
  import TestData._
  import utils.FormBindingTestAssertions._

  val mockAudit: Audit = mock[Audit]

  def threeYearsConstructedController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) =
    new ThreeYearsConstructedController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYouAndThePropertyNavigator,
      threeYearsConstructedView,
      preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepo
    )

  def threeYearsConstructedControllerNone(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(
      prefilledAboutYouAndThePropertyYes.copy(threeYearsConstructed = None)
    )
  ) = new ThreeYearsConstructedController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    threeYearsConstructedView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  "Controller" should {
    "GET / return 200 three years constructed in the session" in {
      val result = threeYearsConstructedController().show(fakeRequest)
      status(result) shouldBe OK
    }

    "GET / return HTML" in {
      val result = threeYearsConstructedController().show(fakeRequest)
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }

    "GET / return 200 when  no three years constructed data in the session" in {
      val result = threeYearsConstructedControllerNone().show(fakeRequest)
      status(result)          shouldBe OK
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }
    "return correct back link if query param from=TL is present" in {
      val result = threeYearsConstructedController().show(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }
    "return correct back link if query param from=CYA is present" in {
      val result = threeYearsConstructedController().show(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      )
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = threeYearsConstructedController().submit(
          FakeRequest().withFormUrlEncodedBody(Seq.empty*)
        )
        status(res) shouldBe BAD_REQUEST
      }

      "Redirect when form data submitted" in {
        val res = threeYearsConstructedController().submit(
          FakeRequest(POST, "/").withFormUrlEncodedBody(
            "threeYearsConstructed" -> "yes"
          )
        )
        status(res) shouldBe SEE_OTHER
      }
    }
  }

  "Form" should {
    "error if data is missing" in {
      val formData = baseFormData - errorKey.threeYearsConstructed
      val form     = theForm.bind(formData)

      mustContainError(errorKey.threeYearsConstructed, "error.threeYearsConstructed.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val threeYearsConstructed: String = "threeYearsConstructed"
    }

    val baseFormData: Map[String, String] = Map("threeYearsConstructed" -> "yes")
  }

}
