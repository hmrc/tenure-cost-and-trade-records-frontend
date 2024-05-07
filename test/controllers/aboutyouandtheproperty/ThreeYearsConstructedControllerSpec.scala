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

import form.aboutyouandtheproperty.ThreeYearsConstructedForm.threeYearsConstructedForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status._
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class ThreeYearsConstructedControllerSpec extends TestBaseSpec {
  import TestData._
  import utils.FormBindingTestAssertions._

  def threeYearsConstructedController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) =
    new ThreeYearsConstructedController(
      stubMessagesControllerComponents(),
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

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = threeYearsConstructedController().submit(
          FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
        )
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Form" should {
    "error if data is missing" in {
      val formData = baseFormData - errorKey.threeYearsConstructed
      val form     = threeYearsConstructedForm.bind(formData)

      mustContainError(errorKey.threeYearsConstructed, "error.threeYearsConstructed.required", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val threeYearsConstructed: String
    } = new {
      val threeYearsConstructed: String = "threeYearsConstructed"
    }

    val baseFormData: Map[String, String] = Map("threeYearsConstructed" -> "yes")
  }

}
