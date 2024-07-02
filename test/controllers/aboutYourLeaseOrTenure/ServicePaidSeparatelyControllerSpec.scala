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

import actions.SessionRequest
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import navigation.AboutYourLeaseOrTenureNavigator
import org.jsoup.Jsoup
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class ServicePaidSeparatelyControllerSpec extends TestBaseSpec {

  def servicePaidSeparatelyController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new ServicePaidSeparatelyController(
      stubMessagesControllerComponents(),
      app.injector.instanceOf[AboutYourLeaseOrTenureNavigator],
      servicePaidSeparatelyView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  "Service paid separately controller" should {
    "return 200 and HTML with Services Paid Separately in the session" in {
      val result = servicePaidSeparatelyController().show(None)(fakeRequest)
      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render a page with an empty form" when {
      "not given an index" in {
        val result = servicePaidSeparatelyController().show(None)(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))

        Option(html.getElementById("description").`val`()).value shouldBe ""
      }

      "given an index" which {
        "doesn't already exist in the session" in {
          val result = servicePaidSeparatelyController().show(Some(2))(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))

          Option(html.getElementById("description").`val`()).value shouldBe ""

        }
      }
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = servicePaidSeparatelyController().submit(None)(
          FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
        )
        status(res) shouldBe BAD_REQUEST
      }
    }

    "getBackLink" should {

      "return the correct backLink" in {
        val controller = servicePaidSeparatelyController()
        val result     = controller.getBackLink(SessionRequest(stillConnectedDetails6030NoSession, fakeRequest), 1)
        result shouldBe controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show().url
      }
      "return the correct backLink if view was accessed via 'Change' link" in {
        val controller        = servicePaidSeparatelyController()
        val requestWithChange = requestWithQueryParam(fakeRequest, "from=Change")
        val result            = controller.getBackLink(SessionRequest(stillConnectedDetails6030NoSession, requestWithChange), 1)
        result shouldBe controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.show(1).url
      }

    }
  }
}
