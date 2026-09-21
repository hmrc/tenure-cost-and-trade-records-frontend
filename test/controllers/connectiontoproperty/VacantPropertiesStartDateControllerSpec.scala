/*
 * Copyright 2026 HM Revenue & Customs
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
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import test.ControllerSpec

class VacantPropertiesStartDateControllerSpec extends ControllerSpec:

  val mockAudit: Audit = mock[Audit]

  def vacantPropertiesStartDateController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ): VacantPropertiesStartDateController =
    VacantPropertiesStartDateController(
      stubMessagesControllerComponents(),
      mockAudit,
      connectedToPropertyNavigator,
      vacantPropertiesStartDateView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepository
    )

  "GET /" should {
    "return 200 and HTML with vacant property start date present in session" in {
      val result = vacantPropertiesStartDateController().show()(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
    }

    "return 200 and HTML with vacant property start date is not present in session" in {
      val controller = vacantPropertiesStartDateController(Some(prefilledStillConnectedDetailsYes))
      val result     = controller.show()(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
    }

    "display the page with the fields prefilled in when exists within the session" in {
      val result = vacantPropertiesStartDateController().show()(getRequest)
      val html   = Jsoup.parse(contentAsString(result))
      Option(html.getElementById("startDateOfVacantProperty.day").`val`()).get   shouldBe "1"
      Option(html.getElementById("startDateOfVacantProperty.month").`val`()).get shouldBe "6"
      Option(html.getElementById("startDateOfVacantProperty.year").`val`()).get  shouldBe "2022"
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = vacantPropertiesStartDateController().submit()(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "redirect when form data submitted without CYA param" in {
      val res = vacantPropertiesStartDateController().submit()(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "startDateOfVacantProperty.day"   -> "20",
          "startDateOfVacantProperty.month" -> "10",
          "startDateOfVacantProperty.year"  -> "2024"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "redirect when form data submitted with CYA param" in {
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
      val result = vacantPropertiesStartDateController().show(getRequestFromCYA)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      )
    }

    "return back link to is the property vacant page if 'from' query param is not present" in {
      val result = vacantPropertiesStartDateController().show(getRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
    }
  }
