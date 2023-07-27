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

package controllers.downloadFORTypeForm

import form.downloadFORTypeForm.DownloadPDFReferenceNumberForm.downloadPDFReferenceNumberForm
import play.api.http.Status
import play.api.test.Helpers._
import stub.StubBackendConnector
import utils.TestBaseSpec

class DownloadPDFReferenceNumberControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  def downloadPDFReferenceNumberController() = new DownloadPDFReferenceNumberController(
    stubMessagesControllerComponents(),
    downloadPDFReferenceNumberView,
    StubBackendConnector()
  )

  "GET /" should {
    "return 200" in {
      val result = downloadPDFReferenceNumberController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = downloadPDFReferenceNumberController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = downloadPDFReferenceNumberController().submit(
        fakeRequest.withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Download PDF reference number form" should {
    "error if reference number is missing" in {
      val formData = baseFormData - errorKey.referenceNumber
      val form     = downloadPDFReferenceNumberForm.bind(formData)

      mustContainError(errorKey.referenceNumber, "error.downloadPdfReferenceNumber.required", form)
    }
  }

  object TestData {
    val errorKey = new {
      val referenceNumber = "downloadPdfReferenceNumber"
    }

    val baseFormData: Map[String, String] = Map(
      "downloadPdfReferenceNumber" -> "9999601001"
    )
  }
}
