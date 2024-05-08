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
import form.Errors
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import form.aboutyouandtheproperty.RenewablesPlantForm.renewablesPlantForm
import utils.TestBaseSpec

class RenewablesPlanControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def renewablesPlantController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new RenewablesPlantController(
    stubMessagesControllerComponents(),
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

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = renewablesPlantController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
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
    val errorKey: Object {
      val renewablesPlant: String
    } = new {
      val renewablesPlant: String = "renewablesPlant"
    }

    val baseFormData: Map[String, String] = Map("renewablesPlant" -> "baseload")
  }
}
