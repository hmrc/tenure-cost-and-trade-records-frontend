/*
 * Copyright 2023 HM Revenue & Customs
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

import form.aboutyouandtheproperty.ContactDetailsQuestionForm.contactDetailsQuestionForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.Helpers.{contentType, status, stubMessagesControllerComponents}
import play.api.test.{FakeRequest, Helpers}
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class ContactDetailsQuestionControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def contactDetailsQuestionController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) =
    new ContactDetailsQuestionController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      contactDetailsQuestionView,
      preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepo
    )

  def contactDetailsQuestionControllerNone() = new ContactDetailsQuestionController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    contactDetailsQuestionView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "ContactDetailsQuestion controller" should {
    "GET / return 200 contact details in the session" in {
      val result = contactDetailsQuestionController().show(fakeRequest)
      status(result) shouldBe OK
    }

    "GET / return HTML" in {
      val result = contactDetailsQuestionController().show(fakeRequest)
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }

    "GET / return 200 no contact details in the session" in {
      val result = contactDetailsQuestionControllerNone().show(fakeRequest)
      status(result)          shouldBe OK
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = contactDetailsQuestionController().submit(
          FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
        )
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Contact details question form" should {
    "error if contact details question is missing" in {
      val formData = baseFormData - errorKey.contactDetailsQuestion
      val form     = contactDetailsQuestionForm.bind(formData)

      mustContainError(errorKey.contactDetailsQuestion, "error.contactDetailsQuestion.missing", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val contactDetailsQuestion: String
    } = new {
      val contactDetailsQuestion: String = "contactDetailsQuestion"
    }

    val baseFormData: Map[String, String] = Map("contactDetailsQuestion" -> "yes")
  }

}
