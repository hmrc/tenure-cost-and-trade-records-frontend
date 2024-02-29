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

package controllers.additionalinformation

import controllers.aboutyouandtheproperty.ContactDetailsQuestionController
import form.aboutyouandtheproperty.ContactDetailsQuestionForm.contactDetailsQuestionForm
import models.submissions.additionalinformation.AdditionalInformation
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class ContactDetailsQuestionControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def contactDetailsQuestionController(
    additionalInformation: Option[AdditionalInformation] = Some(prefilledAdditionalInformation)
  ) =
    new ContactDetailsQuestionController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      contactDetailsQuestionView,
      preEnrichedActionRefiner(additionalInformation = additionalInformation),
      mockSessionRepo
    )

  "ContactDetailsQuestion controller" should {
    "return 200" in {
      val result = contactDetailsQuestionController().show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = contactDetailsQuestionController().show(fakeRequest)
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
