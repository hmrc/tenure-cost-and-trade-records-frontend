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

package controllers.aboutYourLeaseOrTenure

import form.Errors
import play.api.http.Status
import play.api.test.Helpers._
import utils.TestBaseSpec

class RentIncludeTradeServicesControllerSpec extends TestBaseSpec {

  import TestData._
  import form.aboutYourLeaseOrTenure.RentIncludeTradeServicesForm._
  import utils.FormBindingTestAssertions._

  private val controller = app.injector.instanceOf[RentIncludeTradeServicesController]

  "RentIncludetradeServices controller" should {
    "return 200" in {
      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "error if rentIncludeTradeServices is missing" in {
      val formData = baseFormData - errorKey.rentIncludeTradeServices
      val form     = rentIncludeTradeServicesForm.bind(formData)

      mustContainError(errorKey.rentIncludeTradeServices, Errors.booleanMissing, form)
    }
  }

  object TestData {
    val errorKey = new {
      val rentIncludeTradeServices: String = "rentIncludeTradeServices"
    }

    val baseFormData: Map[String, String] = Map("rentIncludeTradeServices" -> "yes")
  }
}
