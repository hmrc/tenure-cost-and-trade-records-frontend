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
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import form.aboutyouandtheproperty.CostsBreakdownForm.costsBreakdownForm
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, charset, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class CostsBreakdownControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  val mockAudit: Audit = mock[Audit]

  def costsBreakdownController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new CostsBreakdownController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    costsBreakdownView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def costsBreakdownControllerNone() = new CostsBreakdownController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    costsBreakdownView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "GET / costs breakdown" should {
    "GET / return 200 about you in the session" in {
      val result = costsBreakdownController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = costsBreakdownController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 plant and technology in the session" in {
      val result = costsBreakdownControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT / costs breakdown" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val result = costsBreakdownController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = costsBreakdownController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "costsBreakdown" -> "xxx"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "costs breakdown form" should {
    "error if  value is missing" in {
      val empty = baseFormData.updated(TestData.errorKey.costsBreakdown, "")
      val form  = costsBreakdownForm.bind(empty)

      mustContainError(errorKey.costsBreakdown, "error.costsBreakdown.required", form)
    }
  }

  object TestData {
    val errorKey = new ErrorKey

    class ErrorKey {
      val costsBreakdown = "costsBreakdown"
    }

    val baseFormData: Map[String, String] = Map(
      "costsBreakdown" -> "xxx"
    )
  }

}
