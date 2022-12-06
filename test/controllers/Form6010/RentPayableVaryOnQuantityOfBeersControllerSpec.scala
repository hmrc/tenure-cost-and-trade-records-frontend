/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.Form6010

import form.Errors
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class RentPayableVaryOnQuantityOfBeersControllerSpec extends TestBaseSpec {

  import TestData._
  import form.Form6010.RentPayableVaryOnQuantityOfBeersForm._
  import utils.FormBindingTestAssertions._

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val fakeRequest = FakeRequest("GET", "/")

  private val controller = app.injector.instanceOf[RentPayableVaryOnQuantityOfBeersController]

  "RentPayableVaryOnQuantityOfBeers controller" should {
    "return 200" in {
      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "error if rentPayableVaryOnQuantityOfBeers is missing" in {
      val formData = baseFormData - errorKey.rentPayableVaryOnQuantityOfBeers
      val form     = rentPayableVaryOnQuantityOfBeersForm.bind(formData)

      mustContainError(errorKey.rentPayableVaryOnQuantityOfBeers, Errors.booleanMissing, form)
    }
  }

  object TestData {
    val errorKey = new {
      val rentPayableVaryOnQuantityOfBeers: String = "rentPayableVaryOnQuantityOfBeers"
    }

    val baseFormData: Map[String, String] = Map("rentPayableVaryOnQuantityOfBeers" -> "yes")
  }

}
