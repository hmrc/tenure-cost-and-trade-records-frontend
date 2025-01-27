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
import form.aboutYourLeaseOrTenure.PayACapitalSumForm.payACapitalSumForm
import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartFour, AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class PayACapitalSumControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  val mockAudit: Audit = mock[Audit]

  def payACapitalSumController(
    forType: ForType = FOR6010,
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo),
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    ),
    aboutLeaseOrAgreementPartFour: Option[AboutLeaseOrAgreementPartFour] = Some(prefilledAboutLeaseOrAgreementPartFour)
  ) =
    new PayACapitalSumController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      payACapitalSumView,
      preEnrichedActionRefiner(
        forType = forType,
        aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo,
        aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree,
        aboutLeaseOrAgreementPartFour = aboutLeaseOrAgreementPartFour
      ),
      mockSessionRepo
    )

  "PayACapitalSumController GET /" should {
    "return 200 and HTML with tenant additional disregarded with yes in the session" in {
      val result = payACapitalSumController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController.show().url
      )
    }

    "return 200 and HTML with tenant additional disregarded with no in the session" in {
      val controller =
        payACapitalSumController(aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwoNo))
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
      )
    }

    "return 200 and HTML tenant additional disregarded with none in the session" in {
      val controller = payACapitalSumController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 and HTML with benefit given with yes in the session with 6020" in {
      val controller = payACapitalSumController(FOR6020)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenDetailsController.show().url
      )
    }

    "return 200 and HTML with benefit given with no in the session with 6020" in {
      val controller = payACapitalSumController(
        FOR6020,
        aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThreeNo)
      )
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenController.show().url
      )
    }

    "return 200 and HTML benefit given with none in the session with 6020" in {
      val controller = payACapitalSumController(FOR6020, aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 and HTML back link rent free period details with Yes in the session with 6045" in {
      val controller = payACapitalSumController(FOR6045)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentFreePeriodDetailsController.show().url
      )
    }

    "return 200 and HTML back link rent free period details with none in the session with 6045" in {
      val controller = payACapitalSumController(FOR6045, aboutLeaseOrAgreementPartFour = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsGivenRentFreePeriodController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = payACapitalSumController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url + "#pay-a-capital-sum")
    }
  }

  "PayACapitalSumController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = payACapitalSumController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = payACapitalSumController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("payACapitalSum" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }

    "Redirect when 6020 form Yes answer submitted" in {
      val res = payACapitalSumController(FOR6020).submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("payACapitalSum" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutYourLeaseOrTenure.routes.CapitalSumDescriptionController.show().url
      )
    }

    "Redirect when 6020 form No answer submitted" in {
      val res = payACapitalSumController(FOR6020).submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("payACapitalSum" -> "no")
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url
      )
    }

    "Redirect when 6030 form Yes answer submitted" in {
      val res = payACapitalSumController(FOR6030).submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("payACapitalSum" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumDetailsController.show().url
      )
    }

    "Redirect when 6030 form No answer submitted" in {
      val res = payACapitalSumController(FOR6030).submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("payACapitalSum" -> "no")
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutYourLeaseOrTenure.routes.PaymentWhenLeaseIsGrantedController.show().url
      )
    }

    "Redirect when 6045 form Yes answer submitted" in {
      val res = payACapitalSumController(FOR6045).submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("payACapitalSum" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutYourLeaseOrTenure.routes.CapitalSumDescriptionController.show().url
      )
    }

    "Redirect when 6045 form No answer submitted" in {
      val res = payACapitalSumController(FOR6045).submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("payACapitalSum" -> "no")
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url
      )
    }

  }

  "Pay a capital sum form" should {
    "error if Pay a capital sum answer is missing" in {
      val formData = baseFormData - errorKey.payACapitalSum
      val form     = payACapitalSumForm.bind(formData)

      mustContainError(errorKey.payACapitalSum, "error.payACapitalSum.missing", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val payACapitalSum: String = "payACapitalSum"
    }

    val baseFormData: Map[String, String] = Map("payACapitalSum" -> "yes")
  }
}
