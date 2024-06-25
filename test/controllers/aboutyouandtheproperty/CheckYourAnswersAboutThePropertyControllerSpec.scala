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

package controllers.aboutyouandtheproperty

import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class CheckYourAnswersAboutThePropertyControllerSpec extends TestBaseSpec {

  def checkYourAnswersAboutThePropertyController6010Yes(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  )                                                        = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )
  def checkYourAnswersAboutThePropertyController6010No(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  )                                                        = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )
  def checkYourAnswersAboutThePropertyController6010None() = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6015Yes(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  )                                                        = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = forType6015, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )
  def checkYourAnswersAboutThePropertyController6015No(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  )                                                        = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = forType6015, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )
  def checkYourAnswersAboutThePropertyController6015None() = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = forType6015, aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6030Yes(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  )                                                        = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = forType6030, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )
  def checkYourAnswersAboutThePropertyController6030No(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  )                                                        = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = forType6030, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )
  def checkYourAnswersAboutThePropertyController6030None() = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = forType6030, aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6020(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = forType6020, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6076(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = forType6076, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyControllerNone() = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = "", aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "GET / CheckYourAnswersAboutThePropertyController" should {
    "return 200 6010 about you and the property CYA with tied goods yes in the session" in {
      val result = checkYourAnswersAboutThePropertyController6010Yes().show(fakeRequest)
      status(result) shouldBe Status.OK
    }
    "return HTML" in {
      val result = checkYourAnswersAboutThePropertyController6010Yes().show(fakeRequest)
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.TiedForGoodsDetailsController.show().url
      )
    }
    "return 200 6010 about you and the property CYA with tied goods no in the session" in {
      val result = checkYourAnswersAboutThePropertyController6010No().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show().url
      )
    }
    "return 200 6010 about you and the property CYA with no tied goods in the session" in {
      val result = checkYourAnswersAboutThePropertyController6010None().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 6015 about you and the property CYA with premises license granted yes in the session" in {
      val result = checkYourAnswersAboutThePropertyController6015Yes().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController.show().url
      )
    }
    "return 200 6015 about you and the property CYA with premises license granted no in the session" in {
      val result = checkYourAnswersAboutThePropertyController6015No().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url
      )
    }
    "return 200 6015 about you and the property CYA with no premises license granted in the session" in {
      val result = checkYourAnswersAboutThePropertyController6015None().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 6030 about you and the property CYA with charity question yes in the session" in {
      val result = checkYourAnswersAboutThePropertyController6030Yes().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.TradingActivityController.show().url
      )
    }
    "return 200 6030 about you and the property CYA with charity question no in the session" in {
      val result = checkYourAnswersAboutThePropertyController6030No().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CharityQuestionController.show().url
      )
    }
    "return 200 6030 about you and the property CYA with no charity question in the session" in {
      val result = checkYourAnswersAboutThePropertyController6030None().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 6020 about you and the property CYA with no in the session" in {
      val result = checkYourAnswersAboutThePropertyController6020().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show().url
      )
    }

    "return 200 no about you and the property CYA in the session" in {
      val result = checkYourAnswersAboutThePropertyControllerNone().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.LoginController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersAboutThePropertyController6010Yes().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
