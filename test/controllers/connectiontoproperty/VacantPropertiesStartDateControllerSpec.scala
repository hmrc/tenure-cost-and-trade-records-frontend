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

package controllers.connectiontoproperty

import connectors.Audit
import models.submissions.connectiontoproperty.StillConnectedDetails
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class VacantPropertiesStartDateControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]
  def vacantPropertiesStartDateController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new VacantPropertiesStartDateController(
      stubMessagesControllerComponents(),
      mockAudit,
      connectedToPropertyNavigator,
      vacantPropertiesStartDateView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "VacantPropertiesStartDateController GET /" should {
    "return 200 and HTML with vacant property start date present in session" in {
      val result = vacantPropertiesStartDateController().show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
    }

    "return 200 and HTML with vacant property start date is not present in session" in {
      val controller = vacantPropertiesStartDateController(Some(prefilledStillConnectedDetailsYes))
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
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

  "VacantPropertiesStartDateController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = vacantPropertiesStartDateController().submit()(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted without CYA param" in {
      val res = vacantPropertiesStartDateController().submit()(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "startDateOfVacantProperty.day"   -> "20",
          "startDateOfVacantProperty.month" -> "10",
          "startDateOfVacantProperty.year"  -> "2024"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Redirect when form data submitted with CYA param" in {
      val res = vacantPropertiesStartDateController().submit(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "startDateOfVacantProperty.day"   -> "20",
          "startDateOfVacantProperty.month" -> "10",
          "startDateOfVacantProperty.year"  -> "2024"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "calculateBackLink" should {
    "return back link to CYA page if query param present" in {
      val result = vacantPropertiesStartDateController().show(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      )
    }
    "return back link to is the property vacant page if 'from' query param is not present" in {
      val result = vacantPropertiesStartDateController().show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
    }
  }

}
