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
import form.aboutyouandtheproperty.CostsBreakdownForm.costsBreakdownForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import test.ControllerSpec

import scala.language.reflectiveCalls

class CostsBreakdownControllerSpec extends ControllerSpec:

  import TestData.{baseFormData, errorKey}

  val mockAudit: Audit = mock[Audit]

  def costsBreakdownController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ): CostsBreakdownController =
    CostsBreakdownController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYouAndThePropertyNavigator,
      costsBreakdownView,
      preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def costsBreakdownControllerNone(): CostsBreakdownController =
    CostsBreakdownController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYouAndThePropertyNavigator,
      costsBreakdownView,
      preEnrichedActionRefiner(aboutYouAndTheProperty = None),
      mockSessionRepository
    )

  "GET /" should {
    "GET / return 200 about you in the session" in {
      val result = costsBreakdownController().show(getRequest)
      status(result) shouldBe OK
    }

    "GET / return HTML" in {
      val result = costsBreakdownController().show(getRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 plant and technology in the session" in {
      val result = costsBreakdownControllerNone().show(getRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
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

  "Costs breakdown form" should {
    "error if  value is missing" in {
      val empty = baseFormData.updated(TestData.errorKey.costsBreakdown, "")
      val form  = costsBreakdownForm.bind(empty)

      mustContainError(errorKey.costsBreakdown, "error.costsBreakdown.required", form)
    }
  }

  object TestData:
    val errorKey = new ErrorKey

    class ErrorKey:
      val costsBreakdown = "costsBreakdown"

    val baseFormData: Map[String, String] = Map(
      "costsBreakdown" -> "xxx"
    )
