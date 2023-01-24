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

import form.Errors
import navigation.AdditionalInformationNavigator
import play.api.data.FormError
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.additionalinformation.furtherInformationOrRemarks

class FurtherInformationOrRemarksControllerSpec extends TestBaseSpec {

  val mockAdditionalInformationNavigator  = mock[AdditionalInformationNavigator]
  val mockFurtherInformationOrRemarksView = mock[furtherInformationOrRemarks]
  when(mockFurtherInformationOrRemarksView.apply(any, any)(any, any)).thenReturn(HtmlFormat.empty)

  val furtherInformationOrRemarksController = new FurtherInformationOrRemarksController(
    stubMessagesControllerComponents(),
    mockAdditionalInformationNavigator,
    mockFurtherInformationOrRemarksView,
    preFilledSession,
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = furtherInformationOrRemarksController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = furtherInformationOrRemarksController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

//  "Further Information form" should {
//      "error if additional information is missing " in {
//      val formData = baseFormData - errorKey.additionalInfo
//      val form = furtherInformationOrRemarksForm.bind(formData)
//
//      mustContainRequiredErrorFor(errorKey.additionalInfo, form)
//    }
//  }

  object TestData {
    val errorKey = new {
      val additionalInfo: String = "c"
    }

    val formErrors                        = new {
      val required = new {
        val additionalInfo = FormError(errorKey.additionalInfo, Errors.required)
      }
    }
    val baseFormData: Map[String, String] = Map(
      "furtherInformationOrRemarks" -> "This is some test information"
    )

  }

}
