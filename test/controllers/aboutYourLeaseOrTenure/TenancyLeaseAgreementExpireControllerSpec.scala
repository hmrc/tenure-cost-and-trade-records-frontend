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
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

import java.time.LocalDate
import java.time.temporal.ChronoUnit.DAYS

class TenancyLeaseAgreementExpireControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def tenancyLeaseAgreementExpireController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) = new TenancyLeaseAgreementExpireController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    tenantsLeaseAgreementExpireView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
    mockSessionRepo
  )

  "TenancyLeaseAgreementExpireController GET /" should {
    "return 200 and HTML with Tenancy Lease Agreement Expire in the session" in {
      val result = tenancyLeaseAgreementExpireController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show().url
      )
    }

    "return 200 and HTML vacant property start date is not present in session" in {
      val controller = tenancyLeaseAgreementExpireController(aboutLeaseOrAgreementPartTwo =
        Some(prefilledAboutLeaseOrAgreementPartTwoNoDate)
      )
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show().url
      )
    }

    "return 200 and HTML when no Tenancy Lease Agreement Expire in the session" in {
      val controller = tenancyLeaseAgreementExpireController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show().url
      )
    }

    "display the page with the fields prefilled in" when {
      "exists within the session" in {
        val result = tenancyLeaseAgreementExpireController().show()(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))
        Option(html.getElementById("tenancyLeaseAgreementExpire.day").`val`()).value   shouldBe "1"
        Option(html.getElementById("tenancyLeaseAgreementExpire.month").`val`()).value shouldBe "6"
        Option(html.getElementById("tenancyLeaseAgreementExpire.year").`val`()).value  shouldBe "2022"
      }
    }

    "TenancyLeaseAgreementExpireController SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = tenancyLeaseAgreementExpireController().submit(
          FakeRequest().withFormUrlEncodedBody(Seq.empty*)
        )
        status(res) shouldBe BAD_REQUEST
      }

      "Redirect when form data rentOpenMarketValue submitted" in {
        val tomorrow = LocalDate.now().plus(1, DAYS)
        val res      = tenancyLeaseAgreementExpireController().submit(
          FakeRequest(POST, "/").withFormUrlEncodedBody(
            "tenancyLeaseAgreementExpire.day"   -> tomorrow.getDayOfMonth.toString,
            "tenancyLeaseAgreementExpire.month" -> tomorrow.getMonthValue.toString,
            "tenancyLeaseAgreementExpire.year"  -> tomorrow.getYear.toString
          )
        )
        status(res) shouldBe SEE_OTHER
      }
    }
  }
}
