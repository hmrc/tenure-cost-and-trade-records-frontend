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

package controllers.aboutyouandtheproperty

import models.ForType.*
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo}
import models.submissions.common.{AnswerNo, AnswerYes}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class CheckYourAnswersAboutThePropertyControllerSpec extends TestBaseSpec {

  def checkYourAnswersAboutThePropertyController6010Yes(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6010No(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new CheckYourAnswersAboutThePropertyController(
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
  ) = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = FOR6015, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6015No(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = FOR6015, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6015None() = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = FOR6015, aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6030Yes(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = FOR6030, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6030No(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = FOR6030, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6030None() = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = FOR6030, aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6020(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = FOR6020, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyController6076(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = FOR6076, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyControllerYes6045(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = FOR6045, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyControllerNo6045(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = FOR6045, aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def checkYourAnswersAboutThePropertyControllerNone() = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    checkYourAnswersAboutThePropertyView,
    preEnrichedActionRefiner(forType = FOR6010, aboutYouAndTheProperty = None),
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

    "return 200 no about you and the property CYA in the session 6076" in {
      val result = checkYourAnswersAboutThePropertyController6076().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.BatteriesCapacityController.show().url
      )
    }

    "return 200 no about you and the property CYA in the session 6045" in {
      val result = checkYourAnswersAboutThePropertyControllerYes6045().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show().url
      )
    }

    "return 200 no about you and the property CYA in the session" in {
      val result = checkYourAnswersAboutThePropertyControllerNone().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      )
    }
    "return correct backLinks for FOR6048" when {
      def controller(
        partTwo: AboutYouAndThePropertyPartTwo,
        isPossibleWelsh: Boolean
      ) = new CheckYourAnswersAboutThePropertyController(
        stubMessagesControllerComponents(),
        aboutYouAndThePropertyNavigator,
        checkYourAnswersAboutThePropertyView,
        preEnrichedActionRefiner(
          forType = FOR6048,
          aboutYouAndThePropertyPartTwo = Option(partTwo),
          isWelsh = isPossibleWelsh
        ),
        mockSessionRepo
      )

      "canProceed is false and isWelsh is true" in {
        val partTwo = prefilledAboutYouAndThePropertyPartTwo6048.copy(canProceed = Option(false))
        val result  = controller(partTwo, isPossibleWelsh = true).show(fakeRequest)

        status(result)        shouldBe Status.OK
        contentAsString(result) should include(
          controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsWelshController.show().url
        )
      }

      "canProceed is false and isWelsh is false" in {
        val partTwo = prefilledAboutYouAndThePropertyPartTwo6048.copy(canProceed = Option(false))
        val result  = controller(partTwo, isPossibleWelsh = false).show(fakeRequest)

        status(result)        shouldBe Status.OK
        contentAsString(result) should include(
          controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsController.show().url
        )
      }

      "some occupiers - family members in the property" in {
        val partTwo = prefilledAboutYouAndThePropertyPartTwo6048.copy(
          canProceed = Option(true),
          partsUnavailable = Option(AnswerYes)
        )
        val result  = controller(partTwo, isPossibleWelsh = false).show(fakeRequest)

        status(result)        shouldBe Status.OK
        contentAsString(result) should include(
          controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(0).url
        )
      }
      "no occupiers  - family members in the property" in {
        val partTwo = prefilledAboutYouAndThePropertyPartTwo6048.copy(
          canProceed = Option(true),
          partsUnavailable = Option(AnswerNo)
        )
        val result  = controller(partTwo, isPossibleWelsh = false).show(fakeRequest)

        status(result)        shouldBe Status.OK
        contentAsString(result) should include(
          controllers.aboutyouandtheproperty.routes.PartsUnavailableController.show().url
        )
      }

    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = checkYourAnswersAboutThePropertyController6010Yes().submit(
          FakeRequest().withFormUrlEncodedBody(Seq.empty*)
        )
        status(res) shouldBe BAD_REQUEST
      }
    }
  }
}
