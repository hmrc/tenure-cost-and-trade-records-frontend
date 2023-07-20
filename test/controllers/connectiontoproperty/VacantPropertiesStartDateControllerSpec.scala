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

package controllers.connectiontoproperty

import models.submissions.connectiontoproperty.StillConnectedDetails
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class VacantPropertiesStartDateControllerSpec extends TestBaseSpec {

  def vacantPropertiesStartDateController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new VacantPropertiesStartDateController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      vacantPropertiesStartDateView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = vacantPropertiesStartDateController().show()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = vacantPropertiesStartDateController().show()(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "display the page with the fields prefilled in" when {
      "exists within the session" in {
        val result = vacantPropertiesStartDateController().show()(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))
        Option(html.getElementById("startDateOfVacantProperty.day").`val`()).value   shouldBe "1"
        Option(html.getElementById("startDateOfVacantProperty.month").`val`()).value shouldBe "6"
        Option(html.getElementById("startDateOfVacantProperty.year").`val`()).value  shouldBe "2022"
      }
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = vacantPropertiesStartDateController().submit()(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
