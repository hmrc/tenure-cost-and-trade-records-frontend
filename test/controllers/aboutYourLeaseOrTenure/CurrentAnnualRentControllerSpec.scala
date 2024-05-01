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

import models.ForTypes
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class CurrentAnnualRentControllerSpec extends TestBaseSpec {

  def connectionToThePropertyController = new CurrentAnnualRentController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      currentAnnualRentView,
      preEnrichedActionRefiner(),
      mockSessionRepo
    )

  def connectionToThePropertyController6011Yes = new CurrentAnnualRentController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      currentAnnualRentView,
      preEnrichedActionRefiner(forType = ForTypes.for6011),
      mockSessionRepo
    )

  def connectionToThePropertyController6011No = new CurrentAnnualRentController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      currentAnnualRentView,
      preEnrichedActionRefiner(forType = ForTypes.for6011, aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOneNo)),
      mockSessionRepo
    )

  def connectionToThePropertyControllerNone = new CurrentAnnualRentController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    currentAnnualRentView,
    preEnrichedActionRefiner(forType = ForTypes.for6011, aboutLeaseOrAgreementPartOne = None),
    mockSessionRepo
  )

  "CurrentAnnualRentController GET /" should {
    "return 200 and HTML with data in the session" in {
      val result = connectionToThePropertyController.show(fakeRequest)
      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show().url
      )
    }

    "return 200 and HTML for 6011 with yes in the session" in {
      val result = connectionToThePropertyController6011Yes.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show().url
      )
    }

    "return 200 and HTML for 6011 with no in the session" in {
      val result = connectionToThePropertyController6011No.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show().url
      )
    }

    "return 200 and HTML with none in the session" in {
      val result = connectionToThePropertyControllerNone.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }
  }

  "CurrentAnnualRentController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = connectionToThePropertyController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
