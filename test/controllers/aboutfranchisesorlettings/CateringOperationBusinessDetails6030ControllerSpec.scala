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

package controllers.aboutfranchisesorlettings

import form.aboutfranchisesorlettings.CateringOperationBusinessDetails6030Form.cateringOperationBusinessDetails6030Form
import models.ForTypes
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class CateringOperationBusinessDetails6030ControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  val mockAboutFranchisesOrLettingsNavigator = mock[AboutFranchisesOrLettingsNavigator]

  def cateringOperationOrLettingAccommodationDetailsController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings6030)
  ) =
    new CateringOperationBusinessDetailsController(
      stubMessagesControllerComponents(),
      mockAboutFranchisesOrLettingsNavigator,
      cateringOperationDetailsView,
      preEnrichedActionRefiner(forType = ForTypes.for6030, aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = cateringOperationOrLettingAccommodationDetailsController().show(None)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = cateringOperationOrLettingAccommodationDetailsController().show(None)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = cateringOperationOrLettingAccommodationDetailsController().submit(None)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Catering Operation Business Details form" should {
    "error if operator name for 6030 is missing" in {
      val formDataWithEmptyOperatorName = baseFormData.updated(TestData.errorKey.operatorName, "")
      val form                          = cateringOperationBusinessDetails6030Form.bind(formDataWithEmptyOperatorName)

      mustContainError(errorKey.operatorName, "error.operatorName6030.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val operatorName = "operatorName6030"
    }

    val baseFormData: Map[String, String] = Map(
      "operatorName6030" -> "Orinoco"
    )
  }

}
