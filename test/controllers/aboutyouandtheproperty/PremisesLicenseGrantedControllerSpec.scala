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

import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class PremisesLicenseGrantedControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import form.aboutyouandtheproperty.PremisesLicenseGrantedForm.premisesLicenseGrantedForm

  def premisesLicenseGrantedController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new PremisesLicenseGrantedController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    premisesLicenceGrantedView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def premisesLicenseGrantedControllerNone() = new PremisesLicenseGrantedController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    premisesLicenceGrantedView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "Premises licence granted controller" should {
    "GET / return 200 license granted in the session" in {
      val result = premisesLicenseGrantedController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = premisesLicenseGrantedController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 no license granted in the session" in {
      val result = premisesLicenseGrantedControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = premisesLicenseGrantedController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Premises licence granted form" should {
    "error if choice is missing " in {
      val formData = baseFormData - errorKey.premisesLicenseGranted
      val form     = premisesLicenseGrantedForm.bind(formData)

      mustContainError(errorKey.premisesLicenseGranted, "error.premisesLicenseGranted.missing", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val premisesLicenseGranted: String
    } = new {
      val premisesLicenseGranted: String = "premisesLicenseGranted"
    }

    val baseFormData: Map[String, String] = Map(
      "premisesLicenseGranted" -> "true"
    )
  }
}
