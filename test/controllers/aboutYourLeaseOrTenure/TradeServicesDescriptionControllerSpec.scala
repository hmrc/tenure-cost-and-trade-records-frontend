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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import navigation.AboutYourLeaseOrTenureNavigator
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class TradeServicesDescriptionControllerSpec extends TestBaseSpec {

  def tradeServicesDescriptionController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new TradeServicesDescriptionController(
      stubMessagesControllerComponents(),
      app.injector.instanceOf[AboutYourLeaseOrTenureNavigator],
      tradeServicesDescriptionView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  "Trade services description controller" should {
    "return 200" in {
      val result = tradeServicesDescriptionController().show(None)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = tradeServicesDescriptionController().show(None)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render a page with an empty form" when {
      "not given an index" in {
        val result = tradeServicesDescriptionController().show(None)(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))

        Option(html.getElementById("sumExcludingVat").`val`()).value shouldBe ""
        Option(html.getElementById("description").`val`()).value     shouldBe ""
      }

      "given an index" which {
        "doesn't already exist in the session" in {
          val result = tradeServicesDescriptionController().show(Some(2))(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))

          Option(html.getElementById("sumExcludingVat").`val`()).value shouldBe ""
          Option(html.getElementById("description").`val`()).value     shouldBe ""

        }
      }
    }
    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = tradeServicesDescriptionController().submit(None)(
          FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
        )
        status(res) shouldBe BAD_REQUEST
      }
    }
  }
}
