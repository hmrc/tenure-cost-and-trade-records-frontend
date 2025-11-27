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
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class LeaseOrAgreementYearsControllerSpec extends TestBaseSpec {

  val mockAboutYourLeaseOrTenureNavigator: AboutYourLeaseOrTenureNavigator = mock[AboutYourLeaseOrTenureNavigator]

  val mockAudit: Audit = mock[Audit]
  def leaseOrAgreementYearsController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  )                    =
    new LeaseOrAgreementYearsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      leaseOrAgreementYearsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  "LeaseOrAgreementYearsController GET /" should {
    "return 200 and HTML with yes data in the session" in {
      val result = leaseOrAgreementYearsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show().url
      )
    }

    "return 200 and HTML with no data in the session" in {
      val controller =
        leaseOrAgreementYearsController(aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOneNo))
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show().url
      )
    }

    "return 200 and HTML with none data in the session" in {
      val controller = leaseOrAgreementYearsController(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = leaseOrAgreementYearsController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url + "#lease-or-agreement-details"
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = leaseOrAgreementYearsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = leaseOrAgreementYearsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "commenceWithinThreeYears"        -> "no",
          "agreedReviewedAlteredThreeYears" -> "no",
          "rentUnderReviewNegotiated"       -> "no"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
