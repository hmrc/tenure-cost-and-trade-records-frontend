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
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class TradeServicesDescriptionControllerSpec extends TestBaseSpec {

  def tradeServicesDescriptionController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new TradeServicesDescriptionController(
      stubMessagesControllerComponents(),
      inject[AboutYourLeaseOrTenureNavigator],
      tradeServicesDescriptionView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  "Trade services description controller" should {
    "return 200 and HTML with Trade Services Description in the session" in {
      val result = tradeServicesDescriptionController().show(Some(0))(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML Trade Services Description with none in the session" in {
      val controller = tradeServicesDescriptionController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(None)(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render a page with an empty form" when {
      "not given an index" in {
        val result = tradeServicesDescriptionController().show(None)(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))

        Option(html.getElementById("description").`val`()).value shouldBe ""
      }

      "given an index" which {
        "doesn't already exist in the session" in {
          val result = tradeServicesDescriptionController().show(Some(2))(fakeRequest)
          val html   = Jsoup.parse(contentAsString(result))

          Option(html.getElementById("description").`val`()).value shouldBe ""

        }
      }
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = tradeServicesDescriptionController().submit(None)(
          FakeRequest().withFormUrlEncodedBody(Seq.empty*)
        )
        status(res) shouldBe BAD_REQUEST
      }

      "Redirect when form data submitted" in {
        val res = tradeServicesDescriptionController().submit(Some(0))(
          FakeRequest(POST, "/").withFormUrlEncodedBody("description" -> "Description")
        )
        status(res) shouldBe SEE_OTHER
      }

      "throw a BAD_REQUEST if description is greater than max length is submitted" in {
        val res = tradeServicesDescriptionController().submit(Some(0))(
          FakeRequest(POST, "/").withFormUrlEncodedBody(
            "description" -> "x" * 501
          )
        )
        status(res) shouldBe BAD_REQUEST
      }
    }

    "getBackLink" should {

      "return the correct backLink" in {
        val controller = tradeServicesDescriptionController()
        val result     = controller.getBackLink(SessionRequest(stillConnectedDetails6030NoSession, fakeRequest), 1)
        result shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
      }
      "return the correct backLink if view was accessed via 'Change' link" in {
        val controller        = tradeServicesDescriptionController()
        val requestWithChange = requestWithQueryParam(fakeRequest, "from=Change")
        val result            = controller.getBackLink(SessionRequest(stillConnectedDetails6030NoSession, requestWithChange), 1)
        result shouldBe controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(1).url
      }

    }
  }
}
