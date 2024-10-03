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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class IsVATPayableForWholePropertyControllerSpec extends TestBaseSpec {

  def isVATPayableForWholePropertyController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new IsVATPayableForWholePropertyController(
    isVATPayableForWholePropertyView,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "IsVATPayableForWholePropertyController GET /" should {
    "return 200 and HTML with is VAT payable for whole property is present in session" in {
      val result = isVATPayableForWholePropertyController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show().url
      )
    }

    "return 200 and HTML is VAT payable for whole property is none in session" in {
      val controller = isVATPayableForWholePropertyController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show().url
      )
    }
  }

  "IsVATPayableForWholePropertyController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = isVATPayableForWholePropertyController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data isVATPayableForWholeProperty submitted" in {
      val res = isVATPayableForWholePropertyController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "isVatPayableForWholeProperty" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

}
