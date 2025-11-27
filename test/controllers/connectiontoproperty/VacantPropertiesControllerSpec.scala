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

package controllers.connectiontoproperty

import connectors.Audit
import models.submissions.connectiontoproperty.AddressConnectionType.*
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class VacantPropertiesControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]
  def vacantPropertiesController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledVacantProperties)
  )                    =
    new VacantPropertiesController(
      stubMessagesControllerComponents(),
      mockAudit,
      connectedToPropertyNavigator,
      vacantPropertiesView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "VacantPropertiesController GET /" should {
    "return 200 and HTML with vacant property is present in session" in {
      val result = vacantPropertiesController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
      )
    }

    "return 200 and HTML with vacant property is not present in session" in {
      val controller = vacantPropertiesController(Some(prefilledStillConnectedDetailsEdit))
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.EditAddressController.show().url
      )
    }
  }

  "calculateBackLink" should {

    "return back link to CYA page when 'from=CYA' query param is present for vacant properties" in {
      val result = vacantPropertiesController(
        stillConnectedDetails = Some(prefilledStillConnectedVacantYes)
      ).show(fakeRequestFromCYA)

      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      )
    }

    "return back link to CYA page when 'from=CYA' query param is present for not vacant properties" in {
      val result = vacantPropertiesController(
        stillConnectedDetails = Some(prefilledStillConnectedVacantNo)
      ).show(fakeRequestFromCYA)

      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url
      )
    }

    "return back link to Task list page when 'from=TL' query param is present for not vacant properties" in {
      val result = vacantPropertiesController().show(fakeRequestFromTL)
      contentAsString(result) should include(controllers.routes.TaskListController.show().url + "#vacant-properties")
    }

    "return Edit Address page URL when addressConnectionType is AddressConnectionTypeYesChangeAddress and 'from' is not set" in {

      val prefilledStillConnectedChangedAddress: StillConnectedDetails = prefilledStillConnectedDetailsYes.copy(
        addressConnectionType = Some(AddressConnectionTypeYesChangeAddress)
      )

      val result = vacantPropertiesController(
        stillConnectedDetails = Some(prefilledStillConnectedChangedAddress)
      ).show(fakeRequest)

      contentAsString(result) should include(controllers.connectiontoproperty.routes.EditAddressController.show().url)
    }

    "return Are You Still Connected page URL when addressConnectionType is not AddressConnectionTypeYesChangeAddress and 'from' is not set" in {
      val result = vacantPropertiesController(
        stillConnectedDetails = Some(prefilledStillConnectedDetailsYes)
      ).show(fakeRequest)

      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
      )
    }

  }
  "VacantPropertiesController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = vacantPropertiesController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = vacantPropertiesController().submit(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "vacantProperties" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
