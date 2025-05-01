/*
 * Copyright 2025 HM Revenue & Customs
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

import connectors.Audit
import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class RentIncludeFixtureAndFittingsDetailsControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def rentIncludeFixtureAndFittingsDetailsController(
    forType: ForType = FOR6010,
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne),
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new RentIncludeFixtureAndFittingsDetailsController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    rentIncludeFixtureAndFittingsDetailsView,
    rentIncludeFixtureAndFittingsDetailsTextAreaView,
    preEnrichedActionRefiner(
      forType = forType,
      aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne,
      aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree
    ),
    mockSessionRepo
  )

  "RentIncludeFixtureAndFittingsDetailsController GET /" should {
    "return 200 data" in {
      val result = rentIncludeFixtureAndFittingsDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
      )
    }

    "return 200 none" in {
      val controller = rentIncludeFixtureAndFittingsDetailsController(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
      )
    }

    "return 200 data for 6045" in {
      val result = rentIncludeFixtureAndFittingsDetailsController(forType = FOR6045).show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
      )
    }

    "return 200 and HTML Rent Includes trade services with none in the session for 6045" in {
      val controller = rentIncludeFixtureAndFittingsDetailsController(
        forType = FOR6045,
        aboutLeaseOrAgreementPartThree = None
      )
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
      )
    }
  }

  "RentIncludeFixtureAndFittingsDetailsController SUBMIT /" should {
    "throw a See_Other if an empty form is submitted" in {
      val res = rentIncludeFixtureAndFittingsDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe SEE_OTHER
    }

    "throw a See_Other if an empty form is submitted with no data in session" in {
      val controller = rentIncludeFixtureAndFittingsDetailsController(aboutLeaseOrAgreementPartOne = None)
      val res        = controller.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe SEE_OTHER
    }

    "Redirect when form data submitted for 6045" in {
      val res = rentIncludeFixtureAndFittingsDetailsController(forType = FOR6045).submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "describeFittingsTextArea" -> "Rent include fixture and fitting details"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "throw a BAD_REQUEST if an empty form is submitted for 6045" in {
      val res = rentIncludeFixtureAndFittingsDetailsController(forType = FOR6045).submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "throw a BAD_REQUEST if describeFittingsTextArea is greater than max length is submitted" in {
      val res = rentIncludeFixtureAndFittingsDetailsController(forType = FOR6045).submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "describeFittingsTextArea" -> "x" * 2001
        )
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
