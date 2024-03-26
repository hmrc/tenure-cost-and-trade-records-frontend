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
import navigation.ConnectionToPropertyNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class VacantPropertiesControllerSpec extends TestBaseSpec {

  val vacantPropertiesNavigator = mock[ConnectionToPropertyNavigator]

  def vacantPropertiesController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledVacantProperties)
  ) =
    new VacantPropertiesController(
      stubMessagesControllerComponents(),
      vacantPropertiesNavigator,
      vacantPropertiesView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  def vacantPropertiesControllerNoVacantProperty(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsEdit)
  ) =
    new VacantPropertiesController(
      stubMessagesControllerComponents(),
      vacantPropertiesNavigator,
      vacantPropertiesView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "GET /" should {
    "return 200 when vacant property is present in session" in {
      val result = vacantPropertiesController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = vacantPropertiesController().show(fakeRequest)
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
      )
    }

    "return 200 when vacant property is not present in session" in {
      val result = vacantPropertiesControllerNoVacantProperty().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.EditAddressController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = vacantPropertiesController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
