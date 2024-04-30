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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class TenancyLeaseAgreementExpireControllerSpec extends TestBaseSpec {

  def tenancyLeaseAgreementExpireController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new TenancyLeaseAgreementExpireController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      tenantsLeaseAgreementExpireView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  def tenancyLeaseAgreementExpireNoStartDate(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(
      prefilledAboutLeaseOrAgreementPartTwoNoDate
    )
  ) =
    new TenancyLeaseAgreementExpireController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      tenantsLeaseAgreementExpireView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  def tenancyLeaseAgreementExpireControllerNone =
    new TenancyLeaseAgreementExpireController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      tenantsLeaseAgreementExpireView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = None),
      mockSessionRepo
    )

  "TenancyLeaseAgreementExpireController GET /" should {
    "return 200 and HTML with Tenancy Lease Agreement Expire in the session" in {
      val result = tenancyLeaseAgreementExpireController().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML vacant property start date is not present in session" in {
      val result = tenancyLeaseAgreementExpireNoStartDate().show()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when no Tenancy Lease Agreement Expire in the session" in {
      val result = tenancyLeaseAgreementExpireControllerNone.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
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

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = tenancyLeaseAgreementExpireController().submit(
          FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
        )
        status(res) shouldBe BAD_REQUEST
      }
    }
  }
}
