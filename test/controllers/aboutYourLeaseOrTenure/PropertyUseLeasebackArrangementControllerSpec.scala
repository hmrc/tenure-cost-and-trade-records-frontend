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
import form.aboutYourLeaseOrTenure.PropertyUseLeasebackArrangementForm.propertyUseLeasebackArrangementForm
import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class PropertyUseLeasebackArrangementControllerSpec extends TestBaseSpec {
  import TestData._

  val mockAudit: Audit = mock[Audit]
  def propertyUseLeasebackAgreementController(
    forType: ForType = FOR6010,
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new PropertyUseLeasebackArrangementController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      propertyUseLeasebackAgreementView,
      preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  "PropertyUseLeasebackArrangementController GET /" should {
    "return 200 and HTML with property use lease back yes in the session" in {
      val result = propertyUseLeasebackAgreementController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show().url
      )
    }

    "return 200 and HTML property use lease back with none in the session" in {
      val controller = propertyUseLeasebackAgreementController(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show().url
      )
    }

    "return 200 and HTML with connection to landlord yes in the session for 6020" in {
      val controller = propertyUseLeasebackAgreementController(forType = FOR6020)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show().url
      )
    }

    "return 200 and HTML with connection to landlord no in the session for 6020" in {
      val controller = propertyUseLeasebackAgreementController(
        forType = FOR6020,
        aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOneNo)
      )
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = propertyUseLeasebackAgreementController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }
  }

  "PropertyUseLeasebackArrangementController POST /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = propertyUseLeasebackAgreementController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = propertyUseLeasebackAgreementController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("propertyUseLeasebackArrangement" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }

    "Trading Name own the property form" should {
      "error if areYouThirdParty is missing" in {
        val formData = baseFormData - propertyUseLeasebackArrangementErrorKey
        val form     = propertyUseLeasebackArrangementForm.bind(formData)

        mustContainError(
          propertyUseLeasebackArrangementErrorKey,
          "error.propertyUseLeasebackArrangement.missing",
          form
        )
      }
    }
  }

  object TestData {
    val propertyUseLeasebackArrangementErrorKey = "propertyUseLeasebackArrangement"

    val baseFormData: Map[String, String] = Map(
      "propertyUseLeasebackArrangement" -> "yes"
    )
  }
}
