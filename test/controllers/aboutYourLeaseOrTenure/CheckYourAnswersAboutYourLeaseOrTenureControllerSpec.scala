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

import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class CheckYourAnswersAboutYourLeaseOrTenureControllerSpec extends TestBaseSpec {

  def cYAAboutYourLeaseOrTenureController = new CheckYourAnswersAboutYourLeaseOrTenureController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    checkYourAnswersAboutLeaseAndTenureView,
    preEnrichedActionRefiner(),
    mockSessionRepo
  )

  def cYAAboutYourLeaseOrTenureControllerOneNone = new CheckYourAnswersAboutYourLeaseOrTenureController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    checkYourAnswersAboutLeaseAndTenureView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = None),
    mockSessionRepo
  )

  def cYAAboutYourLeaseOrTenureControllerLegalPlanNo = new CheckYourAnswersAboutYourLeaseOrTenureController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    checkYourAnswersAboutLeaseAndTenureView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwoNo)),
    mockSessionRepo
  )

  def cYAAboutYourLeaseOrTenureControllerLeaseAgreeNo = new CheckYourAnswersAboutYourLeaseOrTenureController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    checkYourAnswersAboutLeaseAndTenureView,
    preEnrichedActionRefiner(
      aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOneNo),
      aboutLeaseOrAgreementPartTwo = None
    ),
    mockSessionRepo
  )

  def cYAAboutYourLeaseOrTenureControllerTwoNone = new CheckYourAnswersAboutYourLeaseOrTenureController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    checkYourAnswersAboutLeaseAndTenureView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = None),
    mockSessionRepo
  )

  def cYAAboutYourLeaseOrTenureControllerTwoNone6011 = new CheckYourAnswersAboutYourLeaseOrTenureController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    checkYourAnswersAboutLeaseAndTenureView,
    preEnrichedActionRefiner(forType = "FOR6011", aboutLeaseOrAgreementPartTwo = None),
    mockSessionRepo
  )

  def cYAAboutYourLeaseOrTenureControllerTwoNoneOtherForms = new CheckYourAnswersAboutYourLeaseOrTenureController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    checkYourAnswersAboutLeaseAndTenureView,
    preEnrichedActionRefiner(forType = "FOR6020", aboutLeaseOrAgreementPartTwo = None),
    mockSessionRepo
  )

  "CheckYourAnswersAboutYourLeaseOrTenureController GET /" should {
    "return 200 and HTML with CYA and Legal Planning Restrictions (Yes) in the session" in {
      val result = cYAAboutYourLeaseOrTenureController.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsDetailsController.show().url
      )
    }

    "return 200 and HTML when no CYA in the session" in {
      val result = cYAAboutYourLeaseOrTenureControllerOneNone.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsDetailsController.show().url
      )
    }

    "return 200 and HTML with CYA and Legal Planning Restrictions (No) in the session" in {
      val result = cYAAboutYourLeaseOrTenureControllerLegalPlanNo.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url
      )
    }

    "return 200 and HTML with CYA and when no Legal Planning Restrictions and Lease Agreement all No in the session for 6010" in {
      val result = cYAAboutYourLeaseOrTenureControllerLeaseAgreeNo.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentRentPayableWithin12MonthsController.show().url
      )
    }

    "return 200 and HTML with CYA and when no Legal Planning Restrictions in the session for 6010" in {
      val result = cYAAboutYourLeaseOrTenureControllerTwoNone.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show().url
      )
    }

    "return 200 and HTML with CYA and when no Legal Planning Restrictions (Yes) in the session for 6011" in {
      val result = cYAAboutYourLeaseOrTenureControllerTwoNone6011.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController.show().url
      )
    }

    "return exception with CYA and when no Legal Planning Restrictions in the session for other forms" in {
      val result = cYAAboutYourLeaseOrTenureControllerTwoNoneOtherForms.show(fakeRequest)
      result.failed.recover { case e: Exception =>
        e.getMessage shouldBe "Navigation for CYA about lease without correct selection of conditions by controller"
      }
    }
  }

  "CheckYourAnswersAboutYourLeaseOrTenureController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = cYAAboutYourLeaseOrTenureController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
