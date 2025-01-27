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

import connectors.Audit
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class TypeOfTenureControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def typeOfTenureController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new TypeOfTenureController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      typeOfTenureView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  "TypeOfTenureController GET /" should {
    "return 200 and HTML with Type of Tenure in the session" in {
      val result = typeOfTenureController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 and HTML Type of Tenure with none in the session" in {
      val controller = typeOfTenureController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = typeOfTenureController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url + "#type-of-tenure")
    }
  }

  "TypeOfTenureController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = typeOfTenureController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = typeOfTenureController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "typeOfTenure[0]"     -> "leasehold",
          "typeOfTenureDetails" -> "test"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "throw a BAD_REQUEST if tenantsAdditionsDisregardedDetails is greater than max length is submitted" in {
      val res = typeOfTenureController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "typeOfTenureDetails" -> "x" * 2001
        )
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
