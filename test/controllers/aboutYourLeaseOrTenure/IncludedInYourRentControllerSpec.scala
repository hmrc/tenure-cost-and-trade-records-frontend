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

package controllers.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.IncludedInYourRentForm.includedInYourRentForm
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.data.Form
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class IncludedInYourRentControllerSpec extends TestBaseSpec {

  import utils.FormBindingTestAssertions._

  def includedInYourRentController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) = new IncludedInYourRentController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    includedInYourRentView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
    mockSessionRepo
  )

  "IncludedInYourRentController GET /" should {
    "return 200 and HTML with Included In Your Rent Details in the session" in {
      val result = includedInYourRentController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController.show().url
      )
    }

    "return 200 and HTML with no Included In Your Rent Details in the session" in {
      val controller = includedInYourRentController(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController.show().url
      )
    }
  }

  "IncludedInYourRentController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = includedInYourRentController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data includedInYourRent submitted" in {
      val res = includedInYourRentController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "includedInYourRent[0]" -> "nondomesticRates",
          "vatValue"              -> "100"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Form validation" should {

    val baseData: Map[String, String] = Map(
      "includedInYourRent[0]" -> "vat"
    )

    def mustContainInvalidVatValueErrorFor[T](field: String, f: Form[T]): Unit =
      mustContainError(field, "error.includedInYourRent.vatValue.range", f)

    def mustContainMissingVatValueErrorFor[T](field: String, f: Form[T]): Unit =
      mustContainError(field, "error.includedInYourRent.vatValue.required", f)

    "error if vatValue is invalid for form type 6045" in {
      val formData = baseData + ("vatValue" -> "invalid")
      val form     = includedInYourRentForm(FOR6045).bind(formData)
      mustContainInvalidVatValueErrorFor("vatValue", form)
    }

    "error if vatValue is missing form type 6045" in {
      val formData = baseData + ("vatValue" -> "")
      val form     = includedInYourRentForm(FOR6045).bind(formData)
      mustContainMissingVatValueErrorFor("", form)
    }
    "error if vatValue is invalid for form type 6046" in {
      val formData = baseData + ("vatValue" -> "invalid")
      val form     = includedInYourRentForm(FOR6046).bind(formData)
      mustContainInvalidVatValueErrorFor("vatValue", form)
    }

    "error if vatValue is missing form type 6046" in {
      val formData = baseData + ("vatValue" -> "")
      val form     = includedInYourRentForm(FOR6046).bind(formData)
      mustContainMissingVatValueErrorFor("", form)
    }
  }
}
