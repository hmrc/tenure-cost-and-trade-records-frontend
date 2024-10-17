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

import form.aboutYourLeaseOrTenure.UltimatelyResponsibleOutsideRepairsForm.ultimatelyResponsibleOutsideRepairsForm
import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartFour, AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartTwo}
import play.api.http.Status
import utils.TestBaseSpec

import scala.language.reflectiveCalls
import play.api.test.*
import play.api.test.Helpers.*

class UltimatelyResponsibleOutsideRepairsControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def ultimatelyResponsibleOutsideRepairsController(
    forType: ForType = FOR6010,
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne),
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo),
    aboutLeaseOrAgreementPartFour: Option[AboutLeaseOrAgreementPartFour] = Some(prefilledAboutLeaseOrAgreementPartFour)
  ) = new UltimatelyResponsibleOutsideRepairsController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    ultimatelyResponsibleOutsideRepairsView,
    preEnrichedActionRefiner(
      forType = forType,
      aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne,
      aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo,
      aboutLeaseOrAgreementPartFour = aboutLeaseOrAgreementPartFour
    ),
    mockSessionRepo
  )

  "UltimatelyResponsibleOutsideRepairsController GET /" should {
    "return 200 and HTML with Ultimately Responsible Outside Repairs in the session" in {
      val result = ultimatelyResponsibleOutsideRepairsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.DoesTheRentPayableController.show().url
      )
    }

    "return 200 and HTML with Ultimately Responsible Outside Repairs 6020 in the session" in {
      val controller = ultimatelyResponsibleOutsideRepairsController(forType = FOR6020)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsVATPayableForWholePropertyController.show().url
      )
    }

    "return 200 and HTML when no Ultimately Responsible Outside Repairs in the session" in {
      val controller = ultimatelyResponsibleOutsideRepairsController(FOR6020, None, None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show().url
      )
    }

    "return 200 and HTML with Ultimately Responsible Outside Repairs for 6045 in the session" in {
      val controller = ultimatelyResponsibleOutsideRepairsController(forType = FOR6045)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsDetailsController.show().url
      )
    }

    "return 200 and HTML with Ultimately Responsible Outside Repairs for 6045 no structure build details in the session" in {
      val controller = ultimatelyResponsibleOutsideRepairsController(FOR6045, None, None, None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = ultimatelyResponsibleOutsideRepairsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = ultimatelyResponsibleOutsideRepairsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("outsideRepairs" -> "landlord")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Ultimately Responsible OR form" should {
    "error if Ultimately Responsible OR is missing" in {
      val formData = baseFormData - errorKey.ultimatelyResponsibleOR
      val form     = ultimatelyResponsibleOutsideRepairsForm.bind(formData)

      mustContainError(errorKey.ultimatelyResponsibleOR, "error.outsideRepairs.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val ultimatelyResponsibleOR: String = "outsideRepairs"
    }

    val baseFormData: Map[String, String] = Map("outsideRepairs" -> "tenant")
  }
}
