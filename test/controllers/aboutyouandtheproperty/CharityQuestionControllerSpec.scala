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

import form.aboutyouandtheproperty.CharityQuestionForm.charityQuestionForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status._
import play.api.test.Helpers.{contentType, status, stubMessagesControllerComponents}
import play.api.test.{FakeRequest, Helpers}
import utils.TestBaseSpec

class CharityQuestionControllerSpec extends TestBaseSpec {
  import TestData._
  import utils.FormBindingTestAssertions._

  def charityQuestionController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) =
    new CharityQuestionController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      charityQuestionView,
      preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepo
    )

  def charityQuestionControllerNone() = new CharityQuestionController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    charityQuestionView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "CharityQuestion controller" should {
    "GET / return 200 charity question in the session" in {
      val result = charityQuestionController().show(fakeRequest)
      status(result) shouldBe OK
    }

    "GET / return HTML" in {
      val result = charityQuestionController().show(fakeRequest)
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }

    "GET / return 200 no charity question in the session" in {
      val result = charityQuestionControllerNone().show(fakeRequest)
      status(result)          shouldBe OK
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = charityQuestionController().submit(
          FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
        )
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Contact details question form" should {
    "error if contact details question is missing" in {
      val formData = baseFormData - errorKey.charityQuestion
      val form     = charityQuestionForm.bind(formData)

      mustContainError(errorKey.charityQuestion, "error.charityQuestion.missing", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val charityQuestion: String
    } = new {
      val charityQuestion: String = "charityQuestion"
    }

    val baseFormData: Map[String, String] = Map("charityQuestion" -> "yes")
  }

}
