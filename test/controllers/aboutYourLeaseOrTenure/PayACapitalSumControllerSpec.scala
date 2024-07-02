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

import form.aboutYourLeaseOrTenure.PayACapitalSumForm.payACapitalSumForm
import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class PayACapitalSumControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._
  def payACapitalSumController(
    forType: String = ForTypes.for6010,
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo),
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new PayACapitalSumController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      payACapitalSumView,
      preEnrichedActionRefiner(
        forType = forType,
        aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo,
        aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree
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
      val controller = payACapitalSumController(ForTypes.for6020)
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
        ForTypes.for6020,
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
      val controller = payACapitalSumController(ForTypes.for6020, aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
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
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
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
    val errorKey: Object {
      val payACapitalSum: String
    } = new {
      val payACapitalSum: String = "payACapitalSum"
    }

    val baseFormData: Map[String, String] = Map("payACapitalSum" -> "yes")
  }
}
